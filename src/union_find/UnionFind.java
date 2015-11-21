package union_find;

import java.util.ArrayList;
import java.util.List;

public class UnionFind {
	
	private List<UnionFindElement> elements;
	
	public UnionFind() {
		elements = new ArrayList<UnionFindElement>();
	}
	
	/**
	 * Returns the list of union find elements that contain an attribute of
	 * the passed in relation
	 * @param tableName
	 * @return
	 */
	public List<UnionFindElement> findElementsForRelation(String tableName) {
		List<UnionFindElement> returnList = new ArrayList<UnionFindElement>();
		for(UnionFindElement el: elements) {
			if (el.findAllAttributesForRelation(tableName) != null) {
				returnList.add(el);
			}
		}
		return returnList;
	}
	
	/**
	 * Finds the union find element that the passed in attribute belongs to
	 * @param attr
	 * @return
	 */
	public UnionFindElement find(String attr){
		for(UnionFindElement element: elements){
			if(element.attributeInElement(attr)){
				return element;
			}
		}
		return null;
	}
	
	/**
	 * Creates a new union find element
	 * @param attr
	 */
	public UnionFindElement create(String attr){
		UnionFindElement newElement = new UnionFindElement(attr);
		elements.add(newElement);
		return newElement;
	}
	
	/**
	 * Merges the two union find elements that have been passed in to create 
	 * a new union find element
	 * @param element1
	 * @param element2
	 */
	public void merge(UnionFindElement element1, UnionFindElement element2){
		
		UnionFindElement mergedElement = new UnionFindElement();
		
		mergedElement.addAllAttributesFromList(element1.getAttributes());
		mergedElement.addAllAttributesFromList(element2.getAttributes());
		
		setBoundsForElement(element1, element2, mergedElement);
		
		elements.remove(element1);
		elements.remove(element2);
		elements.add(mergedElement);
	}
	
	/**
	 * Sets the lower, upper and equality constraint for the merged element
	 * @param element1
	 * @param element2
	 * @param mergedElement
	 */
	private void setBoundsForElement(UnionFindElement element1, UnionFindElement element2,
			UnionFindElement mergedElement) {
		
		//Setting the lower bound of the merged element
		if(element1.getLowerBound() == null) {
			if(element2.getLowerBound() != null) {
				mergedElement.setLowerBound(element2.getLowerBound());
			}
		} else {
			if(element2.getLowerBound() == null) {
				mergedElement.setLowerBound(element1.getLowerBound());
			} else {
				mergedElement.setLowerBound(element1.getLowerBound() <= element2.getLowerBound() 
												? element1.getLowerBound() : element2.getLowerBound());
			}
		}
		
		//Setting the upper bound of the merged element
		if(element1.getUpperBound() == null) {
			if(element2.getUpperBound() != null) {
				mergedElement.setUpperBound(element2.getUpperBound());
			}
		} else {
			if(element2.getUpperBound() == null) {
				mergedElement.setUpperBound(element1.getUpperBound());
			} else {
				mergedElement.setUpperBound(element1.getUpperBound() >= element2.getUpperBound() 
												? element1.getUpperBound() : element2.getUpperBound());
			}
		}
		
		//Setting the equality constraint of the merged element
		if(element1.getEqualityConstraint() == null){
			mergedElement.setEqualityConstraint(element2.getEqualityConstraint());
		} else {
			mergedElement.setEqualityConstraint(element1.getEqualityConstraint());
		}
	}
}
