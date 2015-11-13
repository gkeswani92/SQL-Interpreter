package operators;

import java.util.ArrayList;
import java.util.List;

import utils.Tuple;

/**
 * Extends operator to implement sort operator.
 * This is a blocking operator that gets all tuples from its child(just 1 child) and
 * sorts them in ascending order. Returns one tuple at a time.
 * @author Gaurav, Tanvi and Sahana (gk368,tmm259 and sv387)
 */
public abstract class SortOperator extends Operator {

	Operator child;
	List<String> sortConditions;
	List<Tuple> tuples;
	Integer currIndex;
	
	public SortOperator(Operator child, List<String> sortConditions,
			List<Tuple> tuples, Integer currIndex) {
		this.child = child;
		this.sortConditions = new ArrayList<String>();
		if (sortConditions != null) {
			this.sortConditions.addAll(sortConditions);
		}
		this.tuples = tuples;
		this.currIndex = currIndex;
	}
	
	public SortOperator(List<String> sortConditions, Operator child) {
		this.child = child;
		this.currIndex = 0;
		this.tuples = new ArrayList<Tuple>();
		this.sortConditions = new ArrayList<String>();
		
		if(sortConditions != null) {
			this.sortConditions.addAll(sortConditions);
		}
	}
	
	@Override
	public void reset() {
		child.reset();
		currIndex = 0;	
	}
}
