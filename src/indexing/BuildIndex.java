package indexing;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import operators.InMemorySortOperator;
import operators.ScanOperator;
import utils.BinaryFileReader;
import utils.BinaryFileWriter;
import utils.DatabaseCatalog;
import utils.IndexConfigFileReader;
import utils.Tuple;

/**
 * Class to build clustered and unclustered indexes
 * @author tanvimehta
 *
 */
public class BuildIndex {

	public static void buildIndexes() {
		
		System.out.println("Building indexes");
		LinkedHashMap<String, List<Index>> indexes = IndexConfigFileReader.getInstance().getAllIndexes();
		
		Set<String> tableNames = indexes.keySet();
		for (String tableName: tableNames) {
			List<Index> tableIndexes = indexes.get(tableName);
			for(Index currIndex: tableIndexes){
				buildIndex(currIndex);
			}
		}
	}
	
	/**
	 * Checks the flag and builds either a clustered or unclustered index
	 * @param index
	 */
	public static void buildIndex(Index index) {
		try {

			// Flag 0 for unclustered and 1 for clustered
			if (index.getFlag() == 1) {
				sortRelation(index);
			}
			
			BinaryFileReader bfr = new BinaryFileReader(index.getTableName());
			List<Record> allRecords = bfr.getAllRecords();
			bulkLoad(index, allRecords);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Calls the BPlusTree bulk insert method to build a BPlusTree and serialize it
	 * @param index
	 * @param allRecords
	 */
	public static void bulkLoad(Index index, List<Record> allRecords) {
		BPlusTree tree = new BPlusTree(index, allRecords);
		tree.bulkInsert();
	}
	
	/**
	 * Sorts the relation on the attribute from the index passed in
	 * @param index
	 */
	public static void sortRelation (Index index) {
		List<String> sortConditions = new ArrayList<String>();
		BinaryFileWriter bfw = null;
		
		sortConditions.add(index.getTableName() + "." + index.getAttribute());
		InMemorySortOperator op = new InMemorySortOperator(sortConditions, new ScanOperator(index.getTableName()));
		Tuple currTuple = op.getNextTuple();
		List<Tuple> tuples = new ArrayList<Tuple>();
		while(currTuple != null) {
			tuples.add(currTuple);
			//bfw.writeNextTuple(currTuple);
			currTuple = op.getNextTuple();
		}
		
		try {
			bfw = new BinaryFileWriter(DatabaseCatalog.getInstance().getInputDir() + "/db/data/" + index.getTableName());
			bfw.writeTupleCollection(tuples);
			bfw.writeNextTuple(null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
