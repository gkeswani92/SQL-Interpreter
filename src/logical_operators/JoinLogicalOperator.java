package logical_operators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.jsqlparser.expression.Expression;
import operators.Operator;

public class JoinLogicalOperator extends LogicalOperator {
	
	Map<String, LogicalOperator> children;
	List<Expression> joinConditions;
	
	public JoinLogicalOperator(List<Expression> joinConditions, Map<String, LogicalOperator> children) {
		this.children = new LinkedHashMap<String, LogicalOperator>();
		this.children.putAll(children);
		this.joinConditions = new ArrayList<Expression>();
		this.joinConditions.addAll(joinConditions);
	}
	
	@Override
	public Operator getNextPhysicalOperator() {
		
		Map<String, Operator> physicalChildren = new LinkedHashMap<String, Operator>();
		for(String tableName: children.keySet()){
			LogicalOperator currentChild = children.get(tableName);
			physicalChildren.put(tableName, currentChild.getNextPhysicalOperator());
		}
		return null;
		
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
//		}
		
	}
}