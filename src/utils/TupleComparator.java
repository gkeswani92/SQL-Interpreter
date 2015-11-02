package utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Compares tuples using the list of conditions passed into the constructor.
 * Conditions specify the column names to sort on.
 * @author Gaurav, Tanvi and Sahana (gk368,tmm259 and sv387)
 */
public class TupleComparator implements Comparator<Tuple>{

	List<String> columns;
	public TupleComparator(List<String> conditions) {
		this.columns = new ArrayList<String>();
		this.columns.addAll(conditions);
	}
	
	@Override
	public int compare(Tuple o1, Tuple o2) {
		int n = 10;
		for (Object column : columns) {
			n = o1.getValueForAttr(column.toString()).compareTo(o2.getValueForAttr(column.toString()));
			if (n != 0) {
				return n;
			}
		}
		return n;
	}
}
