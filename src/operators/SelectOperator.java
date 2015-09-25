package operators;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.PlainSelect;
import parser.ExpressionEvaluator;
import utils.Tuple;

/**
 * 
 * Created by tanvimehta on 15-09-24.
 */
public class SelectOperator extends Operator {
	
	ScanOperator scan;
	Expression whereClause;
	String tableName;
	
	public SelectOperator(PlainSelect body) {
		String tableName = body.getFromItem().toString();
		scan = new ScanOperator(tableName);
		whereClause = body.getWhere();
	}
	
	/**
	 * Gets the next tuple that satisfies the where conditions.
	 * Returns null if either at the end of the table OR no tuple satisfies condition.
	 */
    @Override
    public Tuple getNextTuple() {
        Tuple currentTuple = scan.getNextTuple();
    	while (currentTuple != null) {
	        ExpressionEvaluator ob = new ExpressionEvaluator(currentTuple);
	        whereClause.accept(ob);
	        if (currentTuple.getIsSatisfies()) {
	        	return currentTuple;
	        }
	        currentTuple = scan.getNextTuple();
    	}
    	
    	return null;
    }

    /**
     * Resets the operator to the start of the table
     */
    @Override
    public void reset() {
    	scan = new ScanOperator(tableName);
    }
}
