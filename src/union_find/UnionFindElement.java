package union_find;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.schema.Column;

public class UnionFindElement {
	
	private List<Column> attributes;
	private Long lowerBound, upperBound, equalityConstraint;
	
	public UnionFindElement(){
		attributes = new ArrayList<Column>();
		lowerBound = null;
		upperBound = null; 
		equalityConstraint = null;
	}
	
	public UnionFindElement(Column attr){
		this();
		attributes.add(attr);
	}
	
	/**
	 * Adds the passed in attribute to the union find element
	 * @param attr
	 */
	public void addAttributeToElement(Column attr){
		attributes.add(attr);
	}
	
	/**
	 * Returns true if the union find element contains the passed in attribute
	 * @param attr
	 * @return
	 */
	public boolean attributeInElement(Column attr){
		for(Column attribute: attributes){
			String attrTable = attr.getTable().toString() + "." + attr.getColumnName().toString();
			String currentAttributeTable = attribute.getTable().toString() + "." + attribute.getColumnName().toString();
			if(attrTable.equals(currentAttributeTable)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns all the attributes in the union find element for the passed in 
	 * relation
	 * @param relation
	 * @return
	 */
	public List<Column> findAllAttributesForRelation(String relation){
		
		List<Column> matchingAttributes = new ArrayList<Column>();
		
		for(Column attribute: attributes){
			if(attribute.getTable().toString().equals(relation)){
				matchingAttributes.add(attribute);
			}
		}
		return matchingAttributes;
	}
	
	/**
	 * Adds all the attributes of the list to the current union find element
	 * @param attributes
	 */
	public void addAllAttributesFromList(List<Column> attributes) {
		this.attributes.addAll(attributes);
	}
	
	public List<Column> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Column> attributes) {
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
	
	public String toString() {
		String ufe = "";
		
		// Attributes in union find
		ufe = ufe + "[[";
		for (Column attr: attributes) {
			ufe = ufe + attr.toString() + ",";
		}
		ufe = ufe.substring(0, ufe.length()-1);
		ufe = ufe + "]" + ",";
		
		// Bounds
		ufe = ufe + " equals " + equalityConstraint + ", ";
		ufe = ufe + "min " + lowerBound + ", ";
		ufe = ufe + "max " + upperBound + "]\n";
		
		return ufe;
	}
}
