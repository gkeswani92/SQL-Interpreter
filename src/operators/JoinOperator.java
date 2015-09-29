package operators;

import net.sf.jsqlparser.expression.Expression;
import parser.ExpressionEvaluator;
import utils.Tuple;

public class JoinOperator extends Operator {
	
	Operator leftChild;
	Operator rightChild;
	Expression joinCondition;
	Tuple currLeftTuple;
	Tuple currRightTuple;
	
	public JoinOperator(Expression joinCondition, Operator leftChild, Operator rightChild) {
		this.leftChild = leftChild;
		this.rightChild = rightChild;
		this.joinCondition = joinCondition;
	}
	
	@Override
	public Tuple getNextTuple() {
		
		if (currLeftTuple == null) {
			currLeftTuple = leftChild.getNextTuple();
		}
		
			while (currLeftTuple != null) {
				currRightTuple = rightChild.getNextTuple();
				while (currRightTuple != null) {
					Tuple joinTuple = new Tuple(currLeftTuple, currRightTuple);
					if (joinCondition == null) {
						return joinTuple;
					} else {
						
						ExpressionEvaluator ob = new ExpressionEvaluator(joinTuple);
				        joinCondition.accept(ob);
				        if (joinTuple.getIsSatisfies()) {
				        	return joinTuple;
				        }
				        
				        currRightTuple = rightChild.getNextTuple();
					}
				}
				rightChild.reset();
				currLeftTuple = leftChild.getNextTuple();
			}
			return null;
			
	}

	@Override
	public void reset() {
		rightChild.reset();
		leftChild.reset();
	}
	
    public Operator getLeftChild() {
    	return leftChild;
    }
    
    public Operator getRightChild() {
    	return rightChild;
    }

}
