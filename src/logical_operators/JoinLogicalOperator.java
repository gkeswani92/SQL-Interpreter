package logical_operators;

import net.sf.jsqlparser.expression.Expression;
import operators.JoinOperator;
import operators.Operator;

public class JoinLogicalOperator extends LogicalOperator {
	
	LogicalOperator leftChild;
	LogicalOperator rightChild;
	Expression joinCondition;
	
	public JoinLogicalOperator(Expression joinCondition, LogicalOperator leftChild, LogicalOperator rightChild) {
		this.leftChild = leftChild;
		this.rightChild = rightChild;
		this.joinCondition = joinCondition;
	}

	@Override
	public Operator getNextPhysicalOperator() {
		return new JoinOperator(this.joinCondition, 
				this.leftChild.getNextPhysicalOperator(), 
				this.rightChild.getNextPhysicalOperator());
	}
}