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

	public ExternalSortOperator(List<String> sortConditions, Operator child, Integer numBufferPages) {
		super(sortConditions, child);
		this.numBufferPages = numBufferPages;
	}
	
	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		return null;
	}

	// TODO: READ SORT MERGE JOIN IMPLEMENTATION DESCRIPTION FOR DETAILS ON HOW TO IMPLEMENT THIS
	@Override
	public void reset(int index) {
		// TODO Auto-generated method stub		
	}

}
