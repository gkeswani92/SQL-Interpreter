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
	
	public static void bulkLoad(Index index, List<Record> allRecords) {
		BPlusTree tree = new BPlusTree(index, allRecords);
		tree.bulkInsert();
	}
	
	public static void sortRelation (Index index) {
		List<String> sortConditions = new ArrayList<String>();
		BinaryFileWriter bfw = null;
		
		try {
			bfw = new BinaryFileWriter(DatabaseCatalog.getInstance().getInputDir() + "/db/" + index.getTableName());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		sortConditions.add(index.getTableName() + "." + index.getAttribute());
		InMemorySortOperator op = new InMemorySortOperator(sortConditions, new ScanOperator(index.getTableName()));
		Tuple currTuple = op.getNextTuple();
		while(currTuple != null) {
			bfw.writeNextTuple(currTuple);
			currTuple = op.getNextTuple();
		}
		
		bfw.writeNextTuple(null);
	}
}
