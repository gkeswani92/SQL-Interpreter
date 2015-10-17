package operators;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import utils.Tuple;

/**
 * Extends the operator to implement the Project
 * Project operator filters the required attributes list values from the tuples
 * @author Gaurav, Tanvi and Sahana (gk368,tmm259 and sv387)
 *
 */
public class ProjectOperator extends Operator {
		
	private Operator child;
	private List<String> requiredColumns; 
	
	public ProjectOperator(Operator child, List<String> requiredColumns) {
		this.child = child;
		this.requiredColumns = requiredColumns;
	}
	
//	public ProjectOperator(PlainSelect body, Operator child) {
//		this.child = child;
//		this.requiredColumns = new ArrayList<String>();
//		@SuppressWarnings("unchecked")
//		List<SelectExpressionItem> selectColumns = body.getSelectItems();
//		
//		//Converting each select item to string
//		for(Object c: selectColumns) 
//			requiredColumns.add(c.toString());
//	}
	
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
		child.reset();
	}
	
    public Operator getChild() {
    	return child;
    }
    
    public void setChild(Operator child) {
    	this.child = child;
    }
    
    public void setRequiredColumns(List<String> requiredColumns) {
    	this.requiredColumns = requiredColumns;
    }
}
