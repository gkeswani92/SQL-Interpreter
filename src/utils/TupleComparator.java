package utils;

import java.util.Comparator;
import java.util.List;

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

}
