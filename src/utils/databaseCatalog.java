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

/**
 * @author sv387
 *
 */
public class databaseCatalog {
	private static HashMap<String,String[]> tableFileCatalag = new HashMap<String,String[]>();
	private static HashMap<String,String[]> tableSchemaCatalag = new HashMap<String,String[]>();
	private static final String ATTR_DELIM = " ";	
	private static databaseCatalog instance;
	
	private databaseCatalog() {	}
	
	/*private static class databaseCatalogHolder{		
		private static final databaseCatalog INSTANCE = new databaseCatalog();
	}*/
	
	public static synchronized databaseCatalog getInstance() {
		if(instance == null){
			instance = new databaseCatalog();
		}
        return instance;
	}	
	
	public void buildDbCatalog (String inputsrcDir) {
		// TODO Auto-generated method stub
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
				String[] dbFileLoc = new String[1];
				dbFileLoc[0]= inputsrcDir + "/db/data" + tableName + ".csv";
				attributeList = Arrays.copyOfRange(attributeList, 1, attributeList.length);
				tableSchemaCatalag.put(tableName, attributeList);						
				tableFileCatalag.put(tableName,dbFileLoc);	
				currentLine = file.readLine();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}

	public String[] getTableAttributes(String tableName) {
		// TODO Auto-generated method stub
		return tableSchemaCatalag.get(tableName);		
	}

	public String getDataFilePath(String tableName) {
		// TODO Auto-generated method stub
		return tableFileCatalag.get(tableName)[0];		
	}
	
	public static void main(String[] args) {
		String inputSrcDir;
		String outputSrcDir;		
		if(args.length == 2){
			inputSrcDir = args[0];
			outputSrcDir = args[1];
			databaseCatalog.getInstance().buildDbCatalog(inputSrcDir);
			String tableName = "Sailors";
			System.out.print("Table " + tableName + "'s attribute list is :");			;
			String [] output = databaseCatalog.getInstance().getTableAttributes(tableName);
			for(int i=0; i<output.length; i++){
				System.out.print(output[i]+ ATTR_DELIM);
			}
			
			System.out.println("\nTable" + tableName + "'s data File path:" +databaseCatalog.getInstance().getDataFilePath(tableName));
			
		}		
	}		

}
