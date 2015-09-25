package operators;

import java.util.Stack;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import parser.ExpressionEvaluator;
import utils.Tuple;

/**
 * Created by tanvimehta on 15-09-24.
 */
public class SelectOperator extends Operator {
	
	ScanOperator scan;
	Expression whereClause;
	String tableName;
	PlainSelect body;

	Stack<Expression> stack = new Stack<>();
	Stack<Expression> replaceValues = new Stack<>();
	
	public SelectOperator(PlainSelect b) {
		body = b;
		tableName = body.getFromItem().toString();
		scan = new ScanOperator(tableName);
		whereClause = body.getWhere();
	}
	
    @Override
    public Tuple getNextTuple() {
        Tuple currentTuple = scan.getNextTuple();
    	while (currentTuple != null) {
	        traverseTree(currentTuple, whereClause);
	        reverseStack();
	        replaceColumnWithValues(whereClause);
	        ExpressionEvaluator ob = new ExpressionEvaluator();
	        whereClause.accept(ob);
	        if (((BinaryExpression)whereClause).isNot()) {
	        	return currentTuple;
	        }
	        currentTuple = scan.getNextTuple();
    	}
    	
    	return null;
    }

    @Override
    public void reset() {
    	scan = new ScanOperator(tableName);
    }
    
    public void reverseStack () {
    	while (!stack.isEmpty()) {
    		replaceValues.push(stack.pop());
    	}
    }
    
    public void traverseTree (Tuple currTuple, Expression root) {
    	
    	if (root instanceof Column) {
    		stack.push(new LongValue(currTuple.getValueForAttr(((Column) root).getColumnName())));
    		return;
    	}
    	
    	if (root instanceof LongValue) {
    		return;
    	}
    	
    	if (root instanceof BinaryExpression) {
    		traverseTree(currTuple, ((BinaryExpression) root).getLeftExpression());
    		traverseTree(currTuple, ((BinaryExpression) root).getRightExpression());
    	}
    }
    
    public void replaceColumnWithValues (Expression root) {
    	
    	if (root instanceof BinaryExpression) {
    		if (((BinaryExpression) root).getLeftExpression() instanceof Column) {
    			((BinaryExpression) root).setLeftExpression(replaceValues.pop());
    			return;
    		} else {
    			replaceColumnWithValues(((BinaryExpression) root).getLeftExpression());
    		}
    		
    		if (((BinaryExpression) root).getRightExpression() instanceof Column) {
    			((BinaryExpression) root).setRightExpression(replaceValues.pop());
    			return;
    		} else {
    			replaceColumnWithValues(((BinaryExpression) root).getRightExpression());
    		}  		
    	} else {
    		return;
    	}
    }
}
