package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import operators.Operator;

/**
 * Utility to print out the query output to a the specified 
 * output folder.
 * @author Gaurav, Tanvi and Sahana (gk368,tmm259 and sv387)
 *
 */

public class DumpRelations {
	
	PrintWriter writer;
	BinaryFileWriter binaryWriter;
	String filepath;
	
	public DumpRelations(String outputDir) {
		new File(outputDir).mkdir();
		this.filepath =  outputDir;		
	}
	
	public void writeRelationToFile(Operator root, Integer queryNumber) {
		String filename = this.filepath + queryNumber.toString();
		Tuple currentTuple = root.getNextTuple();
		String tableDump = "";
		
		while(currentTuple != null) {
			tableDump = tableDump + currentTuple.toStringValues() +  "\n";
			currentTuple = root.getNextTuple();
		}		
		try {
			writer = new PrintWriter(filename);
			writer.println(tableDump.trim());
			writer.close();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void writeRelationToBinaryFile(Operator root, Integer queryNumber){
		
		String filename = this.filepath + "/query" + queryNumber.toString();
		Tuple currentTuple = root.getNextTuple();
		
		try{
			binaryWriter = new BinaryFileWriter(filename);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		while(currentTuple != null) {
			binaryWriter.writeNextTuple(currentTuple);
			currentTuple = root.getNextTuple();
		}
		binaryWriter.writeNextTuple(null);
	}
	
	public void writeTestFile(Operator root, Integer queryNumber, String writeMethod) {
		
		PlanBuilderConfigFileReader config = PlanBuilderConfigFileReader.getInstance();
		String filename = this.filepath;
		
		//Decides which folder to put it in
		if(config.getSortType() == 0)
			filename += "/In-Memory/query" + queryNumber.toString();
		else
			filename += "/External/query" + queryNumber.toString();

		//Decides which method to use to write the output
		if(writeMethod.equals("Binary")){
			try{
				Tuple currentTuple = root.getNextTuple();
				binaryWriter = new BinaryFileWriter(filename);
				while(currentTuple != null) {
					binaryWriter.writeNextTuple(currentTuple);
					currentTuple = root.getNextTuple();
				}
				binaryWriter.writeNextTuple(null);
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		//If the write method is human readable
		else{
			try {
				Tuple currentTuple = root.getNextTuple();
				String tableDump = null;
				if (currentTuple != null)
					tableDump = new String(currentTuple.toStringAttributes()+"\n");
				while(currentTuple != null) {
					tableDump = tableDump + currentTuple.toStringValues() +  "\n";
					currentTuple = root.getNextTuple();
				}
				FileWriter writer = new FileWriter(filename);
				if(tableDump!=null)
					writer.write(tableDump);
				writer.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
}