package logical_operators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RelationSubset implements Iterable<String> {
	
	private List<String> relations;
	private Double planCost;
	
	public RelationSubset(String tableName){
		relations = new ArrayList<String>();
		relations.add(tableName);
		planCost = 0.0;
	}
	
	public RelationSubset(List<String> tableNames){
		relations = new ArrayList<String>();
		relations.addAll(tableNames);
		planCost = 0.0;
	}

	/**
	 * Adds the relation to the current relation subset
	 * @param tableName
	 */
	public void addToRelationSubset(String tableName) {
		relations.add(tableName);
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
	
	public String toString() {
		return "Subset: " + relations + " Cost: " + planCost;
	}

	@Override
	public Iterator<String> iterator() {
		return relations.iterator();
	}
	
	public List<String> findAddableRelations(List<String> initialRelations) {
		List<String> addableRelations = new ArrayList<String>();
		for(String relation: initialRelations) {
			if(!relations.contains(relation)){
				addableRelations.add(relation);
			}
		}
		return addableRelations;
	}
}
