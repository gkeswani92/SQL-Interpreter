package utils;

import java.io.FileNotFoundException;
import java.util.Random;

public class LargeRelationGenerator {
	private Integer rows;
	private Integer cols;
	private String tableName;
	private String tablePath;
	private String inputSrc = "D:/Database_Practicals/SQL-Interpreter/samples/input";
	
	public LargeRelationGenerator(String file, Integer rows, Integer cols) {
		this.tableName = file;
		this.tablePath = inputSrc + "/db/data/" + file;
		this.rows = rows;
		this.cols = cols;		
		DatabaseCatalog.getInstance().buildDbCatalog(inputSrc);
	}

	public void generateLargeRelation(){
		
		Random randomGenerator;
		BinaryFileWriter writer;
		
		try {
			 writer = new BinaryFileWriter(this.tablePath);
			 randomGenerator = new Random();
		
			for(int i=0; i<this.rows; i++){
				String tupleStr = "";
				for(int j=0; j<this.cols; j++){
					tupleStr = tupleStr  + randomGenerator.nextInt(1000)+",";
				}
				Tuple tuple = new Tuple(tupleStr, this.tableName);
				writer.writeNextTuple(tuple);
			}
			writer.writeNextTuple(null);
			System.out.println("Created the random relation for "+this.tableName);
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String args[]){
		LargeRelationGenerator sailors = new LargeRelationGenerator("Sailors", 400, 3);
		LargeRelationGenerator boats = new LargeRelationGenerator("Boats", 400, 3);
		LargeRelationGenerator reserves = new LargeRelationGenerator("Reserves", 400, 2);
		sailors.generateLargeRelation();
		boats.generateLargeRelation();
		reserves.generateLargeRelation();
	}
	
}
