package operators;

import java.util.List;

import net.sf.jsqlparser.expression.Expression;
import utils.Tuple;
import utils.TupleComparator;

/**
 * R is the left(outer) relation and S is the right(inner) relation
 * @author tanvimehta
 *
 */
public class SMJOperator extends JoinOperator {

	private List<String> leftSortConditions, rightSortConditions;
	private int innerPartitionStartIndex, currInnerIndex, currOuterIndex, count;
	Tuple Tr, Ts, Gs, returnTuple;
	TupleComparator comp, rightComp;

	public SMJOperator(Expression joinCondition, Operator leftChild, Operator rightChild, 
			List<String> leftSortConditions, List<String> rightSortConditions) {
		super(joinCondition, leftChild, rightChild);
		this.leftSortConditions = leftSortConditions;
		this.rightSortConditions = rightSortConditions;	
		this.innerPartitionStartIndex = 0;
		this.currInnerIndex = 0;
		this.currOuterIndex = 0;
		this.count = 0;

		comp = new TupleComparator(this.leftSortConditions, this.rightSortConditions);
		rightComp = new TupleComparator(this.rightSortConditions, this.rightSortConditions);
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
					
					if ( count == 3331) {
						int test = 0;
						test = test;
					}
					System.out.println(count++ + " " + returnTuple.toStringValues());
					return returnTuple;
				}	
			}			
		}
		
		return returnTuple;
	}
}