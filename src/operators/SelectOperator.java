package operators;

import net.sf.jsqlparser.expression.Expression;
import parser.ExpressionEvaluator;
import utils.Tuple;

/**
 * Extends operator to implement select operator.
 * Has 1 child. Returns the next tuple that satisfies the selection condition.
 * Evaluates the selection condition by calling an evaluate visitor class.
 * @author tmm259
 *
 */
public class SelectOperator extends Operator {
	
	Operator child;
	Expression whereClause;
	String tableName;
	
	public SelectOperator(Expression exp, Operator child) {
		this.child = child;
		whereClause = exp;
	}
	
	/**
	 * Gets the next tuple that satisfies the where conditions.
	 * Returns null if either at the end of the table OR no tuple satisfies condition.
	 */
    @Override
    public Tuple getNextTuple() {
        Tuple currentTuple = child.getNextTuple();
    	while (currentTuple != null) {
	        ExpressionEvaluator ob = new ExpressionEvaluator(currentTuple);
	        whereClause.accept(ob);
	        // Check if tuple satisfies the select condition
	        if (currentTuple.getIsSatisfies()) {
	        	return currentTuple;
	        }
	        currentTuple = child.getNextTuple();
    	}	
    	return null;
    }
    
    @Override
    public void reset() {
    	child = new ScanOperator(tableName);
    }
    
    public Expression getExpression(){
    	return whereClause;
    }
}
