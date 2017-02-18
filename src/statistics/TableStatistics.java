package statistics;

import java.util.HashMap;
import java.util.Map;

public class TableStatistics {
	
	public String tableName;
	public Integer count;
	public Map<String, AttributeStatistics> tableStatistics;
	
	public TableStatistics(String tableName, Integer count, String[] tableAttributes){
		this.tableName = tableName;
		this.count = count;
		
		//Creating a place holder for the attribute sets
		tableStatistics = new HashMap<String, AttributeStatistics>();
		for(int i=0; i<tableAttributes.length; i++){
			tableStatistics.put(tableAttributes[i], new AttributeStatistics(tableAttributes[i]));
		}
	}
	
	public AttributeStatistics getAttributeStatistics(String attr){
		return tableStatistics.get(attr);
	}
	
	@Override
	public String toString(){
		String str = "[Number of Tuples: " + count + ", ";
		for(String attrName: tableStatistics.keySet()){
			str += attrName + ": [" + tableStatistics.get(attrName).toString() + "], ";
		}
		return str+"]";
	}
}
