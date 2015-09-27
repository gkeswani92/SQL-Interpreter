package operators;

import java.util.HashSet;
import java.util.Set;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import utils.Tuple;

public class ProjectOperator extends Operator {
		
	String tableName;
	Operator getNextTupleOperator;
	Set<String> requiredColumns;
	PlainSelect body;

	public ProjectOperator(PlainSelect body) {
		this.tableName = body.getFromItem().toString();
		this.body = body;
				
		//Depending on whether the where clause is present or not, we decide to
		//the next tuple using scan or select
		if(body.getWhere()!=null)
			getNextTupleOperator = new SelectOperator(body);
		else
			getNextTupleOperator = new ScanOperator(tableName);		
		
		//Adding the columns from the query into the required Column hash set
		requiredColumns = new HashSet<String>();
		for(Object c: body.getSelectItems()) {
			SelectExpressionItem currentExpression = (SelectExpressionItem)c;
			requiredColumns.add(((Column) currentExpression.getExpression()).getColumnName());
		}
	}
	
	/**
	 * Gets the next tuple using the scan operator and keeps only the columns
	 * that were present in the query
	 */
	@Override
	public Tuple getNextTuple() {
		Tuple currentTuple = getNextTupleOperator.getNextTuple();
		
		//Only try to restrict on columns if the tuple exists. Exit with null
		//if it does not, since dump() will stop executing if null is returned
		if (currentTuple != null) {
			currentTuple = currentTuple.retainAttributes(requiredColumns);
			currentTuple.setIsSatisfies(true);
			return currentTuple;
		}
		return null;
	}

	@Override
	public void reset() {
		if(body.getWhere()!=null)
			getNextTupleOperator = new SelectOperator(body);
		else
			getNextTupleOperator = new ScanOperator(tableName);
	}
}
