package operators;

import indexing.Index;
import utils.IndexBinaryFileReader;
import utils.Tuple;

public class IndexScanOperator extends Operator {

	private String tableName;
	private Integer lowKey, highKey;
	private IndexBinaryFileReader ibfr;
	
	public IndexScanOperator(String tableName, Integer lowKey, 
				Integer highKey, Index index) {
		this.tableName = tableName;
		this.lowKey = lowKey;
		this.highKey = highKey;
		this.ibfr = new IndexBinaryFileReader(index);
		
		ibfr.navigateToLeafNode(lowKey);
	}
	
	@Override
	public Tuple getNextTuple() {
		return null;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

}
