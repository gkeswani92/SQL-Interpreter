package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import indexing.Index;

/**
 * Util singleton class to read the index config file and store the indexes on the database
 * @author tanvimehta
 */
public class IndexConfigFileReader {
	
	private FileReader configFileReaderObj;
	private BufferedReader file;
	private LinkedHashMap<String, List<Index>> indexes;
	private static IndexConfigFileReader instance;
	
	public IndexConfigFileReader() {
		indexes = new LinkedHashMap<String, List<Index>>();
	}
	
	public static synchronized IndexConfigFileReader getInstance() {
		if(instance == null){
			instance = new IndexConfigFileReader();
		}
        return instance;
	}	
	
	public void readConfigFile(String inputsrcDir) {
		try {
			configFileReaderObj = new FileReader(inputsrcDir + "/db/index_info.txt");
			file = new BufferedReader(configFileReaderObj);
			
			// Read each line of the file and translate into indexes
			String line = file.readLine();
			while (line != null && !line.isEmpty()) {
				String[] parts = line.split(" ");	
				if (indexes.containsKey(parts[0])) {
					indexes.get(parts[0]).add(new Index(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3])));
				} else {
					List<Index> indxs = new ArrayList<Index>();
					indxs.add(new Index(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3])));
					indexes.put(parts[0], indxs);
				}
				line = file.readLine();
			}

		} catch (IOException ie) {
			ie.printStackTrace();
		}

	}
	
	public List<Index> getIndexesByTableName(String tableName) {
		return indexes.get(tableName);
	}
	
	public LinkedHashMap<String, List<Index>> getAllIndexes() {
		return indexes;
	}
	
	public boolean attributeHasIndex(String attrName, String tableName){
		List<Index> tableIndexes = indexes.get(tableName);
		if(tableIndexes != null){
			for(Index index: tableIndexes) {
				if(index.getAttribute().equals(attrName) && index.getTableName().equals(tableName))
					return true;
			}
		}
		return false;
	}
	
	public Index getIndexForAttribute(String attrName, String tableName){
		if (DatabaseCatalog.getInstance().getTableForAlias(tableName) != null) {
			tableName = DatabaseCatalog.getInstance().getTableForAlias(tableName);
		}
		
		if(attrName != null && attributeHasIndex(attrName, tableName)) {
			List<Index> tableIndexes = indexes.get(tableName);
			for(Index index: tableIndexes) {
				if(index.getAttribute().equals(attrName) && index.getTableName().equals(tableName))
					return index;
			}
		}
		return null;
	}
}
