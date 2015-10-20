package operators;

import net.sf.jsqlparser.expression.Expression;
import utils.Tuple;

public class BNLJOperator extends JoinOperator {

	private Integer numBufferPages;
	
	public BNLJOperator(Expression joinCondition, Operator leftChild, Operator rightChild, Integer numBufferPages) {
		super(joinCondition, leftChild, rightChild);
		this.numBufferPages = numBufferPages;
	}

	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		return null;
	}
}
