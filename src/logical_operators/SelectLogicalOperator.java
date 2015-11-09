package logical_operators;

import java.util.ArrayList;
import java.util.List;

import expression_visitors.IndexExpressionBuilder;
import indexing.Index;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import operators.IndexScanOperator;
import operators.Operator;
import operators.SelectOperator;
import utils.DatabaseCatalog;
import utils.IndexConfigFileReader;

public class SelectLogicalOperator extends LogicalOperator {

	LogicalOperator child;
	Expression whereClause;
	
	public SelectLogicalOperator(Expression exp, LogicalOperator child) {
		this.child = child;
		whereClause = exp;
	}
	
    public Expression getExpression(){
    	return whereClause;
    }

	@Override
	public Operator getNextPhysicalOperator() {
		// If child is a scan, then can possibly use index
		if (child instanceof ScanLogicalOperator) {
			
			// Create an expression visitor to get the lowKey, highKey and select conditions for attributes without index
			String tableName = ((ScanLogicalOperator)child).getTableName();
			List<Expression> selectConditions = new ArrayList<Expression>();
			
			// Alias is either the alias(if there is 1) OR the base tableName(if no aliases exist)
			String alias = tableName;
			
			// If current tableName is an alias, update it to be the base table name so we can find the index
			if (DatabaseCatalog.getInstance().getTableForAlias(tableName) != null) {
				tableName = DatabaseCatalog.getInstance().getTableForAlias(tableName);
			}
			
			Index index = IndexConfigFileReader.getInstance().getAllIndexes().get(tableName);
			
			// All this only happens if an index exists on the table of interest
			if (index != null) {
				IndexExpressionBuilder ieb = new IndexExpressionBuilder(
						index,
						selectConditions);
				whereClause.accept(ieb);
				
				// If either lowKey or highKey is a non null value, the where clause has index usable conditions
				if (!(ieb.getLowKey() == null && ieb.getHighKey() == null)) {
					
					// If selectConditions is non empty, the where clause has both index usable and index non-usable conditions.
					// Create SelectOperator and set child as IndexScanOperator
					if (!selectConditions.isEmpty()) {
						Expression select = createSelectCondition(selectConditions);
						return new SelectOperator(
								new IndexScanOperator(ieb.getLowKey(), ieb.getHighKey(), index, alias), 
								select);
					} else {
						return new IndexScanOperator(ieb.getLowKey(), ieb.getHighKey(), index, alias);
					}
				}
			}
		}
		
		// Create selectOperator for non index usable. Will only come here if no index usable expressions exist.
		return new SelectOperator(child.getNextPhysicalOperator(), whereClause);
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
