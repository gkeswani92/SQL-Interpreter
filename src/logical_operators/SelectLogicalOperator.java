package logical_operators;

import net.sf.jsqlparser.expression.Expression;
import operators.Operator;
import operators.SelectOperator;

public class SelectLogicalOperator extends LogicalOperator {

	LogicalOperator child;
	Expression whereClause;
	
	public SelectLogicalOperator(Expression exp, LogicalOperator child) {
		this.child = child;
		whereClause = exp;
	}
	
    public Expression getExpression(){
    	return whereClause;
    }

	@Override
	public Operator getNextPhysicalOperator() {
		return new SelectOperator(child.getNextPhysicalOperator(), this.whereClause);
	}    
}
