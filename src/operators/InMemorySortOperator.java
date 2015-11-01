package operators;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import utils.Tuple;
import utils.TupleComparator;

public class InMemorySortOperator extends SortOperator {

	public InMemorySortOperator(Operator child, List<String> sortConditions, List<Tuple> tuples, Integer currIndex) {
		super(child, sortConditions, tuples, currIndex);
	}
	
	public InMemorySortOperator(List<String> sortConditions, Operator child) {
		super(sortConditions, child);
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
			
			if (!tuples.isEmpty()) {
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
		}
	
		///////////////////////////////////////////////////////////////
//		String tableDump = "";
//		for (Tuple test: tuples) {
//			tableDump = tableDump + test.toStringValues() +  "\n";	
//		}
//		
//		PrintWriter out = null;
//		try {
//			out = new PrintWriter("inMemorySort.txt");
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		out.write(tableDump);
//		out.close();
		///////////////////////////////////////////////////////////////
		
		// Return one tuple at a time
		if (currIndex < tuples.size()) {
			return tuples.get(currIndex++);
		} else {
			return null;
		}	
	}

	@Override
	public void reset(int index) {
		currIndex = index;
	}
}
