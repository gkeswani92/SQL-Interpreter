package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

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
		this.filepath =  outputDir+"/query";		
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
		String filename = this.filepath + queryNumber.toString();
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
}