package indexing;

import java.util.ArrayList;
import java.util.List;

import utils.RecordComparator;

/**
 * BPlusTree Class Assumptions: 1. No duplicate keys inserted 2. Order D:
 * D<=number of keys in a node <=2*D 3. All keys are non-negative
 * @param <K>
 * @param <T>
 */
public class BPlusTree {

	public Node root;
	public Index index;
	public List<Record> records;
	public int order;
	
	public BPlusTree(Index index, List<Record> allRecords) {
		this.index = index;
		this.records = allRecords;
		this.order = index.getOrder();
		bulkInsert();
	}

	/**
	 * TODO Search the value for a specific key
	 * 
	 * @param key
	 * @return value
	 */
	public List<Record> search(Integer key) {
		return null;
	}

	/**
	 * 
	 */
	public void bulkInsert() {
		
		records.sort(new RecordComparator(index.getAttribute()));
		List<LeafNode> leaves = new ArrayList<LeafNode>();
		
		for (Record record: records) {
			Integer key = record.getTuple().getValueForAttr(index.getAttribute());
			LeafNode leaf = getLeafNodeWithKey(leaves, key);
			
			//If this is the first ever key being inserted into the index
			if(leaves.isEmpty()){
				leaf = new LeafNode(key, record);
				leaves.add(leaf);
				continue;
			}
			
			//Adding a record to an existing leaf which has the key
			if (leaf != null) {
				leaf.addRecord(key, record);
			}
			
			//If this is the first time we are seeing a new key
			else {
				leaf = leaves.get(leaves.size()-1);
				
				//If this leaf is full
				if (leaf.size() >= 2*order) {
					leaf = new LeafNode(key, record);
					leaves.add(leaf);
				}
				
				//Last leaf has space for current key i.e. size is less than 2d
				else {
					leaf.addRecord(key, record);
				}
			} 
		}
		System.out.println(leaves.toString());
	}
	
	/**
	 * Returns the leaf that has the key we are looking for
	 * @param leaves
	 * @param key
	 * @return
	 */
	public LeafNode getLeafNodeWithKey(List<LeafNode> leaves, int key){
		for(LeafNode leaf: leaves){
			if(leaf.hasKey(key))
				return leaf;
		}
		return null;
	}
}
