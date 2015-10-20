package operators;

import java.util.List;

import utils.Tuple;

public class ExternalSortOperator extends SortOperator {

	private Integer numBufferPages;

	public ExternalSortOperator(Operator child, List<String> sortConditions, 
			List<Tuple> tuples, Integer currIndex, Integer numBufferPages) {
		super(child, sortConditions, tuples, currIndex);
		this.numBufferPages = numBufferPages;
	}

	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		return null;
	}

}
