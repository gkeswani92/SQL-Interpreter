package logical_operators;

import java.util.ArrayList;
import java.util.List;

import expression_visitors.SMJSortConditionsBuilder;
import net.sf.jsqlparser.expression.Expression;
import operators.BNLJOperator;
import operators.ExternalSortOperator;
import operators.InMemorySortOperator;
import operators.Operator;
import operators.SMJOperator;
import operators.TNLJOperator;
import utils.PlanBuilderConfigFileReader;

public class JoinLogicalOperator extends LogicalOperator {
	
	LogicalOperator leftChild;
	LogicalOperator rightChild;
	Expression joinCondition;
	String leftTableName, rightTableName;
	
	public JoinLogicalOperator(Expression joinCondition, LogicalOperator leftChild, 
			LogicalOperator rightChild, String rightTableName) {
		this.leftChild = leftChild;
		this.rightChild = rightChild;
		this.joinCondition = joinCondition;	
		this.rightTableName = rightTableName;
	}

	@Override
	public Operator getNextPhysicalOperator() {
		
		PlanBuilderConfigFileReader config = PlanBuilderConfigFileReader.getInstance();
		// Read config file and create appropriate join operator
		if (config.getJoinType() == 0) {
			return new TNLJOperator(this.joinCondition, 
					this.leftChild.getNextPhysicalOperator(), 
					this.rightChild.getNextPhysicalOperator());
		} else if (config.getJoinType() == 1) {
			return new BNLJOperator(this.joinCondition,
					this.leftChild.getNextPhysicalOperator(), 
					this.rightChild.getNextPhysicalOperator(), 
					config.getJoinBuffer());
		} else {
			List<String> leftSortConditions = new ArrayList<String>();
			List<String> rightSortConditions = new ArrayList<String>();
			
			// Call ExpressionVisitor to get list of left sort conditions and list of right sort conditions.
			SMJSortConditionsBuilder conditions = new SMJSortConditionsBuilder(leftSortConditions, rightSortConditions, rightTableName);
			this.joinCondition.accept(conditions);
			
			if (PlanBuilderConfigFileReader.getInstance().getSortType()==0) {
				return new SMJOperator(this.joinCondition, 
						new InMemorySortOperator(leftSortConditions, 
								this.leftChild.getNextPhysicalOperator()),
						new InMemorySortOperator(rightSortConditions, 
								this.rightChild.getNextPhysicalOperator()),
						leftSortConditions,
						rightSortConditions);
			} else {
				return new SMJOperator(this.joinCondition, 
						new ExternalSortOperator(leftSortConditions, 
								this.leftChild.getNextPhysicalOperator(), 
								PlanBuilderConfigFileReader.getInstance().getSortBuffer()),
						new ExternalSortOperator(rightSortConditions, 
								this.rightChild.getNextPhysicalOperator(), 
								PlanBuilderConfigFileReader.getInstance().getSortBuffer()),
						leftSortConditions,
						rightSortConditions);
			}
		}
		
	}
}