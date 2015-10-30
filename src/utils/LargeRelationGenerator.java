package utils;

import java.io.FileNotFoundException;
import java.util.Random;

public class LargeRelationGenerator {
	private Integer rows = 5000;
	private Integer cols = 4;	
	
	public LargeRelationGenerator(Integer rows, Integer cols) {
		super();
		this.rows = rows;
		this.cols = cols;		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
            //queriesFile = inputSrcDir+"/queries.sql";
		DatabaseCatalog.getInstance().buildDbCatalog("D:/Database_Practicals/SQL-Interpreter/samples/input");
		
		
		LargeRelationGenerator myRelation = new LargeRelationGenerator(4000,3);
		String valueString = "";
		String TableName = "Sailors";
		String FileName = "D:/Database_Practicals/SQL-Interpreter/samples/input/db/data/Sailors";
		Random randomGenerator = new Random();
		int randomInt;
		Tuple tuple ;
		try {
			BinaryFileWriter bfw = new BinaryFileWriter(FileName);
			for(int i=0;i<myRelation.rows;i++){	
				valueString="";
				for(int j=0;j<myRelation.cols;j++){
					randomInt = randomGenerator.nextInt(500);
					valueString = valueString + randomInt + ",";
				}
				tuple = new Tuple(valueString, TableName);
				bfw.writeNextTuple(tuple);
			}
			bfw.writeNextTuple(null);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
