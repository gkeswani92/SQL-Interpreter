package operators;

import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.List;

import indexing.Index;
import indexing.LeafNode;
import indexing.Record;
import utils.BinaryFileReader;
import utils.IndexBinaryFileReader;
import utils.Tuple;

public class IndexScanOperator extends Operator {

	private String tableName;
	private Integer lowKey, highKey;
	private IndexBinaryFileReader ibfr;
	private BinaryFileReader bfr;
	private int currKeyIndex, numKeysInCurrLeaf, currRecordIndex;
	private LeafNode currLeafNode;
	
	public IndexScanOperator(String tableName, Integer lowKey, 
				Integer highKey, Index index) {
		
		this.tableName = tableName;
		this.lowKey = lowKey;
		this.highKey = highKey;
		currKeyIndex = -1;
		currRecordIndex = -1;
		
		try {
			this.bfr = new BinaryFileReader(this.tableName);
			this.ibfr = new IndexBinaryFileReader(index);
			ibfr.navigateToLeafNode(lowKey);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Tuple getNextTuple() {
		Record r = getNextRecord();
		Tuple t = getTupleForRecord(r);
		return t;
	}
	
	/**
	 * Gets the tuple corresponding to the page id and tuple id that has been
	 * passed in
	 * @param r
	 * @return
	 */
	public Tuple getTupleForRecord(Record r) {
		
		if (r == null)
			return null;

		bfr.setChannelToPage(r.getPageId());
		for(int i=0; i<r.getTupleId(); i++)
			bfr.getNextTuple();

		Tuple t = bfr.getNextTuple();
		if (t != null) {
			System.out.println(t.toStringValues());
		}
		return t;
	}
	
	/**
	 * Gets the next record depending on the current key and record index. Uses
	 * the state that is being maintained by the object.
	 * @return
	 */
	public Record getNextRecord() {
		
		if (currLeafNode == null) {
			currLeafNode = ibfr.getNextLeafNode();
			currKeyIndex = 0;
			currRecordIndex = 0;
		}
		
		while(currLeafNode != null){
			LinkedHashMap<Integer, List<Record>> dataEntries = currLeafNode.getDataEntries();
			Integer[] keys = new Integer[dataEntries.keySet().toArray().length];
			dataEntries.keySet().toArray(keys);
			numKeysInCurrLeaf = keys.length;
			
			for (int i = currKeyIndex; i< numKeysInCurrLeaf; i++) {
				if (keys[i] >= lowKey && keys[i] <= highKey) {
					currKeyIndex = i;
					while (currRecordIndex < dataEntries.get(keys[currKeyIndex]).size()) {
						return dataEntries.get(keys[currKeyIndex]).get(currRecordIndex++);
					}
					currKeyIndex++;
					currRecordIndex = 0;
				} else {
					return null;
				}
			}
			currLeafNode = ibfr.getNextLeafNode();
			currKeyIndex = 0;
			currRecordIndex = 0;
		}
		return null;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

}
