package indexing;

import java.util.ArrayList;
import java.util.List;

public class IndexNode extends Node {
	
	List<Integer> keys;
	List<Node> children;

	public IndexNode(Node child) {
		keys = new ArrayList<Integer>();
		children = new ArrayList<Node>();
		children.add(child);
	}

	public IndexNode(Integer key, Node child) {
		keys = new ArrayList<Integer>();
		keys.add(key);
		children = new ArrayList<Node>();
		children.add(child);
	}

	public List<Integer> getKeys() {
		return keys;
	}

	public void setKeys(List<Integer> keys) {
		this.keys = keys;
	}

	public List<Node> getChildren() {
		return children;
	}

	public void setChildren(List<Node> children) {
		this.children = children;
	}
	
	public void addKey(Integer key) {
		keys.add(key);
	}
	
	public void addChild(Node child) {
		children.add(child);
	}
}
