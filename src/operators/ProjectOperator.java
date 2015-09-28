package operators;

import java.util.HashSet;
import java.util.Set;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import parser.OperatorVisitor;
import utils.Tuple;

public class ProjectOperator extends Operator {
		
	String tableName;
	Operator child;
	Set<String> requiredColumns;
	PlainSelect body;

	public ProjectOperator(PlainSelect body, Operator child) {
		this.tableName = body.getFromItem().toString();
		this.body = body;
				
		//Depending on whether the where clause is present or not, we decide to
		//the next tuple using scan or select
//		if(body.getWhere()!=null)
//			child = new SelectOperator(body);
//		else
//			child = new ScanOperator(tableName);	
		this.child = child;
		
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
		Tuple currentTuple = child.getNextTuple();
		
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
		//TODO: FIX THIS WHEN YOU HAVE A CLUE!!!
//		if(body.getWhere()!=null)
//			child = new SelectOperator(body);
//		else
//			child = new ScanOperator(tableName);
	}
	
	@Override
	public void accept(OperatorVisitor visitor) {
		visitor.visit(this);
	}
	
    public Operator getChild() {
    	return child;
    }
}
