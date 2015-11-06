package logical_operators;

import indexing.Index;
import net.sf.jsqlparser.expression.Expression;
import operators.IndexScanOperator;
import operators.Operator;
import operators.SelectOperator;
import utils.IndexConfigFileReader;

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
//		return new SelectOperator(child.getNextPhysicalOperator(), this.whereClause);
		return new IndexScanOperator("Boats", 7, 10, IndexConfigFileReader.getInstance().getAllIndexes().get("Boats"));
	}    
}
