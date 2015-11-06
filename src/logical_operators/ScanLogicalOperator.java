package logical_operators;

import net.sf.jsqlparser.statement.select.PlainSelect;
import operators.IndexScanOperator;
import operators.Operator;
import operators.ScanOperator;
import utils.DatabaseCatalog;

public class ScanLogicalOperator extends LogicalOperator {

	private String tableName; 
	private String alias;
	
	public ScanLogicalOperator(String tableName) {
		this.tableName = tableName;
	}
	
	public ScanLogicalOperator(PlainSelect body) {
		this.tableName = body.getFromItem().toString();
		if(body.getFromItem().getAlias()!=null) {
			this.tableName = updateCatalogForAlias(this.tableName,body.getFromItem().getAlias());
			this.alias = body.getFromItem().getAlias();
		}
	}
	
	private String updateCatalogForAlias(String tableName,String alias) {
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
		return new ScanOperator(this.tableName, this.alias);
	}
}
