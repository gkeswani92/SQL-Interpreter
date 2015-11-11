package test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FileTupleComparator implements Comparator<FileTuple>{

	List<Integer> columns;
	public FileTupleComparator(List<Integer> conditions) {
		this.columns = new ArrayList<Integer>();
		this.columns.addAll(conditions);
	}
	
	@Override
	public int compare(FileTuple o1, FileTuple o2) {
		int n = 10;
		for (Object column : columns) {
			n = o1.getValueForAttr(Integer.parseInt(column.toString())).compareTo(o2.getValueForAttr(Integer.parseInt(column.toString())));
			if (n != 0) {
				return n;
			}
		}
		return n;
	}
}
