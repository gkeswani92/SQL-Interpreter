package union_find;

import java.util.ArrayList;
import java.util.List;

public class UnionFindElement {
	
	private List<String> attributes;
	private Long lowerBound, upperBound, equalityConstraint;
	
	public UnionFindElement(){
		attributes = new ArrayList<String>();
		lowerBound = null;
		upperBound = null; 
		equalityConstraint = null;
	}
	
	public UnionFindElement(String attr){
		this();
		attributes.add(attr);
	}
	
	/**
	 * Adds the passed in attribute to the union find element
	 * @param attr
	 */
	public void addAttributeToElement(String attr){
		attributes.add(attr);
	}
	
	/**
	 * Returns true if the union find element contains the passed in attribute
	 * @param attr
	 * @return
	 */
	public boolean attributeInElement(String attr){
		return attributes.contains(attr);
	}
	
	/**
	 * Returns all the attributes in the union find element for the passed in 
	 * relation
	 * @param relation
	 * @return
	 */
	public List<String> findAllAttributesForRelation(String relation){
		
		List<String> matchingAttributes = new ArrayList<String>();
		for(String attribute: attributes){
			if(relation.equals(attribute.split(".")[0])){
				matchingAttributes.add(attribute);
			}
		}
		return matchingAttributes;
	}
	
	/**
	 * Adds all the attributes of the list to the current union find element
	 * @param attributes
	 */
	public void addAllAttributesFromList(List<String> attributes) {
		this.attributes.addAll(attributes);
	}
	
	public List<String> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<String> attributes) {
		this.attributes = attributes;
	}

	public Long getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(Long lowerBound) {
		this.lowerBound = lowerBound;
	}

	public Long getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(Long upperBound) {
		this.upperBound = upperBound;
	}

	public Long getEqualityConstraint() {
		return equalityConstraint;
	}

	public void setEqualityConstraint(Long equalityConstraint) {
		this.equalityConstraint = equalityConstraint;
		setLowerBound(equalityConstraint);
		setUpperBound(equalityConstraint);
	}
}
