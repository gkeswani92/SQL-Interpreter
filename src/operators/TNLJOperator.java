package operators;

import net.sf.jsqlparser.expression.Expression;
import parser.ExpressionEvaluator;
import utils.Tuple;

public class TNLJOperator extends JoinOperator {

	private Integer countInner = 0;
	
	public TNLJOperator(Expression joinCondition, Operator leftChild, Operator rightChild) {
		super(joinCondition, leftChild, rightChild);
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
				    
				    countInner++;
				    currRightTuple = rightChild.getNextTuple();
				}
			}
			
			// When right child is completely traversed, reset the child and move to next left tuple
			rightChild.reset();
			countInner = 0;
			currLeftTuple = leftChild.getNextTuple();
		}
		return null;		
	}

}
