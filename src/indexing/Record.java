package indexing;

import utils.Tuple;

public class Record {

	private Integer pageId, tupleId;
	private Tuple tuple;

	public Record (Integer pageId, Integer tupleId, Tuple tuple) {
		this.pageId = pageId;
		this.tupleId = tupleId;
		this.tuple = tuple;
	}
	
	public Integer getPageId() {
		return pageId;
	}

	public void setPageId(Integer pageId) {
		this.pageId = pageId;
	}

	public Integer getTupleId() {
		return tupleId;
	}

	public void setTupleId(Integer tupleId) {
		this.tupleId = tupleId;
	}

	public Tuple getTuple() {
		return tuple;
	}

	public void setTuple(Tuple tuple) {
		this.tuple = tuple;
	}
}
