package operators;

import utils.Tuple;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ScanOperator extends Operator{
	
	private String tableName; 
	private FileReader fileReaderObj;
	private BufferedReader file;
	private static final String filePath = "samples/input/db/data/Boats.csv"; //TODO: Remove the hard coding
	
	public ScanOperator(String tableName) {
		this.tableName = tableName;
		try {
			fileReaderObj = new FileReader(filePath);
			file = new BufferedReader(fileReaderObj);
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Tuple getNextTuple() {
		
		Tuple currentTuple = null;
		try {
			String line = file.readLine();
			if(line==null){
				return currentTuple;
			}
			currentTuple = new Tuple(line, tableName); 			
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		return currentTuple;
	}

	@Override
	public void dump() {
		Tuple currentTuple = getNextTuple();
		String tableDump = new String(currentTuple.toStringAttributes()+"\n");
		while(currentTuple != null) {
			tableDump = tableDump + currentTuple.toStringValues() +  "\n";
			currentTuple = getNextTuple();
		}
		System.out.println(tableDump);
	}

	@Override
	public void reset() {
		//Closing the current file
		try {
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Opens the same file again to reset the pointer to the start
		try {
			fileReaderObj = new FileReader(filePath);
			file = new BufferedReader(fileReaderObj);
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ScanOperator op = new ScanOperator("Boats");
		System.out.println(op.getNextTuple().toStringValues());
		System.out.println(op.getNextTuple().toStringValues());
		op.reset();
		System.out.println(op.getNextTuple().toStringValues());
		System.out.println(op.getNextTuple().toStringValues());
	}
}
