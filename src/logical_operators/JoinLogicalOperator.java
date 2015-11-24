package logical_operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.jsqlparser.expression.Expression;
import operators.Operator;
import union_find.UnionFind;
import union_find.UnionFindElement;
import utils.RelationSubsetComparator;

public class JoinLogicalOperator extends LogicalOperator {
	
	private Map<String, LogicalOperator> children;
	private List<Expression> joinConditions;
	private Map<List<String>, Double> planCosts;
	private UnionFind unionFind;
	
	public JoinLogicalOperator(List<Expression> joinConditions, Map<String, LogicalOperator> children,
					UnionFind unionFind) {
		this.children = new LinkedHashMap<String, LogicalOperator>();
		this.children.putAll(children);
		this.joinConditions = new ArrayList<Expression>();
		this.joinConditions.addAll(joinConditions);
		this.unionFind = unionFind;
	}
	
	@Override
	public Operator getNextPhysicalOperator() {
		
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
		
		findBestJoinPlan(relationSubsets, initialRelations);
		return null;
	}
	
	public void findBestJoinPlan(List<RelationSubset> relationSubsets, List<String> tableNames) {
		
		//Base case: When all plans have the size of the initial number of relations
		if(tableNames.size() == relationSubsets.get(0).getRelations().size()){
			System.out.println("We have computed all possible plans. Now checking for the best plan");
		}
		
		List<RelationSubset> subsetsToRemove = new ArrayList<RelationSubset>();
		List<RelationSubset> subsetsToAdd = new ArrayList<RelationSubset>();
		
		for(RelationSubset currentSubset: relationSubsets){
			System.out.println("Finding the best possible plan for "+currentSubset);		
			List<String> addableRelations = currentSubset.findAddableRelations(tableNames);
			List<RelationSubset> addableRelationSubsets = new ArrayList<RelationSubset>();
			
			//Find all possible relation subsets that can be created using the current relation subset
			for(String relation: addableRelations) {
				RelationSubset currentPossibleSubset = new RelationSubset(currentSubset.getRelations(), relation);
				computeCostOfPlan(currentPossibleSubset, currentSubset);
				addableRelationSubsets.add(currentPossibleSubset);
			}	
			
			//Finding the best plans for the current subset and keeping them for the next iteration
			List<RelationSubset> bestAddableRelationSubsets = findBestAddableRelationSubset(addableRelationSubsets);
			subsetsToAdd.addAll(bestAddableRelationSubsets);
			subsetsToRemove.add(currentSubset);	
		}		
		
		relationSubsets.removeAll(subsetsToRemove);
		relationSubsets.addAll(subsetsToAdd);
		findBestJoinPlan(relationSubsets, tableNames);
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
	
	/**
	 * Computes and sets the cost of the current subset by using the cost of the parent subset
	 * and the size of the parent relation
	 * @param currentSubset
	 * @param parentSubset
	 */
	public void computeCostOfPlan(RelationSubset currentSubset, RelationSubset parentSubset){
		currentSubset.setPlanCost(parentSubset.getPlanCost() + getSizeOfParent(parentSubset));
	}
	
	/**
	 * Computes the size of the parent subset relation
	 * @param parentSubset
	 * @return
	 */
	public Double getSizeOfParent(RelationSubset parentSubset) {
		return 123.0;
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
	
//		PlanBuilderConfigFileReader config = PlanBuilderConfigFileReader.getInstance();
//		// Read config file and create appropriate join operator
//		if (config.getJoinType() == 0) {
//			return new TNLJOperator(this.joinCondition, 
//					this.leftChild.getNextPhysicalOperator(), 
//					this.rightChild.getNextPhysicalOperator());
//		} else if (config.getJoinType() == 1) {
//			return new BNLJOperator(this.joinCondition,
//					this.leftChild.getNextPhysicalOperator(), 
//					this.rightChild.getNextPhysicalOperator(), 
//					config.getJoinBuffer());
//		} else {
//			List<String> leftSortConditions = new ArrayList<String>();
//			List<String> rightSortConditions = new ArrayList<String>();
//			
//			// Call ExpressionVisitor to get list of left sort conditions and list of right sort conditions.
//			SMJSortConditionsBuilder conditions = new SMJSortConditionsBuilder(leftSortConditions, rightSortConditions, rightTableName);
//			this.joinCondition.accept(conditions);
//			
//			if (PlanBuilderConfigFileReader.getInstance().getSortType()==0) {
//				return new SMJOperator(this.joinCondition, 
//						new InMemorySortOperator(leftSortConditions, 
//								this.leftChild.getNextPhysicalOperator()),
//						new InMemorySortOperator(rightSortConditions, 
//								this.rightChild.getNextPhysicalOperator()),
//						leftSortConditions,
//						rightSortConditions);
//			} else {
//				return new SMJOperator(this.joinCondition, 
//						new ExternalSortOperator(leftSortConditions, 
//								this.leftChild.getNextPhysicalOperator(), 
//								PlanBuilderConfigFileReader.getInstance().getSortBuffer()),
//						new ExternalSortOperator(rightSortConditions, 
//								this.rightChild.getNextPhysicalOperator(), 
//								PlanBuilderConfigFileReader.getInstance().getSortBuffer()),
//						leftSortConditions,
//						rightSortConditions);
//			}
}