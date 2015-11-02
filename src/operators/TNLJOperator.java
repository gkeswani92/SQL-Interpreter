package operators;

import net.sf.jsqlparser.expression.Expression;
import parser.ExpressionEvaluator;
import utils.Tuple;

public class TNLJOperator extends JoinOperator {

	private static int count = 0;	
	private Integer countInner = 0;
	private Integer countOuter = 0;
	
	
	public TNLJOperator(Expression joinCondition, Operator leftChild, Operator rightChild) {
		super(joinCondition, leftChild, rightChild);
	}

	@Override
	public Tuple getNextTuple() {
//		199,789,912,199,804
		if (currLeftTuple == null) {
			currLeftTuple = leftChild.getNextTuple();
		}		
		
		// Make left tuple the outer table and iterate through the right child(inner table)
		while (currLeftTuple != null) {
			currRightTuple = rightChild.getNextTuple();
			while (currRightTuple != null) {
				Tuple joinTuple = new Tuple(currLeftTuple, currRightTuple);
				
				if ( joinTuple !=null && joinTuple.getArributeList().contains("Sailors.A") && joinTuple.getValueForAttr("Sailors.A") == 199 && 
						joinTuple.getArributeList().contains("Sailors.B") && joinTuple.getValueForAttr("Sailors.B") == 789 &&
								joinTuple.getArributeList().contains("Sailors.C") && joinTuple.getValueForAttr("Sailors.C") == 912 &&
										joinTuple.getArributeList().contains("Reserves.G") && joinTuple.getValueForAttr("Reserves.G") == 199 &&
												joinTuple.getArributeList().contains("Reserves.H") && joinTuple.getValueForAttr("Reserves.H") == 804) {
					int test = 0;
					test = test;
				}
				
				// Cartesian product (in case of no join condition)
				if (joinCondition == null) {
					return joinTuple;
					
				} else {
					// Evaluate join condition	
					ExpressionEvaluator ob = new ExpressionEvaluator(joinTuple);
				    joinCondition.accept(ob);
				    if (joinTuple.getIsSatisfies()) {
				    	System.out.println(count++ + " " + joinTuple.toStringValues());
				        return joinTuple;
				    }       
				    
				    countInner++;
				    currRightTuple = rightChild.getNextTuple();
				}
			}
			
			// When right child is completely traversed, reset the child and move to next left tuple
			rightChild.reset();
			//System.out.println(countInner);
			countInner = 0;
			currLeftTuple = leftChild.getNextTuple();
		}
		return null;		
	}

}
