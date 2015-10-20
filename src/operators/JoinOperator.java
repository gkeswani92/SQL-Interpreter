package operators;

import net.sf.jsqlparser.expression.Expression;
import utils.Tuple;

/**
 * Extends operator for implementation of join operator. Contains a left child and a right child
 * and returns the next tuple that satisfies the join condition
 * In case of no join condition, returns the next tuple in the cartesian product
 * @author Gaurav, Tanvi and Sahana (gk368,tmm259 and sv387)
 */
public abstract class JoinOperator extends Operator {
	
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
