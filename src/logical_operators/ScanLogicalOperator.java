package logical_operators;

import net.sf.jsqlparser.statement.select.PlainSelect;
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
		tableName = body.getFromItem().toString();
		if(body.getFromItem().getAlias()!=null) {
			updateCatalogForAlias(tableName,body.getFromItem().getAlias());
			tableName = body.getFromItem().getAlias();
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
	
	public String getTableName() {
		return tableName;
	}

	@Override
	public String getLogicalPlanToString(Integer level) {
		String plan = "";
		
		// Level
		if (level > 0) {
			for (int i = 0; i < level; i++) {
				plan = plan + "-";
			}
		}
		
		String table = tableName;
		if (DatabaseCatalog.getInstance().getTableForAlias(table) != null) {
			table = DatabaseCatalog.getInstance().getTableForAlias(table);
		}
		
		plan = plan + "Leaf[" + table + "]\n";
		return plan;
	}
}
