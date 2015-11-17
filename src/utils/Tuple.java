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
	
	/**
	 * Create a tuple given the attributes, their values and the table name
	 * @param values
	 * @param attributes
	 * @param tableName
	 */
	public Tuple (int[] values, String[] attributes,String tableName) {

		for (int i = 0; i < attributes.length; i++) {
			attributeValues.put(attributes[i], values[i]);
		}
		this.tableName = tableName;
		this.isUpdated = false;
	}
	
	/**
	 * Create a tuple from a string of attributes and table name
	 * @param line
	 * @param tableName
	 */
	public Tuple(String line, String tableName){
		this.tableName = tableName;
		this.createTuple(line);
		this.isSatisfies = false;
		this.isUpdated = false;
	}
	
	/**
	 * Create a tuple by joining two tuples
	 * @param leftTuple
	 * @param rightTuple
	 */
	public Tuple (Tuple leftTuple, Tuple rightTuple){
		this.attributeValues.putAll(leftTuple.getAttributeValues());
		this.attributeValues.putAll(rightTuple.getAttributeValues());	
		this.isUpdated = false;
	}
	
	/**
	 * Returns the attributes of the table
	 * @param tableName
	 * @return
	 */
	public String[] getTableAttributes(String tableName) {
		return DatabaseCatalog.getInstance().getTableAttributes(tableName);
	}
	
	/**
	 * Creates a tuple by splitting the String of values passed in
	 * @param line
	 * @return
	 */
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
	
	/**
	 * Convert tuple to string for printing to screen
	 * @return
	 */
	public String toStringValues() {
		String values = "";
		for(String key : attributeValues.keySet()) {
			values = values + attributeValues.get(key) + ",";
		}
		return values.substring(0, values.length()-1);
	}
	
	/**
	 * Returns the string representation of the relations attributes
	 * @return
	 */
	public String toStringAttributes(){
		String attr = "";
		for(String key : attributeValues.keySet()) {
			attr = attr + key;
			attr = attr + " ";
		}
		return attr;
	}
	
	/**
	 * Returns the value corresponding to the attribute that has been passed in
	 * @param colName
	 * @return
	 */
	public Integer getValueForAttr (String colName) {
		return attributeValues.get(colName);
	}
	
	/**
	 * Drops the attribute that is passed in as a string
	 * @param key
	 */
	public void dropAttribute(String key) {
		attributeValues.remove(key);
	}
	
	/**
	 * Removes all attributes except the ones specificied in the list that is passed in
	 * @param colNames
	 * @return
	 */
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
	
	/**
	 *
	 * @param tableName
	 */
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
	
	/**
	 * Returns the table name to which this tuple belongs
	 * @return
	 */
	public String getTableName() {
		return tableName;
	}
	
	/**
	 * Sets the table name for the tuple
	 * @param tableName
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	/**
	 * Gets the number of attributes in this tuple
	 * @return
	 */
	public Integer getNumAttributes() {
		return attributeValues.size();
	}
	
	public List<Integer> getListAttributeValues() {
		return (List<Integer>) attributeValues.values();
	}
}