package utils;

import java.util.Comparator;

import indexing.Record;

public class RecordComparator implements Comparator<Record> {

	String attributeName;
	
	public RecordComparator(String attrName) {
		this.attributeName = attrName;
	}
	
	@Override
	public int compare(Record r1, Record r2) {
		return r1.getTuple().getValueForAttr(attributeName).compareTo(r2.getTuple().getValueForAttr(attributeName));
	}
}
