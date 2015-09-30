package utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Util class for tuples in a table.
 * Keep track of attribute names and values and whether a tuple satisfies a certain condition
 * @author tmm259
 */
public class Tuple {
	
	private String tableName;
	private Boolean isSatisfies;
	private Map<String, Integer> attributeValues = new LinkedHashMap<String, Integer>();
	private static final String TUPLE_DELIM = ",";
	private static final String ATTR_DELIM = " ";
	
	public Tuple(String line, String tableName){
		this.tableName = tableName;
		this.createTuple(line);
		this.isSatisfies = false;
	}
	
	public Tuple (Tuple leftTuple, Tuple rightTuple){
		this.attributeValues.putAll(leftTuple.getAttributeValues());
		this.attributeValues.putAll(rightTuple.getAttributeValues());	
	}
	
	public String[] getTableAttributes(String tableName) {
		return databaseCatalog.getInstance().getTableAttributes(tableName);
	}
	
	public Tuple createTuple(String line) {
		String[] valueList = line.split(TUPLE_DELIM);
		String[] attributeList = getTableAttributes(tableName);
		
		//Creating a hash map of attribute name: value
		//Attribute list is one index in front to ignore table name
		for(int i=0; i<valueList.length; i++){
			attributeValues.put(tableName + "." + attributeList[i], Integer.parseInt(valueList[i]));
		}
		return this;
	}
	
	public String toStringValues() {
		String values = "";
		for(String key : attributeValues.keySet()) {
			values = values + attributeValues.get(key) + " ";
		}
		return values;
	}
	
	public String toStringAttributes(){
		String attr = "";
		for(String key : attributeValues.keySet()) {
			attr = attr + key;
			attr = attr + " ";
		}
		return attr;
	}
	
	public Integer getValueForAttr (String colName) {
		return attributeValues.get(colName);
	}
	
	public void dropAttribute(String key) {
		attributeValues.remove(key);
	}
	
	public Tuple retainAttributes(List<String> colNames) {
		attributeValues.keySet().retainAll(colNames);
		Map<String, Integer> orderedAttributeValues = new LinkedHashMap<String, Integer>();
		
		//Creating an ordered version of the attribute list depending on the column order that was specified
		for (String column: colNames) 
			orderedAttributeValues.put(column, attributeValues.get(column));
		
		this.setAttributeValues(orderedAttributeValues);
		return this;
	}
	
	public Set<String> getArributeList() {
		return attributeValues.keySet();
	}
	public Boolean getIsSatisfies() {
		return isSatisfies;
	}

	public void setIsSatisfies(Boolean isSatisfies) {
		this.isSatisfies = isSatisfies;
	}
	
	public Map<String, Integer> getAttributeValues() {
		return attributeValues;
	}
	
	public void setAttributeValues(Map<String, Integer> orderedAttributeValues) {
		attributeValues = orderedAttributeValues;
	}
}