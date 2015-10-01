package operators;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.statement.select.OrderByElement;
import utils.Tuple;
import utils.TupleComparator;

/**
 * Extends operator to implement sort operator.
 * This is a blocking operator that gets all tuples from its child(just 1 child) and
 * sorts them in ascending order. Returns one tuple at a time.
 * @author Gaurav, Tanvi and Sahana (gk368,tmm259 and sv387)
 */
public class SortOperator extends Operator {

	Operator child;
	List<String> sortConditions;
	List<Tuple> tuples;
	Integer currIndex;
	
	public SortOperator(List<OrderByElement> sortConditions, Operator child) {
		this.child = child;
		this.currIndex = 0;
		this.tuples = new ArrayList<Tuple>();
		this.sortConditions = new ArrayList<String>();
		
		if(sortConditions != null) {
			for (OrderByElement el : sortConditions) {
				this.sortConditions.add(el.toString());				
			}
		}
	}
	
	@Override
	public Tuple getNextTuple() {
		
		// Get all tuples from child
		if (tuples.isEmpty()) {
			Tuple currTuple = child.getNextTuple();
			while (currTuple != null) {
				tuples.add(currTuple);
				currTuple = child.getNextTuple();
			}
			
			// If query has no sort condition (in case of distinct), sort using all attributes
			// For further explanation refer DuplicateElimationOperator.java
			if (sortConditions.isEmpty()) {
				sortConditions = new ArrayList<String>(tuples.get(0).getArributeList());
			} else {
				// Adds all remaining attributes that aren't already sort conditions to the sort conditions
				// Preserves order of attributes in the tuple
				List<String> attributes = new ArrayList<String>(tuples.get(0).getArributeList());
				
				for (String sort: sortConditions) {
					if (attributes.contains(sort)) {
						attributes.remove(sort);
					}
				}
				sortConditions.addAll(attributes);
			}
			// Sort using tuple comparator
			tuples.sort(new TupleComparator(sortConditions));
		}
		
		// Return one tuple at a time
		if (currIndex < tuples.size()) {
			return tuples.get(currIndex++);
		} else {
			return null;
		}	
	}

	@Override
	public void reset() {
		child.reset();
		currIndex = 0;	
	}

}
