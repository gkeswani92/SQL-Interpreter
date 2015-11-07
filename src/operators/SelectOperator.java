package operators;

import expression_visitors.ExpressionEvaluator;
import net.sf.jsqlparser.expression.Expression;
import utils.Tuple;

/**
 * Extends operator to implement select operator.
 * Has 1 child. Returns the next tuple that satisfies the selection condition.
 * Evaluates the selection condition by calling an evaluate visitor class.
 * @author Gaurav, Tanvi and Sahana (gk368,tmm259 and sv387)
 *
 */
public class SelectOperator extends Operator {
	
	Operator child;
	Expression whereClause;
	
	public SelectOperator(Operator child, Expression whereClause) {
		this.child = child;
		this.whereClause = whereClause;
	}
	
	/**
	 * Gets the next tuple that satisfies the where conditions.
	 * Returns null if either at the end of the table OR no tuple satisfies condition.
	 */
    @Override
    public Tuple getNextTuple() {
        Tuple currentTuple = child.getNextTuple();
        
        // Update tuple with tableName if child is an indexScanOperator
        if (child instanceof IndexScanOperator) {
        	IndexScanOperator isc = (IndexScanOperator)child;
        	currentTuple.updateTuple(isc.getTableName());
        }
        
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
    	child.reset();
    }
    
    public Expression getExpression(){
    	return whereClause;
    }
}
