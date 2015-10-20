package operators;

import net.sf.jsqlparser.expression.Expression;
import utils.Tuple;

public class SMJOperator extends JoinOperator {

	public SMJOperator(Expression joinCondition, Operator leftChild, Operator rightChild) {
		super(joinCondition, leftChild, rightChild);
	}

	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		return null;
	}
}
