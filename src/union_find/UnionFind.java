package union_find;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.schema.Column;

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
			if (el.findAllAttributesForRelation(tableName) != null && el.findAllAttributesForRelation(tableName).size() > 0) {
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
	public UnionFindElement find(Column attr){
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
	public UnionFindElement create(Column attr){
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
	
	/**
	 * Returns a conjugated expression(AND expression) for all attributes from a single table (with name tableName)
	 * @param ufe
	 * @param tableName
	 * @return
	 */
	private Expression getExpressionForUnionFindElement(UnionFindElement ufe, String tableName) {
		List<Column> attributes = ufe.findAllAttributesForRelation(tableName);
		Expression finalExp = null;

		// If all bounds are null, the create equalsTo expressions for all pairs of attributes from the same table
		if (ufe.getEqualityConstraint() == null && ufe.getLowerBound() == null && ufe.getUpperBound() == null) {
			if (attributes.size() > 1) {
				Column left = attributes.get(0);
				finalExp = new EqualsTo(left, attributes.get(1));
				if (attributes.size() == 2) {
					return finalExp;
				}
				
				for (int i = 2; i < attributes.size(); i++) {
					finalExp = new AndExpression(finalExp, new EqualsTo(left, attributes.get(i)));
				}
			}
			
			return finalExp;
		}
		
		// Comes here only if atleast 1 bound is non-null
		if (ufe.getEqualityConstraint() != null) {
			finalExp = new EqualsTo(attributes.get(0), new LongValue(ufe.getEqualityConstraint()));
			if (attributes.size() == 1) {
				return finalExp;
			} else {
				// If multiple attributes of same relation in the union find.
				// Use AND to conjugate all the equals to expressions
				for (int i = 1; i < attributes.size(); i++) {
					finalExp = new AndExpression(finalExp, new EqualsTo(attributes.get(i), new LongValue(ufe.getEqualityConstraint())));
				}
			}
		} else {
			// If union find has a lower bound
			if (ufe.getLowerBound() != null) {
				finalExp = new GreaterThanEquals(attributes.get(0), new LongValue(ufe.getLowerBound()));
				
				if (attributes.size() > 1) {
					for(int i = 1; i < attributes.size(); i++) {
						finalExp = new AndExpression(finalExp, new GreaterThanEquals(attributes.get(i), new LongValue(ufe.getLowerBound())));
					}
				}
			}
			
			// If union find has an upper bound
			if (ufe.getUpperBound() != null) {
				// Only comes here if union find had neither a lower bound or an equality constraint
				if (finalExp == null) {
					finalExp = new MinorThanEquals(attributes.get(0), new LongValue(ufe.getUpperBound()));
				} else {
					// If union find had a lower bound, add upper bound expression of 1st attribute in the list to the existing lower bound expressions 
					// using an AND expression.
					finalExp = new AndExpression(finalExp, new MinorThanEquals(attributes.get(0), new LongValue(ufe.getUpperBound())));
				}
				
				if (attributes.size() == 1) {
					return finalExp;
				}
				
				// Add remaining attributes to the expression
				for (int i = 1; i < attributes.size(); i++) {
					finalExp = new AndExpression(finalExp, new MinorThanEquals(attributes.get(i), new LongValue(ufe.getUpperBound())));
				}	
			}
		}	

		return finalExp;
	}
	
	/**
	 * Get conjugated expression (AND expression) for all union finds that have attributes from tableName passed as argument
	 * @param ufes union finds with attributes from tableName
	 * @param tableName
	 * @return
	 */
	public Expression getExpressionForUnionFindElements(List<UnionFindElement> ufes, String tableName) {
		
		if (ufes.size() == 0){
			return null;
		}
		
		Expression exp = getExpressionForUnionFindElement(ufes.get(0), tableName);
		
		if (exp == null || ufes.size() == 1) {
			return exp;
		}
		
		for (int i = 1; i < ufes.size(); i++) {
			Expression rightExpression = getExpressionForUnionFindElement(ufes.get(i), tableName);
			if (rightExpression != null) {
				exp = new AndExpression(exp, rightExpression);
			}
		}
		
		return exp;
	}
}
