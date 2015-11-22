package logical_operators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import expression_visitors.SelectionBoundCalculator;
import indexing.Index;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import operators.Operator;
import operators.SelectOperator;
import statistics.AttributeSelectionStatistics;
import statistics.AttributeStatistics;
import statistics.TableStatistics;
import utils.DatabaseCatalog;
import utils.IndexConfigFileReader;
import utils.PlanBuilderConfigFileReader;

public class SelectLogicalOperator extends LogicalOperator {

	LogicalOperator child;
	Expression whereClause;
	DatabaseCatalog dbCatalog;
	
	public SelectLogicalOperator(Expression exp, LogicalOperator child) {
		this.child = child;
		whereClause = exp;
		dbCatalog = DatabaseCatalog.getInstance();
	}
	
    public Expression getExpression(){
    	return whereClause;
    }

	@Override
	public Operator getNextPhysicalOperator() {
		
		// If child is a scan, then can possibly use index
		if (PlanBuilderConfigFileReader.getInstance().getUseIndexFlag() == 1 && child instanceof ScanLogicalOperator) {
			
			
			String alias = getTableName();
			
			//Attribute statistics for the min, max and reduction factor for the attribute
			Map<String, AttributeSelectionStatistics> currentAttributeStatistics = getAttributeSelectionStatistics(alias);
			
			Map<String, Double> ioCost = getIOCostForIndexes(alias, currentAttributeStatistics);
			
		
			List<Expression> selectConditions = new ArrayList<Expression>();
			List<Index> indexes = IndexConfigFileReader.getInstance().getIndexesByTableName(alias);
			
			
		}
		
		// Create selectOperator for non index usable. Will only come here if no index usable expressions exist.
		return new SelectOperator(child.getNextPhysicalOperator(), whereClause);
	}

	/** 
	 * Returns a map of the attribute to the IO cost of using an index on that attribute
	 * @param alias
	 * @param currentAttributeStatistics
	 * @return
	 */
	private Map<String, Double> getIOCostForIndexes(String alias,
										Map<String, AttributeSelectionStatistics> currentAttributeStatistics) {
		
		//IO cost for using each of the given attributes
		Map<String, Double> ioCost = new HashMap<String, Double>();
		
		for(String attribute: currentAttributeStatistics.keySet()){
			AttributeSelectionStatistics attrStatistics = currentAttributeStatistics.get(attribute);
			Index attrIndex = IndexConfigFileReader.getInstance().getIndexForAttribute(attribute, alias);
			if(attrIndex != null) {
				Integer numAttributes = dbCatalog.getTableAttributes(alias).length;
				Integer numTuples = dbCatalog.getStatistics(alias).count;
				Integer numPages = Math.floorDiv(numTuples, 4088 / (4 * numAttributes));
				Double ioCostAttr = 0.0;
				
				//0 is for unclustered and 1 is for clustered
				if(attrIndex.getFlag() == 0) {
					ioCostAttr = 3 + (numTuples * attrStatistics.getReductionFactor()) + (attrIndex.getNumLeaves() * attrStatistics.getReductionFactor());
				} else {
					ioCostAttr = 3 + (numPages * attrStatistics.getReductionFactor());
				}
				ioCost.put(attribute, ioCostAttr);
			}
		}
		return ioCost;
	}

	/**
	 * Returns a map of the attribute to its selection statistics which contains the reduction factor
	 * @param alias
	 * @return
	 */
	private Map<String, AttributeSelectionStatistics> getAttributeSelectionStatistics(String alias) {
		
		SelectionBoundCalculator sbc = new SelectionBoundCalculator();
		whereClause.accept(sbc);
		
		Map<String, AttributeSelectionStatistics> currentAttributeStatistics = sbc.getAss();
		TableStatistics tableStats = DatabaseCatalog.getInstance().getStatistics(alias);
		
		//Setting the reduction factors for all the attributes
		for(String attribute: currentAttributeStatistics.keySet()) {
			if(IndexConfigFileReader.getInstance().attributeHasIndex(attribute, alias)){
				AttributeSelectionStatistics attrSelectionStats = currentAttributeStatistics.get(attribute);
				AttributeStatistics attrRelationStats = tableStats.getAttributeStatistics(attribute);
				attrSelectionStats.setReductionFactor(attrRelationStats.minimum, attrRelationStats.maximum);
			}
		}
		return currentAttributeStatistics;
	}

	/**
	 * Checks whether there is an alias and gives the respective table name
	 * @return
	 */
	private String getTableName() {
		String tableName = ((ScanLogicalOperator)child).getTableName();

		// Alias is either the alias(if there is 1) OR the base tableName(if no aliases exist)
		String alias = tableName;
		
		// If current tableName is an alias, update it to be the base table name so we can find the index
		if (DatabaseCatalog.getInstance().getTableForAlias(tableName) != null) {
			tableName = DatabaseCatalog.getInstance().getTableForAlias(tableName);
		}
		return alias;
	}
	
	/**
	 * Creates a single select condition by aggregating a list of conditions using AND expression
	 * @param selectConditions
	 * @return
	 */
	public Expression createSelectCondition(List<Expression> selectConditions) {
		Expression returnExp = selectConditions.remove(0);
		
		for (Expression ex: selectConditions) {
			returnExp = new AndExpression(returnExp, ex);
		}
		return returnExp;
	}
}
