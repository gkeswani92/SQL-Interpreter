package indexing;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LeafNode {
	
	private LinkedHashMap<Integer, List<Record>> leafDataEntries;
	
	public LeafNode(Integer key, Record rec) {
		leafDataEntries = new LinkedHashMap<Integer, List<Record>>();
		List<Record> records = new ArrayList<Record>();
		records.add(rec);
		leafDataEntries.put(key, records);
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
}
