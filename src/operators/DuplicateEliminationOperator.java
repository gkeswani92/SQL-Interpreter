package operators;

import java.util.ArrayList;
import utils.Tuple;
import utils.TupleComparator;

/**
 * Extends the operator to implement the DISTINCT operation
 * Eliminates the duplicate tuples by using sort
 * @author Gaurav, Tanvi and Sahana (gk368,tmm259 and sv387) 
 * 
 */
public class DuplicateEliminationOperator extends Operator {
	
	private Operator child;
	private Tuple previousTuple;
	private TupleComparator comp;
	
	public DuplicateEliminationOperator(Operator child) {
		this.child = child;
	}
	
	@Override
	public Tuple getNextTuple() {
		Tuple currentTuple = child.getNextTuple();
		
		while(currentTuple != null) {
			
			//Sort tuples with the sortcondition set to all the attributes list
			comp = new TupleComparator(new ArrayList<String>(currentTuple.getArributeList()));
			
			if (previousTuple == null) {
				currentTuple.setIsSatisfies(true);
				previousTuple = currentTuple;
				return currentTuple;
			}
			else {
				//Compare the adjacent sorted tuples for duplicate elimination
				if(comp.compare(currentTuple, previousTuple) != 0) {
					currentTuple.setIsSatisfies(true);
					previousTuple = currentTuple;
					return currentTuple;
				}
				else
					currentTuple = child.getNextTuple();
			}
		}
		return null;
	}
	
	@Override
	public void reset() {
		child.reset();
	}

}
