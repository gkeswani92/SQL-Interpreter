package operators;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import utils.Tuple;
import utils.BinaryFileReader;
import utils.DatabaseCatalog;

/**
 * Extends operator to implement the scan
 * Scan operator that reads the tuples from the given file
 * @author Gaurav, Tanvi and Sahana (gk368,tmm259 and sv387)
 *
 */

public class ScanOperator extends Operator{
	
	private String tableName; 
	private String alias;
	private String filePath;
	BinaryFileReader bfr;
	
	public ScanOperator(String tableName, String alias) {
		this.tableName = tableName;
		this.alias = alias;
		try {
			bfr = new BinaryFileReader(tableName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
//	//Constructor that initializes the fileReaderObj object
//	// to read data from the file system
//	public ScanOperator(PlainSelect body) {
//		this.tableName = body.getFromItem().toString();
//		if(body.getFromItem().getAlias()!=null){
//			this.tableName = updateCatalogForAlias(this.tableName,body.getFromItem().getAlias());
//			alias = body.getFromItem().getAlias();
//		}
//		filePath = DatabaseCatalog.getInstance().getDataFilePath(this.tableName);
//		try {
//			fileReaderObj = new FileReader(filePath);
//			file = new BufferedReader(fileReaderObj);
//		} 
//		catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//	}
	
	//Overloaded constructor to handle the alias scan references 
	public ScanOperator(String tableName) {
		this.tableName = tableName;
		
		BinaryFileReader bfr;
		try {
			bfr = new BinaryFileReader(tableName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
//		filePath = DatabaseCatalog.getInstance().getDataFilePath(this.tableName);
//		try {
//			fileReaderObj = new FileReader(filePath);
//			file = new BufferedReader(fileReaderObj);
//		} 
//		catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
	}
	
	// When the table is aliased add additional entry for the alias as key with 
	// the copy of the base table values to be used for references made
	private String updateCatalogForAlias(String tableName,String alias) {
		// TODO Auto-generated method stub
		if(tableName.contains("AS")){
			String baseTable = tableName.substring(0,tableName.indexOf(" "));
			DatabaseCatalog.getInstance().setEntryForAlias(baseTable, alias);
			return tableName.substring(0,tableName.indexOf(" "));
		}else{
			return tableName;
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
			bfr = new BinaryFileReader(tableName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
