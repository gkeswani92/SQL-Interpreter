package logical_operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import operators.Operator;
import statistics.AttributeSelectionStatistics;
import statistics.AttributeStatistics;
import statistics.TableStatistics;
import union_find.UnionFind;
import union_find.UnionFindElement;
import utils.DatabaseCatalog;
import utils.RelationSubsetComparator;

public class JoinLogicalOperator extends LogicalOperator {
	
	private Map<String, LogicalOperator> children;
	private List<String> relationsToBeJoined;
	private List<Expression> joinConditions;
	private UnionFind unionFind;
	private Map<String, Double> v_values;
	private DatabaseCatalog dbCatalog;
	
	public JoinLogicalOperator(List<Expression> joinConditions, Map<String, LogicalOperator> children,
					UnionFind unionFind) {
		this.children = new LinkedHashMap<String, LogicalOperator>();
		this.children.putAll(children);
		this.joinConditions = new ArrayList<Expression>();
		this.joinConditions.addAll(joinConditions);
		this.unionFind = unionFind;
		
		dbCatalog = DatabaseCatalog.getInstance();
		relationsToBeJoined = new ArrayList<String>();
		relationsToBeJoined.addAll(children.keySet());
		v_values = new HashMap<String, Double>(); 
		computeVValuesForChildren();
	}
	
	/**
	 * Computes V values for each of the attributes of all the relations being joined
	 * @return
	 */
	public Map<String, Double> computeVValuesForChildren(){
		
		for(String relation: relationsToBeJoined) {
			
			//Getting the child and the statistics for the relation 
			LogicalOperator child = children.get(relation);
			TableStatistics tableStats = DatabaseCatalog.getInstance().getStatistics(relation);
			String[] attributes = dbCatalog.getTableAttributes(relation);
			
			//If the child is a scan operator, the V value of each attribute depends
			//on their own min and max value
			if(child instanceof ScanLogicalOperator){
				for(String attribute: attributes) {
					AttributeStatistics currentAttrStats = tableStats.getAttributeStatistics(attribute);
					v_values.put(relation + "." + attribute, currentAttrStats.maximum - currentAttrStats.minimum + 1.0);
				}
			} 
			
			//For selection, V(R,A) is num tuples * reduction factor of all selection attributes
			//if A appears in the selection condition. 
			//For other attributes like B that do not come in the selection condition, we take 
			//min(V(R,B), V(R,A))
			else {
				Map<String, AttributeSelectionStatistics> attrSelectionStats = ((SelectLogicalOperator) child).getCurrentAttributeStatistics();
				Integer tupleCount = dbCatalog.getStatistics(relation).count;	
				List<String> selectionAttributes = new ArrayList<String>();
				
				//Every attribute that has a selection on it has a V value that has 
				//to be a cumulative reduction factor of all attributes that have
				//a selection condition on them.
				//ALL ATTRIBUTES IN THE SELECTION CONDITION WILL HAVE THE SAME V VALUE
				for(String attribute: attributes) {
					Double cumulativeReductionFactor = 1.0;
					if(attrSelectionStats.containsKey(attribute)){
						selectionAttributes.add(relation + "." + attribute);
						
						for(String attr: attributes) {
							if(attrSelectionStats.containsKey(attr)) {
								cumulativeReductionFactor *= attrSelectionStats.get(attr).getReductionFactor();
							}
						}
						v_values.put(relation + "." + attribute, tupleCount * cumulativeReductionFactor);
					} 
				}
				
				//Computing V values for attributes that did not appear in the selection condition.
				//It will be the min of their own V value from their min and max range values and
				//the V value of attributes that have a selection condition on them
				for(String attribute: attributes){
					if(!selectionAttributes.contains(relation + "." + attribute)){
						AttributeStatistics currentAttrStats = tableStats.getAttributeStatistics(attribute);
						Double vValueFromRange = currentAttrStats.maximum - currentAttrStats.minimum + 1.0;
						Double vValueFromSelectionCondition = v_values.get(selectionAttributes.get(0));
						if(vValueFromRange < vValueFromSelectionCondition) {
							v_values.put(relation + "." + attribute, vValueFromRange);
						} else {
							v_values.put(relation + "." + attribute, vValueFromSelectionCondition);
						}
					}
				}
			} 
		}
		return v_values;
	}
	
	@Override
	public Operator getNextPhysicalOperator() {
		
		// Map of relation name to the corresponding physical child operator
		Map<String, Operator> physicalChildren = new LinkedHashMap<String, Operator>();
		for(String tableName: children.keySet()){
			LogicalOperator currentChild = children.get(tableName);
			physicalChildren.put(tableName, currentChild.getNextPhysicalOperator());
		}
		
		List<RelationSubset> relationSubsets =  new ArrayList<RelationSubset>();
		List<String> initialRelations = new ArrayList<String>();
		
		//Initialising the map to contain one relation plans
		for(String tableName: physicalChildren.keySet()) {
			RelationSubset currentSubset = new RelationSubset(tableName);
			relationSubsets.add(currentSubset);
			initialRelations.add(tableName);
		}
		
		//Find the best join plan for the given relations
		findBestJoinPlan(relationSubsets, initialRelations);
		return null;
	}
	
	/**
	 * Finds the best left deep join plan for the children of the logical join
	 * @param relationSubsets
	 * @param tableNames
	 */
	public void findBestJoinPlan(List<RelationSubset> relationSubsets, List<String> tableNames) {
		
		// Base case: When all plans have the size of the initial number of relations
		// Select the plan with the minimum cost
		if(tableNames.size() == relationSubsets.get(0).getRelations().size()){
			System.out.println("We have computed all possible plans. Now checking for the best plan");
			return;
		}
		
		List<RelationSubset> newSubsets = new ArrayList<RelationSubset>();
		
		for(RelationSubset currentSubset: relationSubsets){
			List<String> relationAdditions = currentSubset.findAddableRelations(tableNames);
			List<RelationSubset> relationAdditionSubsets = new ArrayList<RelationSubset>();
			
			//Find all possible relation subsets that can be created using the current relation subset
			for(String relation: relationAdditions) {
				RelationSubset currentPossibleSubset = new RelationSubset(currentSubset.getRelations(), relation);
				relationAdditionSubsets.add(currentPossibleSubset);
				
				//Computing the cost of the current plan
				Double planCost = computeCostOfPlan(currentPossibleSubset, currentSubset, relation);
				currentPossibleSubset.setPlanCost(planCost);
				
				//Computing the size of the current plan
				Double size = computeSizeOfPlan(currentPossibleSubset, currentSubset, relation);
				currentPossibleSubset.setSize(size);
			}	
			
			//Finding the best plans for the current subset and keeping them for the next iteration
			List<RelationSubset> bestAddableRelationSubsets = findBestAddableRelationSubset(relationAdditionSubsets);
			newSubsets.addAll(bestAddableRelationSubsets);
		}
		
		findBestJoinPlan(newSubsets, tableNames);
	}
	
	/**
	 * Computes and sets the cost of the current subset by using the cost and the
	 * size of the parent subset
	 * @param currentSubset
	 * @param parentSubset
	 * @return 
	 */
	public Double computeCostOfPlan(RelationSubset currentSubset, RelationSubset parentSubset, String newRelation){
		
		if(currentSubset.getRelations().size() == 2) {
			return 0.0;
		}
			
		Double planCost = parentSubset.getPlanCost() + parentSubset.getSize();
		return planCost;
	}
	
	/**
	 * Computes and sets the cost of the current subset by using the cost and the
	 * size of the parent subset
	 * @param currentSubset
	 * @param parentSubset
	 * @return 
	 */
	public Double computeSizeOfPlan(RelationSubset currentSubset, RelationSubset parentSubset, String newRelation){
		
		Double size = 0.0;
		
		//If this is a base table, size of join of is 0
		if(currentSubset.getRelations().size() == 1) {
			return size;
		}
	
		List<String> leftRelations = parentSubset.getRelations();
		String rightRelation = newRelation;
		Integer numTuplesRight = dbCatalog.getStatistics(rightRelation).count;
		
		List<List<Column>> joinConditions = unionFind.getJoinAttributes(leftRelations, rightRelation);
		currentSubset.setJoinConditions(joinConditions);
		
		//If the current subset has two tables, the V values of the children needs
		//to be used directly
		if(currentSubset.getRelations().size() == 2) {
			Integer numTuplesLeft = dbCatalog.getStatistics(leftRelations.get(0)).count;
			
			
			//If there are no join condition, the size is the cross product
			if(joinConditions.size() > 0) {
				Double denominator = 1.0;
			
				//For multiple join conditions, the denominator needs to be a product of 
				//max v values for each join condition
				for(List<Column> joinCondition: joinConditions){
					Double vValueLeft = v_values.get(joinCondition.get(0).toString());
					Double vValueRight = v_values.get(joinCondition.get(1).toString());
					denominator *= Math.max(vValueLeft,vValueRight);
				}
				size = (numTuplesLeft * numTuplesRight) / denominator;
			} 
		}
		
		//For joins which contains more than 2 tables on the left, we need to
		//find the appropriate V value and compute the join size
		else {
			Double numTuplesLeft = parentSubset.getSize();
			Double denominator = 1.0;
			
			for(List<Column> joinCondition: joinConditions){
				denominator *= computeMaxVValueForJoinCondition(joinCondition, parentSubset, newRelation);
			}
			size = (numTuplesLeft * numTuplesRight * 1.0) / denominator;
		}	
		return size;
	}
	
	/**
	 * For a given join condition and parent subset, finds the appropriate v value to use 
	 * when the left join table itself is a join
	 * @param joinCondition
	 * @param parentSubset
	 * @param newRelation
	 * @return
	 */
	public Double computeMaxVValueForJoinCondition(List<Column> joinCondition, RelationSubset 
							parentSubset, String newRelation){
		
		String currentLeftJoinAttribute = joinCondition.get(0).toString();
		String currentRightJoinAttribute = joinCondition.get(1).toString();
		Double vValueRight = v_values.get(currentRightJoinAttribute);
		
		List<Double> matchingAttributeVValues = new ArrayList<Double>();
		matchingAttributeVValues.add(v_values.get(currentLeftJoinAttribute));
		
		for(List<Column> parentJoinCondition: parentSubset.getJoinConditions()){
			String leftCondition = parentJoinCondition.get(0).toString();
			String rightCondition = parentJoinCondition.get(1).toString();
			
			if(leftCondition.compareTo(currentLeftJoinAttribute) == 0) {
				matchingAttributeVValues.add(v_values.get(rightCondition));
			} else if (rightCondition.compareTo(currentLeftJoinAttribute) == 0){
				matchingAttributeVValues.add(v_values.get(leftCondition));
			}
		}
		
		Double vValueLeft = Collections.min(matchingAttributeVValues);
		Double vValue =  Math.max(vValueLeft,vValueRight);
		return vValue;
	}
	
	
	/**
	 * Finds the minimum cost relation subsets. In case of a tie, all relations subsets with the minimum
	 * cost are returned
	 * @param addableRelationSubsets
	 * @return
	 */
	public List<RelationSubset> findBestAddableRelationSubset(List<RelationSubset> addableRelationSubsets) {
		
		Integer index = 0;
		List<RelationSubset> minCostPlans = new ArrayList<RelationSubset>();
		
		Collections.sort(addableRelationSubsets, new RelationSubsetComparator());
		RelationSubset rs = addableRelationSubsets.get(index++);
		Double minCost = rs.getPlanCost();
		minCostPlans.add(rs);

		//Finding all relation subsets that have the minimum cost
		while (index < addableRelationSubsets.size() ){
			rs = addableRelationSubsets.get(index++);
			if(rs.getPlanCost().equals(minCost)){
				minCostPlans.add(rs);
			} else {
				break;
			}
		}
		return minCostPlans;
	}

	@Override
	public String getLogicalPlanToString(Integer level) {
		String plan = "";
		
		// Level
		if (level > 0) {
			for (int i = 0; i < level; i++) {
				plan = plan + "-";
			}
		}
		
		// Join with Residual join expressions
		plan = plan + "Join" + "[";
		for (Expression exp: joinConditions) {
			plan = plan + exp.toString() + ",";
		}
		plan = plan.substring(0, plan.length()-1);
		if (!joinConditions.isEmpty()) {
			plan = plan + "]";
		}
		
		plan = plan + "\n";
		
		// Union find elements
		for (UnionFindElement ufe: unionFind.getElements()) {
			plan = plan + ufe.toString();
		}
		
		level = level + 1;
		for (String key: children.keySet()) {
			plan = plan + children.get(key).getLogicalPlanToString(level);
		}
		
		plan = plan + "\n";
		
		return plan;
	}
}