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

	private String tableName, alias;
	private Integer lowKey, highKey;
	private IndexBinaryFileReader ibfr;
	private BinaryFileReader bfr;
	private int currKeyIndex, numKeysInCurrLeaf, currRecordIndex;
	private LeafNode currLeafNode;
	private Index index;
	
	public IndexScanOperator(Integer lowKey, 
				Integer highKey, Index index, String alias) {
		
		this.index = index;
		this.tableName = index.getTableName();
		this.lowKey = lowKey;
		this.highKey = highKey;
		this.alias = alias;
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
		
		//Gets the record and the tuple corresponding to that tuple if we are using an
		//unclustered index
		if(index.getFlag() == 0){
			Record r = getNextRecord();
			Tuple t = getTupleForRecord(r);
			
			if(t != null) {
				t.updateTuple(alias);
			}
			return t;
		} else {
			
			//Gets the record only in the first call for clustered index
			if (currKeyIndex == -1){
				Record r = getNextRecord();
				if (r == null)
					return null;
				
				//Setting the channel at a position such that the next tuple will
				//be the corresponding to our record
				bfr.setChannelToPage(r.getPageId());
				for(int i=0; i<r.getTupleId();i++)
					bfr.getNextTuple();
			}
			
			Tuple t = bfr.getNextTuple();

			if (t == null) {
				return null;
			}
			
			t.updateTuple(alias);
			Integer attrToCheck = t.getValueForAttr(alias + "." + index.getAttribute());
			
			//If the tuple satisfies the condition, we return it. Else, this is 
			//the end
			if ((lowKey == null || attrToCheck.compareTo(lowKey) >= 0) && 
					(highKey == null || attrToCheck.compareTo(highKey) <=0)){
				return t;
			}
		}
		return null;
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

		//Sets the channels position to the beginning of the page the current
		//record is on and iterates over tuples to get the tuple we need
		bfr.setChannelToPage(r.getPageId());
		for(int i=0; i<r.getTupleId(); i++)
			bfr.getNextTuple();

		Tuple t = bfr.getNextTuple();
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
				
				//If the current key is in the range we are looking for, we return 
				//the next record
				if ((lowKey == null || keys[i] >= lowKey) && (highKey == null || keys[i] <= highKey)) {
					while (currRecordIndex < dataEntries.get(keys[currKeyIndex]).size()) {
						return dataEntries.get(keys[currKeyIndex]).get(currRecordIndex++);
					}
					currKeyIndex++;
					currRecordIndex = 0;
				} 
				
				//In case the key is less than the low key, we skip the current loop
				//and process the next key
				else if (lowKey != null && keys[i] < lowKey){
					currKeyIndex++;
					continue;
				}
				
				//This is called either when our key goes over the high key and is the
				//base case for this method
				else{
					return null;
				}
			}
			
			//Current leaf node has been processed. Read next page and create a new leaf node
			currLeafNode = ibfr.getNextLeafNode();
			currKeyIndex = 0;
			currRecordIndex = 0;
		}
		
		//We reach here only if we run out of leaves to process
		return null;
	}

	public String getTableName() {
		return tableName;
	}
	
	@Override
	public void reset() {
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

}
