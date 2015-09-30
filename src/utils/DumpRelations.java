package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import operators.Operator;

public class DumpRelations {
	
	PrintWriter writer;
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
		
		if(tableDump != null) {
			try {
				writer = new PrintWriter(filename);
				writer.println(tableDump.trim());
				writer.close();
			} 
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
