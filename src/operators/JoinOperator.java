package operators;

import net.sf.jsqlparser.expression.Expression;
import parser.ExpressionEvaluator;
import utils.Tuple;

/**
 * Extends operator for implementation of join operator. Contains a left child and a right child
 * and returns the next tuple that satisfies the join condition
 * In case of no join condition, returns the next tuple in the cartesian product
 * @author tmm259
 */
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
		
		// Make left tuple the outer table and iterate through the right child(inner table)
		while (currLeftTuple != null) {
			currRightTuple = rightChild.getNextTuple();
			while (currRightTuple != null) {
				Tuple joinTuple = new Tuple(currLeftTuple, currRightTuple);
				
				// Cartesian product (in case of no join condition)
				if (joinCondition == null) {
					return joinTuple;
					
				} else {
					// Evaluate join condition	
					ExpressionEvaluator ob = new ExpressionEvaluator(joinTuple);
				    joinCondition.accept(ob);
				    if (joinTuple.getIsSatisfies()) {
				        return joinTuple;
				    }       
				    
				    currRightTuple = rightChild.getNextTuple();
				}
			}
			
			// When right child is completely traversed, reset the child and move to next left tuple
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
