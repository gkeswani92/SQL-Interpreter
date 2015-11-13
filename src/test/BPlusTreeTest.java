package test;

import java.io.FileNotFoundException;
import utils.BinaryFileWriter;
import utils.DatabaseCatalog;
import utils.Tuple;

public class BPlusTreeTest {
	private Integer rows;
	private String tableName;
	private String tablePath;
	private String inputSrc = "/Users/tanvimehta/Desktop/CORNELL..YAY!!/Courses/CS5321/project2/samples/input";
//	private String inputSrc = "/Users/gaurav/Documents/Eclipse/SQL-Interpreter/samples/input";
	
	public BPlusTreeTest(String file, Integer rows, Integer cols) {
		this.tableName = file;
		this.tablePath = inputSrc + "/db/data/" + file;
		this.rows = rows;		
		DatabaseCatalog.getInstance().buildDbCatalog(inputSrc);
	}

	public void generateLargeRelation(){
		
		BinaryFileWriter writer;
		
		int[] numbers = {5, 1, 7, 12, 11, 2, 8, 14, 13, 4};

		
		try {
			 writer = new BinaryFileWriter(this.tablePath);
			 //Random randomGenerator = new Random();
		
			for(int i=0; i< this.rows; i++){
				String tupleStr = numbers[i]+","+numbers[i]+","+numbers[i];
				Tuple tuple = new Tuple(tupleStr, this.tableName);
				writer.writeNextTuple(tuple);
			}
			writer.writeNextTuple(null);
			System.out.println("Created the sample relation for "+this.tableName);
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]){
		BPlusTreeTest boats = new BPlusTreeTest("Boats", 10, 3);
		boats.generateLargeRelation();
	}
	
}