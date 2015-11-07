package logical_operators;

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
		// If child is a scan, then can possibly use index
		if (child instanceof ScanLogicalOperator) {
			
			// Create an expression visitor to categorize expressions into index usable and non index usable
			
			// If both lists have expressions, create index scan for index usable expressions and set it as a child to selectOperator
			// for non index usable.
			
			return new IndexScanOperator("Boats", null, 20, IndexConfigFileReader.getInstance().getAllIndexes().get("Boats"));
		}
		
		// Create selectOperator for non index usable. Will only come here if no index usable expressions exist.
		return new SelectOperator(child.getNextPhysicalOperator(), this.whereClause);
	}    
}
