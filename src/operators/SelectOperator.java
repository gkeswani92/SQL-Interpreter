package operators;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.PlainSelect;
import utils.Tuple;

/**
 * Created by tanvimehta on 15-09-24.
 */
public class SelectOperator extends Operator {
	
	ScanOperator scan;
	Expression whereClause;
	
	public SelectOperator(PlainSelect body) {
		String tableName = body.getFromItem().toString();
		scan = new ScanOperator(tableName);
		whereClause = body.getWhere();
	}
	
    @Override
    public Tuple getNextTuple() {
        Tuple currentTuple = scan.getNextTuple();
        
        return null;
    }

    @Override
    public void dump() {

    }

    @Override
    public void reset() {

    }
}
