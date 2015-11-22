/**
 * 
 */
package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import statistics.TableStatistics;

/**
 * Singleton class to initialize the table details for access by all instances
 * across application
 * 
 * @author Gaurav, Tanvi and Sahana (gk368,tmm259 and sv387)
 *
 */
public class DatabaseCatalog {
	private Map<String, String[]> tableFileCatalag = new HashMap<String, String[]>();
	private Map<String, String[]> tableSchemaCatalag = new HashMap<String, String[]>();
	private Map<String, String> aliasToTableNameMap = new HashMap<String, String>();
	private Map<String, TableStatistics> stats = new HashMap<String, TableStatistics>();
	
	private final String ATTR_DELIM = " ";
	private static DatabaseCatalog instance;
	private String inputsrcDir;

	private DatabaseCatalog() {
		//Default constructor
	}

	public static synchronized DatabaseCatalog getInstance() {
		if (instance == null) {
			instance = new DatabaseCatalog();
		}
		return instance;
	}

	/**
	 * Given the path of the interpreter configuration file, this method returns
	 * the input directory, output dir, temp dir, flag for building indexes and
	 * flag for evaluating the queries
	 * @param configPath
	 * @return
	 */
	public ArrayList<String> getConfigPaths(String configPath) {
		
		FileReader configFileReaderobj;
		BufferedReader file;
		ArrayList<String> configParameters = new ArrayList<String>();
		
		try{
			configFileReaderobj = new FileReader(configPath);
			file = new BufferedReader(configFileReaderobj);
			
			//Read line by line to create the array list of 5 input params
			String currentLine = file.readLine();
			while(currentLine != null){
				configParameters.add(currentLine);
				currentLine = file.readLine();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return configParameters;
	}
	
	public void buildDbCatalog(String inputsrcDir) {
		// Calling the databaseCatalog to read the schema.txt for furture access
		FileReader schemaFileReaderobj;
		BufferedReader file;
		this.inputsrcDir = inputsrcDir;
		try {
			schemaFileReaderobj = new FileReader(inputsrcDir + "/db/schema.txt");
			file = new BufferedReader(schemaFileReaderobj);
			
			// read line by line to create the databaseCatalog hash
			String currentLine = file.readLine();
			while (currentLine != null) {
				String[] attributeList = currentLine.split(ATTR_DELIM);
				String tableName = attributeList[0];
				String[] dbFileLoc = new String[2];
				dbFileLoc[0] = inputsrcDir + "/db/data/" + tableName;
				attributeList = Arrays.copyOfRange(attributeList, 1, attributeList.length);
				tableSchemaCatalag.put(tableName, attributeList);
				tableFileCatalag.put(tableName, dbFileLoc);
				currentLine = file.readLine();
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String[] getTableAttributes(String tableName) {
		return tableSchemaCatalag.get(tableName);
	}

	public String getBinaryDataFilePath(String tableName) {
		return tableFileCatalag.get(tableName)[0];
	}

	public String getEntryForAlias(String alias) {
		return tableFileCatalag.get(alias)[0];
	}

	public void setEntryForAlias(String baseTable, String alias) {
		tableSchemaCatalag.put(alias, getTableAttributes(baseTable));
		String[] dataFilePath = new String[1];
		dataFilePath[0] = getBinaryDataFilePath(baseTable);
		tableFileCatalag.put(alias, dataFilePath);
		aliasToTableNameMap.put(alias, baseTable);
	}
	
	public String getInputDir() {
		return inputsrcDir;
	}
	
	public String getTableForAlias(String alias) {
		return aliasToTableNameMap.get(alias);
	}
	
	public Set<String> getTableNames(){
		return tableSchemaCatalag.keySet();
	}
	
	public void setStatistics(Map<String, TableStatistics> stats) {
		this.stats = stats;
	}
	
	public TableStatistics getStatistics(String tableName) {
		return stats.get(tableName);
	}
}
