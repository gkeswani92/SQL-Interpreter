package logical_operators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.jsqlparser.schema.Column;
import statistics.AttributeSelectionStatistics;

public class RelationSubset implements Iterable<String> {
	
	private List<String> relations;
	private Double planCost, size, parentSize;
	private List<List<Column>> joinConditions;
	
	public RelationSubset(String tableName){
		relations = new ArrayList<String>();
		relations.add(tableName);
		planCost = 0.0;
		size = 0.0;
		parentSize = 0.0;
		joinConditions = new ArrayList<List<Column>>();
	}
	
	public RelationSubset(List<String> tableNames, String newTableName, Double parentSize){
		relations = new ArrayList<String>();
		relations.addAll(tableNames);
		relations.add(newTableName);
		planCost = 0.0;
		size = 0.0;
		this.parentSize = parentSize;
		joinConditions = new ArrayList<List<Column>>();
	}

	/**
	 * Adds the relation to the current relation subset
	 * @param tableName
	 */
	public void addToRelationSubset(String tableName) {
		relations.add(tableName);
	}
	
	/**
	 * Returns the list of relations that are passed in but are not presnt in this
	 * relation 
	 * @param initialRelations
	 * @return
	 */
	public List<String> findAddableRelations(List<String> initialRelations) {
		List<String> addableRelations = new ArrayList<String>();
		for(String relation: initialRelations) {
			if(!relations.contains(relation)){
				addableRelations.add(relation);
			}
		}
		return addableRelations;
	}
	
	/**
	 * Returns true if the passed in relations matches the current subsets in 
	 * this relations
	 * @param parents
	 * @return
	 */
	public boolean exactlyMatches(List<String> parents) {
		if(relations.size() == parents.size()){
			for(int i=0; i<relations.size(); i++){
				if(!relations.get(i).equals(parents.get(i))){
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public List<List<Column>> getJoinConditions() {
		return joinConditions;
	}

	public void setJoinConditions(List<List<Column>> joinConditions) {
		this.joinConditions = joinConditions;
	}
	
	public void addJoinConditions(List<List<Column>> joinConditions) {
		this.joinConditions.addAll(joinConditions);
	}
	
	public List<String> getRelations() {
		return relations;
	}

	public void setRelations(List<String> relations) {
		this.relations = relations;
	}

	public Double getPlanCost() {
		return planCost;
	}

	public void setPlanCost(Double planCost) {
		this.planCost = planCost;
	}
	
	public Double getSize() {
		return size;
	}

	public void setSize(Double size) {
		this.size = size;
	}
	
	public String toString() {
		return "Subset: " + relations + " Cost: " + planCost + " Size: " + size + " Parent Size: "+parentSize;
	}

	@Override
	public Iterator<String> iterator() {
		return relations.iterator();
	}
	
	public Double getParentSize() {
		return parentSize;
	}

	public void setParentSize(Double parentSize) {
		this.parentSize = parentSize;
	}
	
	public boolean exactSubset(RelationSubset other){
		
		if(relations.size() != other.getRelations().size())	{
			return false;
		} 
		for(String relation: other.getRelations()){
			if(!relations.contains(relation)){
				return false;
			}
		}
		return true;
	}
	
}
