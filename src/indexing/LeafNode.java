package indexing;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class LeafNode extends Node {
	
	private LinkedHashMap<Integer, List<Record>> leafDataEntries;
	
	public LeafNode(Integer key, Record rec) {
		leafDataEntries = new LinkedHashMap<Integer, List<Record>>();
		List<Record> records = new ArrayList<Record>();
		records.add(rec);
		leafDataEntries.put(key, records);
		isLeafNode = true;
	}
	
	public LinkedHashMap<Integer, List<Record>> getDataEntries(){
		return leafDataEntries;
	}

	public void setDataEntries(LinkedHashMap<Integer, List<Record>> leafDataEntries){
		this.leafDataEntries = leafDataEntries;
	}
	
	public boolean hasKey(Integer key) {
		return leafDataEntries.containsKey(key);
	}

	public void addRecord(Integer key, Record rec){
		List<Record> records = leafDataEntries.get(key);
		
		//If a key is being added to the leaf for the first time
		if (records == null) {
			records = new ArrayList<Record>();
			records.add(rec);
			leafDataEntries.put(key, records);
		} else {
			records.add(rec);
		}
	}
	
	public Integer size(){
		return leafDataEntries.size();
	}
	
	public Integer getFirstKey() {
		return (Integer)leafDataEntries.keySet().toArray()[0];
	}
	
	public String toString() {
		Set<Integer> keys = leafDataEntries.keySet();
		String result= "";
		for (Integer key: keys) {
			result = result + "[" + key + ":";
			List<Record> records = leafDataEntries.get(key);
			
			for (Record r: records) {
				result = result + "(" + r.getPageId() + ", " + r.getTupleId() + ")" + ",";
			}
			result = result + "]";
		}
		return result;
		
	}
}
