package utils;

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
		this.columns = conditions;
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
	
	/*
	 * For merge sort join, comparing 2 tuples based on different column names
	 */
	List<String> leftColumns;
	List<String> rightColumns;
	public TupleComparator(List<String> leftConditions, List<String> rightConditions) {
		this.leftColumns = leftConditions;
		this.rightColumns = rightConditions;
	}
	
	/**
	 * Compare two tuples based on values in different columns
	 * @param o1 tuple1
	 * @param o2 tuple2
	 * @return -1 if o1 < o2, 1 if o1 > o2, 0 if equal
	 */
	public int joinCompare(Tuple o1, Tuple o2) {
		int n = 10;
		for (int i = 0; i < leftColumns.size(); i++) {
			Integer left = o1.getValueForAttr(leftColumns.get(i).toString());
			if (o2 == null) {
				return -1;
			}
			Integer right = o2.getValueForAttr(rightColumns.get(i).toString());
			n = left.compareTo(right);
			if (n != 0) {
				return n;
			}
		}
		return n;
	}

}
