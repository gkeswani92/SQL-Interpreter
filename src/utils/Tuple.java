package utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Util class for tuples in a table.
 * Keep track of attribute names and values and whether a tuple satisfies a certain condition
 * @author Gaurav, Tanvi and Sahana (gk368,tmm259 and sv387)
 */
public class Tuple {
	
	private String tableName;
	private Boolean isSatisfies;
	private Map<String, Integer> attributeValues = new LinkedHashMap<String, Integer>();
	private static final String TUPLE_DELIM = ",";
	private boolean isUpdated;
	
	public Tuple (int[] values, String[] attributes,String tableName) {

		for (int i = 0; i < attributes.length; i++) {
			attributeValues.put(attributes[i], values[i]);
		}
		this.tableName = tableName;
		this.isUpdated = false;
	}
	
	public Tuple(String line, String tableName){
		this.tableName = tableName;
		this.createTuple(line);
		this.isSatisfies = false;
		this.isUpdated = false;
	}
	
	public Tuple (Tuple leftTuple, Tuple rightTuple){
		this.attributeValues.putAll(leftTuple.getAttributeValues());
		this.attributeValues.putAll(rightTuple.getAttributeValues());	
		this.isUpdated = false;
	}
	
	public String[] getTableAttributes(String tableName) {
		return DatabaseCatalog.getInstance().getTableAttributes(tableName);
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
			values = values + attributeValues.get(key) + ",";
		}
		// Remove trailing ,
		// TODO: REMOVE THIS ITS TOO PYTHONY
		return values.substring(0, values.length()-1);
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
	
	public String[] getArributeArray() {
		return attributeValues.keySet().toArray(new String[attributeValues.size()]);
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
	
	public void updateTuple (String tableName) {
		if (!isUpdated) {
			this.tableName = tableName;
			Map<String, Integer> newAttrValues = new LinkedHashMap<String, Integer>();
			
			for(String key: attributeValues.keySet()) {
				newAttrValues.put(tableName+"."+key, attributeValues.get(key));
			}
			
			attributeValues.clear();
			attributeValues.putAll(newAttrValues);
			
			this.isUpdated = true;
		}
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public Integer getNumAttributes() {
		return attributeValues.size();
	}
}