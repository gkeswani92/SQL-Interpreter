package indexing;

/**
 * Class to keep track of index objects
 * @author tanvimehta
 *
 */
public class Index {
	
	String tableName, attribute;
	int flag, order;
	Integer numLeaves;
	
	public Index (String tableName, String attribute, int flag, int order) {
		this.tableName = tableName;
		this.attribute = attribute;
		this.flag = flag;
		this.order = order;
		this.numLeaves = 0;
	}
	
	public void setNumLeaves(Integer numLeaves){
		this.numLeaves = numLeaves;
	}
	
	public Integer getNumLeaves(){
		return numLeaves;
	}
	public String getTableName() {
		return tableName;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public String getAttribute() {
		return attribute;
	}
	
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	
	public Integer getFlag() {
		return flag;
	}
	
	public void setFlag(Integer flag) {
		this.flag = flag;
	}
	
	public Integer getOrder() {
		return order;
	}
	
	public void setOrder(Integer order) {
		this.order = order;
	}
}
