package operators;

import net.sf.jsqlparser.statement.select.PlainSelect;
import parser.OperatorVisitor;
import utils.Tuple;

public class JoinOperator extends Operator {
	
	Operator leftChild;
	Operator rightChild;
	
	public JoinOperator(PlainSelect body) {
	}
	
	@Override
	public Tuple getNextTuple() {
		
		return null;
	}

	@Override
	public void reset() {

	}
	
	@Override
	public void accept(OperatorVisitor visitor) {
		visitor.visit(this);
	}
	
    public Operator getLeftChild() {
    	return leftChild;
    }
    
    public Operator getRightChild() {
    	return rightChild;
    }

}
