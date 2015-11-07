package operators;

import java.io.FileNotFoundException;

import utils.Tuple;
import utils.BinaryFileReader;

/**
 * Extends operator to implement the scan
 * Scan operator that reads the tuples from the given file
 * @author Gaurav, Tanvi and Sahana (gk368,tmm259 and sv387)
 *
 */

public class ScanOperator extends Operator{
	
	private String tableName; 
	private String alias;
	private BinaryFileReader bfr;
	
	public ScanOperator(String tableName, String alias) {
		this.tableName = tableName;
		this.alias = alias;
		try {
			bfr = new BinaryFileReader(tableName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	//Overloaded constructor to handle the alias scan references 
	public ScanOperator(String tableName) {
		this.tableName = tableName;
		
		try {
			bfr = new BinaryFileReader(tableName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Gets the next tuple in the table
	 */
	@Override
	public Tuple getNextTuple() {
		Tuple tuple = null;
		tuple = bfr.getNextTuple();
		
		if(tuple == null){
			bfr.closeStuff();
			return tuple;
		}
		
		if(alias != null)
			tuple.updateTuple(alias); 	
		else 
			tuple.updateTuple(tableName);
		
		return tuple;
	}

	/**
	 * Resets the scan operator to the first tuple in the table
	 */
	@Override
	public void reset() {
		try {
			bfr.closeStuff();
			bfr = new BinaryFileReader(tableName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
