package utils;

import java.util.List;

public class JoinTupleComparator {

	/*
	 * For merge sort join, comparing 2 tuples based on different column names
	 */
	List<String> leftColumns;
	List<String> rightColumns;
	public JoinTupleComparator(List<String> leftConditions, List<String> rightConditions) {
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
		//TODO: MAKE SURE THIS IS SUPPOSED TO BE HERE!!!!!
		if (o1 == null || o2 == null) {
			return -1;
		}
		
		for (int i = 0; i < leftColumns.size(); i++) {

			Integer left = o1.getValueForAttr(leftColumns.get(i).toString());
			
			for (int j = 0; j < rightColumns.size(); j++) {
				
				if (o2 == null) 
					return -1;
				else if (o1 == null)
					return -2;
				
				Integer right = o2.getValueForAttr(rightColumns.get(j).toString());
				n = left.compareTo(right);
	
				if (n != 0) 
					return n;
			}
		}
		return n;
	}
}
