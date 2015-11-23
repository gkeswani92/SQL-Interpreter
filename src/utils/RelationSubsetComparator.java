package utils;

import java.util.Comparator;

import logical_operators.RelationSubset;

/**
 * Compares relation subsets by plan costs
 * @author tanvimehta
 *
 */
public class RelationSubsetComparator implements Comparator<RelationSubset> {

	@Override
	public int compare(RelationSubset o1, RelationSubset o2) {
		return o1.getPlanCost().compareTo(o2.getPlanCost());
	}
}
