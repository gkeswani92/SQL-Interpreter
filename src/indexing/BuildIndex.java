package indexing;

import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import utils.BinaryFileReader;
import utils.IndexConfigFileReader;

public class BuildIndex {

	public static void buildIndexes() {
		
		System.out.println("Building indexes");
		LinkedHashMap<String, Index> indexes = IndexConfigFileReader.getInstance().getAllIndexes();
		
		Set<String> tableNames = indexes.keySet();
		for (String tableName: tableNames) {
			Index currIndex = indexes.get(tableName);
			buildIndex(currIndex);
		}
	}
	
	public static void buildIndex(Index index) {
		try {
			BinaryFileReader bfr = new BinaryFileReader(index.getTableName());
			List<Record> allRecords = bfr.getAllRecords();
			
			// Flag 0 for unclustered and 1 for clustered
			if (index.getFlag() == 0) {
				bulkLoad(index, allRecords);
			} else {
				sortLeafNodes(allRecords);
				bulkLoad(index, allRecords);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void bulkLoad(Index index, List<Record> allRecords) {
		BPlusTree tree = new BPlusTree(index, allRecords);
		tree.bulkInsert();
	}
	
	public static void sortLeafNodes (List<Record> allRecords) {
		
	}
}
