/**
 * 
 */
package utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class to initialize the table details for access
 * by all instances across application
 * @author Gaurav, Tanvi and Sahana (gk368,tmm259 and sv387)
 *
 */
public class DatabaseCatalog {
	private static Map<String,String[]> tableFileCatalag = new HashMap<String,String[]>();
	private static Map<String,String[]> tableSchemaCatalag = new HashMap<String,String[]>();
	private static final String ATTR_DELIM = " ";	
	private static DatabaseCatalog instance;
	
	private DatabaseCatalog() {	}	
	
	public static synchronized DatabaseCatalog getInstance() {
		if(instance == null){
			instance = new DatabaseCatalog();
		}
        return instance;
	}	
	
	public void buildDbCatalog (String inputsrcDir) {
		//Calling the databaseCatalog to read the schema.txt for furture access
		FileReader schemaFileReaderobj;
		BufferedReader file;		
		try {
			schemaFileReaderobj = new FileReader(inputsrcDir + "/db/schema.txt");
			file = new BufferedReader(schemaFileReaderobj);
			//read line by line to create the databaseCatalog hash
			String currentLine = file.readLine();
			while (currentLine != null){
				String[] attributeList = currentLine.split(ATTR_DELIM);
				String tableName = attributeList[0];
				String[] dbFileLoc = new String[2];
//				dbFileLoc[0]= inputsrcDir + "/db/data/" + tableName + "_humanreadable";
				dbFileLoc[0]= inputsrcDir + "/db/data/" + tableName;
				attributeList = Arrays.copyOfRange(attributeList, 1, attributeList.length);
				tableSchemaCatalag.put(tableName, attributeList);						
				tableFileCatalag.put(tableName,dbFileLoc);	
				currentLine = file.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
	}

	public String[] getTableAttributes(String tableName) {
		return tableSchemaCatalag.get(tableName);		
	}

//	public String getDataFilePath(String tableName) {
//		return tableFileCatalag.get(tableName)[0];		
//	}
	
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
		tableFileCatalag.put(alias,dataFilePath);		
	}	
}
