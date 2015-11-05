package indexing;

public class Node {
	protected boolean isLeafNode;
	protected Integer address;
	
	public boolean isLeafNode(){
		return isLeafNode;
	}
	
	public void setAddress(Integer address) {
		this.address = address;
	}
	
	public Integer getAddress() {
		return address;
	}
}
