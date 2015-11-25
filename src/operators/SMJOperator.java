package operators;

import java.util.List;

import net.sf.jsqlparser.expression.Expression;
import utils.JoinTupleComparator;
import utils.Tuple;

/**
 * R is the left(outer) relation and S is the right(inner) relation
 * @author tanvimehta
 *
 */
public class SMJOperator extends JoinOperator {

	private List<String> leftSortConditions, rightSortConditions;
	private int innerPartitionStartIndex, currInnerIndex, currOuterIndex;
	Tuple Tr, Ts, Gs, returnTuple;
	JoinTupleComparator comp, rightComp;

	public SMJOperator(Expression joinCondition, Operator leftChild, Operator rightChild, 
			List<String> leftSortConditions, List<String> rightSortConditions) {
		super(joinCondition, leftChild, rightChild);
		this.leftSortConditions = leftSortConditions;
		this.rightSortConditions = rightSortConditions;	
		this.innerPartitionStartIndex = 0;
		this.currInnerIndex = 0;
		this.currOuterIndex = 0;

		comp = new JoinTupleComparator(this.leftSortConditions, this.rightSortConditions);
		rightComp = new JoinTupleComparator(this.rightSortConditions, this.rightSortConditions);
	}

	@Override
	public Tuple getNextTuple() {
		
		leftChild.reset(currOuterIndex);
		rightChild.reset(currInnerIndex);
		
		Tr = leftChild.getNextTuple();
		Ts = rightChild.getNextTuple();
		
		Gs = Ts;
		returnTuple = null;
		
		while (Tr != null && Gs != null) {
			while (comp.joinCompare(Tr, Gs) < 0) {
				if (Tr == null || Gs == null) {
					return returnTuple;
				}
				currOuterIndex++;
				Tr = leftChild.getNextTuple();
			}
						
			while (comp.joinCompare(Tr, Gs) > 0) {
				if (Tr == null || Gs == null) {
					return returnTuple;
				}
				innerPartitionStartIndex++;
				currInnerIndex++;
				Gs = rightChild.getNextTuple();
			}
						
			Ts = Gs;
			
			while (comp.joinCompare(Tr, Gs) == 0) {
 				
				while (comp.joinCompare(Tr, Ts) == 0) {
					returnTuple = new Tuple(Tr, Ts);

					if (rightComp.joinCompare(Ts, rightChild.getNextTuple()) == 0) {
						currInnerIndex++;
					} else {
					currOuterIndex++;
					currInnerIndex = innerPartitionStartIndex;
					}
					
					return returnTuple;
				}	
			}			
		}
		
		return returnTuple;
	}
	
	@Override
	public String getPhysicalPlanToString(Integer level) {
		String plan = "";
		
		// Level
		if (level > 0) {
			for (int i = 0; i < level; i++) {
				plan = plan + "-";
			}
		}
		
		// Join with join expressions
		if (joinCondition != null) {
			plan = plan + "SMJ[" + joinCondition.toString()+ "]\n";
		} else {
			plan = plan + "SMJ\n";
		}
		
		level += 1;
		plan = plan + leftChild.getPhysicalPlanToString(level);
		plan = plan + rightChild.getPhysicalPlanToString(level);
		return plan;
	}
}