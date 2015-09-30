package operators;

import java.util.ArrayList;
import utils.Tuple;
import utils.TupleComparator;

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
			comp = new TupleComparator(new ArrayList<String>(currentTuple.getArributeList()));
			
			if (previousTuple == null) {
				currentTuple.setIsSatisfies(true);
				previousTuple = currentTuple;
				return currentTuple;
			}
			else {
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
