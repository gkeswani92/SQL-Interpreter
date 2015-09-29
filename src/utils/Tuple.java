package utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
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
	private static final String schemaPath = "samples/input/db/schema.txt"; //TODO: Remove the hard coding
	
	public Tuple(String line, String tableName){
		this.tableName = tableName;
		this.createTuple(line);
		this.isSatisfies = false;
	}
	
	public Tuple (Tuple leftTuple, Tuple rightTuple){
		this.attributeValues.putAll(leftTuple.getAttributeValues());
		this.attributeValues.putAll(rightTuple.getAttributeValues());	
	}
	
	@SuppressWarnings("resource")
	public String[] getTableAttributes(String tableName) {
		FileReader fileReaderObj;
		BufferedReader file;
		
		try {
			fileReaderObj = new FileReader(schemaPath);
			file =  new BufferedReader(fileReaderObj);
			
			//To find the line which breaks the while loop i.e. the one which matches the passed in table name
			String currentLine = file.readLine();
			while(!currentLine.startsWith(tableName)){
				currentLine = file.readLine();
			}
			
			//Array of the table name and attribute names
			String[] attributeList = currentLine.split(ATTR_DELIM);
			
			return attributeList;
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Tuple createTuple(String line) {
		String[] valueList = line.split(TUPLE_DELIM);
		String[] attributeList = getTableAttributes(tableName);
		
		//Creating a hash map of attribute name: value
		//Attribute list is one index in front to ignore table name
		for(int i=0; i<valueList.length; i++){
			attributeValues.put(tableName + "." + attributeList[i+1], Integer.parseInt(valueList[i]));
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
	
	public Tuple retainAttributes(Set<String> colNames) {
		attributeValues.keySet().retainAll(colNames);
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
}
