package test;

import java.util.LinkedHashMap;
import java.util.Map;

public class FileTuple {
	private Map<Integer, Integer> attributeValues = new LinkedHashMap<Integer, Integer>();
	private static final String TUPLE_DELIM = ",";
	
	public FileTuple (int[] values) {		
		for (int i = 0; i < values.length; i++) {
			attributeValues.put(i, values[i]);
		}
	}
	
	public FileTuple(String line){		
		this.createTuple(line);
	}
	
	public void createTuple(String line) {
		String[] valueList = line.split(TUPLE_DELIM);		
		//Creating a hash map of attribute name: value
		//Attribute list is one index in front to ignore table name
		for(int i= 0; i< valueList.length; i++){
			attributeValues.put(i, Integer.parseInt(valueList[i]));
		}		
	}
	
	public Integer getValueForAttr (Integer colName) {
		return attributeValues.get(colName);
	}
	
	public Integer getAttributeCount(){
		return attributeValues.keySet().size();
	}
	
	public String toStringValues() {
		String values = "";
		for(Integer key : attributeValues.keySet()) {
			values = values + attributeValues.get(key) + ",";
		}		
		return values.substring(0, values.length()-1);
	}
	

}
