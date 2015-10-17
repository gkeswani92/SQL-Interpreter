package logical_operators;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import net.sf.jsqlparser.statement.select.PlainSelect;
import operators.Operator;
import operators.ScanOperator;
import utils.DatabaseCatalog;

public class ScanLogicalOperator extends LogicalOperator {

	private String tableName; 
	private String alias;
	private FileReader fileReaderObj;
	private BufferedReader file;
	private String filePath;
	
	public ScanLogicalOperator(String tableName) {
		this.tableName = tableName;
	}
	
	public ScanLogicalOperator(PlainSelect body) {
		this.tableName = body.getFromItem().toString();
		if(body.getFromItem().getAlias()!=null) {
			this.tableName = updateCatalogForAlias(this.tableName,body.getFromItem().getAlias());
			this.alias = body.getFromItem().getAlias();
		}
		filePath = DatabaseCatalog.getInstance().getDataFilePath(this.tableName);
		try {
			fileReaderObj = new FileReader(filePath);
			this.file = new BufferedReader(fileReaderObj);
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

	@Override
	public Operator getNextPhysicalOperator() {
		return new ScanOperator(this.tableName, this.alias,
				this.fileReaderObj, this.file, this.filePath);
	}
}
