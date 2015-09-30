package operators;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import net.sf.jsqlparser.statement.select.PlainSelect;
import utils.Tuple;
import utils.DatabaseCatalog;

public class ScanOperator extends Operator{
	
	private String tableName; 
	private String alias;
	private FileReader fileReaderObj;
	private BufferedReader file;
	private String filePath;
	
	public ScanOperator(PlainSelect body) {
		this.tableName = body.getFromItem().toString();
		if(body.getFromItem().getAlias()!=null){
			this.tableName = updateCatalogForAlias(this.tableName,body.getFromItem().getAlias());
			alias = body.getFromItem().getAlias();
		}
		filePath = DatabaseCatalog.getInstance().getDataFilePath(this.tableName);
		try {
			fileReaderObj = new FileReader(filePath);
			file = new BufferedReader(fileReaderObj);
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public ScanOperator(String tableName) {
		this.tableName = tableName;
		
		filePath = DatabaseCatalog.getInstance().getDataFilePath(this.tableName);
		try {
			fileReaderObj = new FileReader(filePath);
			file = new BufferedReader(fileReaderObj);
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
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
		
		Tuple currentTuple = null;
		try {
			String line = file.readLine();
			if(line==null){
				return currentTuple;
			}
			if(alias != null)
				currentTuple = new Tuple(line, alias); 	
			else 
				currentTuple = new Tuple(line, tableName); 	
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		return currentTuple;
	}

	/**
	 * Resets the scan operator to the first tuple in the table
	 */
	@Override
	public void reset() {
		//Closing the current file
		try {
			file.close();
		} catch (IOException e) {
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
}
