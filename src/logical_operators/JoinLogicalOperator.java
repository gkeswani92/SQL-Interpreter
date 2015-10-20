package logical_operators;

import net.sf.jsqlparser.expression.Expression;
import operators.BNLJOperator;
import operators.JoinOperator;
import operators.Operator;
import operators.SMJOperator;
import operators.TNLJOperator;
import utils.ConfigFileReader;

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
		
		ConfigFileReader config = ConfigFileReader.getInstance();
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
			return new SMJOperator(this.joinCondition, 
					this.leftChild.getNextPhysicalOperator(), 
					this.rightChild.getNextPhysicalOperator());
		}
		
	}
}