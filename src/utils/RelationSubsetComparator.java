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
		int comp = o1.getPlanCost().compareTo(o2.getPlanCost());
		if (comp != 0) {
			return comp;
		} else {
			Double parentSize1 = o1.getParentSize();
			Double parentSize2 = o2.getParentSize();
			return parentSize1.compareTo(parentSize2);
		}
	}
}
