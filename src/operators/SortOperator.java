package operators;

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
		this.sortConditions = sortConditions;
		this.tuples = tuples;
		this.currIndex = currIndex;
	}

	@Override
	public void reset() {
		child.reset();
		currIndex = 0;	
	}

}
