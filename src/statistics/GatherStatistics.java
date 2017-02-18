package statistics;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import utils.BinaryFileReader;
import utils.DatabaseCatalog;
import utils.Tuple;

public class GatherStatistics {
	
	public static Map<String, TableStatistics> gatherStatistics(Set<String> tableNames){
		
		Map<String, TableStatistics> stats = new HashMap<String, TableStatistics>();
		
		for(String tableName: tableNames) {
			try {
				//Getting all the tuples for the table
				BinaryFileReader bfr = new BinaryFileReader(tableName);
				List<Tuple> tuples = bfr.getAllTuples();
				Integer numTuples = bfr.getNumberOfTuples();
				
				//Creating the current table statistics object
				String[] tableAttributes = DatabaseCatalog.getInstance().getTableAttributes(tableName);
				TableStatistics currentTableStats = new TableStatistics(tableName, numTuples, tableAttributes);
				
				findMaxValueForAttributes(tuples, currentTableStats);
				stats.put(tableName, currentTableStats);
			} 
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		writeStatistics(stats);
		DatabaseCatalog.getInstance().setStatistics(stats);
		return stats;
	}

	private static void findMaxValueForAttributes(List<Tuple> tuples, TableStatistics currentTableStats) {
		
		//Finding the maximum and minimum value for each attribute of the relation
		for(Tuple t: tuples){
			Map<String, Integer> values = t.getAttributeValues();
			
			for(String attrName: values.keySet()) {
				Integer attributeValue = values.get(attrName);
				AttributeStatistics currentAttributeStats= currentTableStats.tableStatistics.get(attrName);
				if(attributeValue.compareTo(currentAttributeStats.minimum) == -1)
					currentAttributeStats.minimum = attributeValue;
				
				if(attributeValue.compareTo(currentAttributeStats.maximum) == 1)
					currentAttributeStats.maximum = attributeValue;
			}
		}
	}
	
	public static void writeStatistics(Map<String, TableStatistics> stats){
		String statistics = "";
		String statsFilePath = DatabaseCatalog.getInstance().getInputDir()+"/db/stats.txt";
		PrintWriter out = null;
		
		try {
			out = new PrintWriter(statsFilePath);
			
			for(String tableName: stats.keySet()){
				TableStatistics currentTableStats = stats.get(tableName);
				statistics += tableName + " " + currentTableStats.count + " ";
				
				for(String attrName: currentTableStats.tableStatistics.keySet()){
					AttributeStatistics currentAttrStats = currentTableStats.tableStatistics.get(attrName);
					statistics += currentAttrStats.attributeName + "," + currentAttrStats.minimum + "," + currentAttrStats.maximum + " ";
				}
				statistics += "\n";
			}
			out.write(statistics);
			out.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
