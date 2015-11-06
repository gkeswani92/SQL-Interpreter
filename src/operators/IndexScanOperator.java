package operators;

import utils.Tuple;

public class IndexScanOperator extends Operator {

	private static String tableName;
	private static Integer lowKey;
	private static Integer highKey;
	
	public IndexScanOperator(String tableName, Integer highKey, Integer lowKey){
		this.tableName = tableName;
		this.lowKey = lowKey;
		this.highKey = highKey;
	}
	
	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

}
