package operators;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.statement.select.OrderByElement;
import utils.Tuple;
import utils.TupleComparator;

public class SortOperator extends Operator {

	Operator child;
	List<OrderByElement> sortConditions;
	List<Tuple> tuples;
	Integer currIndex;
	
	public SortOperator(List<OrderByElement> sortConditions, Operator child) {
		this.child = child;
		this.sortConditions = sortConditions;
		this.currIndex = 0;
		this.tuples = new ArrayList<Tuple>();
	}
	
	@Override
	public Tuple getNextTuple() {
		
		if (tuples.isEmpty()) {
			Tuple currTuple = child.getNextTuple();
			while (currTuple != null) {
				tuples.add(currTuple);
				currTuple = child.getNextTuple();
			}
			
			tuples.sort(new TupleComparator(sortConditions));
		}
		
		if (currIndex < tuples.size()) {
			return tuples.get(currIndex++);
		} else {
			return null;
		}	
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

}
