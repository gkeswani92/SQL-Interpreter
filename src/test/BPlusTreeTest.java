package test;

import java.io.FileNotFoundException;
import java.util.Random;
import utils.BinaryFileWriter;
import utils.DatabaseCatalog;
import utils.Tuple;

public class BPlusTreeTest {
	private Integer rows;
	private Integer cols;
	private String tableName;
	private String tablePath;
	//private String inputSrc = "/Users/tanvimehta/Desktop/CORNELL..YAY!!/Courses/CS5321/project2/samples/input";
	private String inputSrc = "/Users/gaurav/Documents/Eclipse/SQL-Interpreter/samples/input";
	
	public BPlusTreeTest(String file, Integer rows, Integer cols) {
		this.tableName = file;
		this.tablePath = inputSrc + "/db/data/" + file;
		this.rows = rows;
		this.cols = cols;		
		DatabaseCatalog.getInstance().buildDbCatalog(inputSrc);
	}

	public void generateLargeRelation(){
		
		Random randomGenerator;
		BinaryFileWriter writer;
		
		//int[] numbers = new int[]{1, 2, 4, 5, 7, 8, 11, 12, 13, 14};
		int[] numbers = new int[]{3,
				4,
				6,
				9,
				10,
				11,
				12,
				13,
				20,
				22,
				23,
				31,
				35,
				36,
				38,
				41,
				44};
		
		try {
			 writer = new BinaryFileWriter(this.tablePath);
			 randomGenerator = new Random();
		
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
		BPlusTreeTest boats = new BPlusTreeTest("Boats", 17, 3);
		boats.generateLargeRelation();
	}
	
}