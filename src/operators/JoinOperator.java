package operators;

import net.sf.jsqlparser.statement.select.PlainSelect;
import utils.Tuple;

public class JoinOperator extends Operator {
	
	Operator leftChild;
	Operator rightChild;
	
	public JoinOperator(PlainSelect body, Operator leftChild, Operator rightChild) {
		this.leftChild = leftChild;
		this.rightChild = rightChild;
	}
	
	@Override
	public Tuple getNextTuple() {
		
		return null;
	}

	@Override
	public void reset() {

	}
	
    public Operator getLeftChild() {
    	return leftChild;
    }
    
    public Operator getRightChild() {
    	return rightChild;
    }

}
