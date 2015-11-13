package indexing;

import utils.Tuple;

/**
 * Record object that contains information about the pageId and tupleId.
 * These are stored in the leaves of the BPlusTree structure depending on the key values.
 * @author tanvimehta
 *
 */
public class Record {

	private Integer pageId, tupleId;
	private Tuple tuple;

	public Record(Integer pageId, Integer tupleId, Tuple tuple) {
		this.pageId = pageId;
		this.tupleId = tupleId;
		this.tuple = tuple;
	}
	
	public Record(Integer pageId, Integer tupleId){
		this.pageId = pageId;
		this.tupleId = tupleId;
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
