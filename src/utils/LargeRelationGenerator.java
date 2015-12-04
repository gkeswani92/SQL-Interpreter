package utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Random;

public class LargeRelationGenerator {
	private Integer rows, cols;
	private String tableName;
	private String tablePath;
//	private String inputSrc = "/Users/tanvimehta/Desktop/CORNELL..YAY!!/Courses/CS5321/project2/samples/input";
//	private static String statsFile = "/Users/tanvimehta/Desktop/CORNELL..YAY!!/Courses/CS5321/project2/samples/input/db/stats.txt";
	
	private String inputSrc = "/Users/gaurav/Documents/Eclipse/SQL-Interpreter/samples/input";
	private static String statsFile = "/Users/gaurav/Documents/Eclipse/SQL-Interpreter/samples/input/db/stats.txt";
	
	private int[] highs, lows;
	
	public LargeRelationGenerator(String file, Integer rows, Integer cols, int[] highs, int[] lows) {
		this.tableName = file;
		this.tablePath = inputSrc + "/db/data/" + file;
		this.rows = rows;
		this.cols = cols;	
		this.highs = highs;
		this.lows = lows;
		DatabaseCatalog.getInstance().buildDbCatalog(inputSrc);
	}
	
	public void generateLargeRelation(){
		
		Random randomGenerator;
		BinaryFileWriter writer;
		
		try {
			writer = new BinaryFileWriter(this.tablePath);
			randomGenerator = new Random();
			
			// Tuple with all maximums to ensure at least 1 tuple has the maximum
			String tupleStr = "";
			for (int j=0; j<this.cols; j++) {
				tupleStr = tupleStr + highs[j] + ",";
			}
			Tuple maxTup = new Tuple(tupleStr, this.tableName);
			writer.writeNextTuple(maxTup);
			
			// Tuple with all minimums to ensure at least 1 tuple has the maximum
			tupleStr = "";
			for (int j=0; j<this.cols; j++) {
				tupleStr = tupleStr + lows[j] + ",";
			}
			
			Tuple minTup = new Tuple(tupleStr, this.tableName);
			writer.writeNextTuple(minTup);
			
			// Use max and min of each attribute in table and generate (rows-2) rows of random tuples.
			for (int i=0; i<this.rows-2; i++){
				tupleStr = "";
				for (int j=0; j<this.cols; j++){
					tupleStr = tupleStr  + (randomGenerator.nextInt((highs[j]+1)- lows[j]) + lows[j]) + ",";
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
	
	public static void main(String args[]) throws Exception{
		
		FileReader reader = new FileReader(statsFile);
		BufferedReader file = new BufferedReader(reader);
		
		// Read each line of the file and translate into indexes
		String line = file.readLine();
		while (line != null && !line.isEmpty()) {
			String[] parts = line.split(" ");
			int index = 0;
			int[] highs = new int[parts.length-2], lows = new int[parts.length-2];
			
			// Maintain the max and min values for each attribute in the table
			for (int i = 2; i < parts.length; i++) {
				String[] attrParts = parts[i].split(",");
				lows[index] = Integer.parseInt(attrParts[1]);
				highs[index] = Integer.parseInt(attrParts[2]);
				index++;
			}
			
			LargeRelationGenerator relation = new LargeRelationGenerator(parts[0], Integer.parseInt(parts[1]), (parts.length-2), highs, lows);
			relation.generateLargeRelation();
			line = file.readLine();
		}
		
		file.close();
	}
	
}