package indexing;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import utils.RecordComparator;

public class BPlusTree {

	public Node root;
	public Index index;
	public List<Record> records;
	public int order;
	
	public BPlusTree(Index index, List<Record> allRecords) {
		this.index = index;
		this.records = allRecords;
		this.order = index.getOrder();
	}

	/**
	 * Controller method for creating the B plus tree for the attribute in question
	 * @return
	 * 		The root node of the newly created B+ tree
	 */
	public void bulkInsert() {
		
		records.sort(new RecordComparator(index.getAttribute()));
		List<Node> leaves = new ArrayList<Node>();
		
		for (Record record: records) {
			Integer key = record.getTuple().getValueForAttr(index.getAttribute());
			LeafNode leaf = (LeafNode)getLeafNodeWithKey(leaves, key);
			
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
				leaf = (LeafNode) leaves.get(leaves.size()-1);
				
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
		
		checkForLastLeafUnderflow(leaves);
		IndexSerializer.serializeLeaves(leaves, index);
		Node root = createIndexNodes(leaves);
		IndexSerializer.serializeHeader(root, leaves.size(), order);

	}
	
	/**
	 * Creates the index recursively for the given nodes until we have one
	 * index node
	 * @param nodes
	 * @return
	 * 		Root node of the B plus tree
	 */
	public Node createIndexNodes(List<Node> nodes){
		
		if(nodes.size() == 1)
			return nodes.get(0);
		
		//Attaching all child nodes to a upper level index nodes
		List<Node> indexes = addChildrenToIndexNodes(nodes);
		
		for(Node index: indexes){
			for(int i=0; i<((IndexNode)index).children.size()-1; i++){
				((IndexNode)index).keys.add(getSmallestKeyFromSubtree(((IndexNode)index).children.get(i+1)));
			}
		}
		
		checkForLastIndexUnderflow(indexes);
		IndexSerializer.serializeIndexNodes(indexes);
		return createIndexNodes(indexes);
	}
	
	/**
	 * Makes all the current layers nodes children of the upper layer
	 * @param children
	 * @return
	 */
	public List<Node> addChildrenToIndexNodes(List<Node> children) {
		
		List<Node> indexes = new ArrayList<Node>();
		indexes.add(new IndexNode(children.remove(0)));
		
		for (Node node: children) {
			IndexNode lastIndex = (IndexNode)indexes.get(indexes.size()-1);
			
			if (lastIndex.children.size() == (2*order + 1)) {
				IndexNode newIndexNode = new IndexNode(node);
				indexes.add(newIndexNode);
				
			} else {
				lastIndex.children.add(node);
			}
		}
		
		return indexes;
	}
	
	/**
	 * Returns the smallest key of the subtree that is passed in
	 * @param root
	 * @return
	 */
	public Integer getSmallestKeyFromSubtree(Node root) {
		
		if (root.isLeafNode)
			return ((LeafNode)root).getFirstKey();
		
		return getSmallestKeyFromSubtree(((IndexNode)root).children.get(0));
	}
	
	
	/**
	 * Returns the leaf that has the key we are looking for
	 * @param leaves
	 * @param key
	 * @return
	 */
	public LeafNode getLeafNodeWithKey(List<Node> leaves, int key){
		for(Node leaf: leaves) {
			LeafNode currLeaf = (LeafNode)leaf;
			if(currLeaf.hasKey(key))
				return currLeaf;
		}
		return null;
	}
	
	/**
	 * Checks if we have an underflow on the leaf node and redistributes
	 * between the last 2 to prevent that from happening
	 * @param leaves
	 */
	public void checkForLastLeafUnderflow(List<Node> leaves) {
		
		//Root node does not have to be considered for underflow
		if(leaves.size() == 1)
			return;
		
		LeafNode lastLeaf = (LeafNode) leaves.get(leaves.size()-1);
		
		if (lastLeaf.size() < order){

			LinkedHashMap<Integer, List<Record>> secondLastDataEntries = new LinkedHashMap<Integer, List<Record>>();
			LinkedHashMap<Integer, List<Record>> lastDataEntries = new LinkedHashMap<Integer, List<Record>>();
			LinkedHashMap<Integer, List<Record>> allDataEntries = new LinkedHashMap<Integer, List<Record>>();
			
			//Getting the second last leaf node and finding the distribution factor for the nodes
			LeafNode secondLastLeafNode = (LeafNode) leaves.get(leaves.size()-2);
			int numKeys = lastLeaf.size() + secondLastLeafNode.size();
			
			allDataEntries.putAll(secondLastLeafNode.getDataEntries());
			allDataEntries.putAll(lastLeaf.getDataEntries());
			
			//Adds k/2 records into the second last leaf node
			for(int i = 0; i < numKeys/2; i++){
				Integer key = (Integer)allDataEntries.keySet().toArray()[i];
				secondLastDataEntries.put(key, allDataEntries.get(key));
			}
						
			//Adds the remaining records to the last leaf node
			for(int i = numKeys/2; i < numKeys - (numKeys/2); i++){
				Integer key = (Integer)allDataEntries.keySet().toArray()[i];
				lastDataEntries.put(key, allDataEntries.get(key));
			}
			
			secondLastLeafNode.setDataEntries(secondLastDataEntries);
			lastLeaf.setDataEntries(lastDataEntries);
		}
	}
	
	/**
	 * Checks if last index node has an underflow
	 * If yes, redistributes the keys and children for second last and last index nodes
	 * Second last index node gets m/2 - 1 keys and remainder go to last node
	 * @param indexes
	 */
	public void checkForLastIndexUnderflow(List<Node> indexes){
		
		//Root node does not have to be considered for underflow
		if(indexes.size() == 1)
			return;
		
		IndexNode lastIndex = (IndexNode)indexes.get(indexes.size()-1);
		
		//If the last index is underflowing, we need to redistribute with the second last node
		if(lastIndex.keys.size() < order){
			
			IndexNode secondLastIndex = (IndexNode)indexes.get(indexes.size()-2);
			
			//Collecting all the keys and children together so as to redistribute
			List<Integer> keys = new ArrayList<Integer>();
			keys.addAll(secondLastIndex.keys);
			keys.addAll(lastIndex.keys);
			
			List<Node> children = new ArrayList<Node>();
			children.addAll(secondLastIndex.children);
			children.addAll(lastIndex.children);
			
			secondLastIndex.keys.clear();
			secondLastIndex.children.clear();
			lastIndex.keys.clear();
			lastIndex.children.clear();

			//Second to last index node gets m/2 keys and m/2 + 1 children
			for(int i=0; i<keys.size()/2; i++){
				secondLastIndex.keys.add(keys.remove(i));
				secondLastIndex.children.add(children.remove(i));
			}
			secondLastIndex.children.add(children.remove(0));
			
			//Last index gets the remaining keys and children
			lastIndex.keys.addAll(keys);
			lastIndex.children.addAll(children);
		}
	}
}
