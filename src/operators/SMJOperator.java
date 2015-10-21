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
	private int partitionIndex;

	public SMJOperator(Expression joinCondition, Operator leftChild, Operator rightChild, 
			List<String> leftSortConditions, List<String> rightSortConditions) {
		super(joinCondition, leftChild, rightChild);
		this.leftSortConditions = leftSortConditions;
		this.rightSortConditions = rightSortConditions;	
		this.partitionIndex = 0;
	}

	@Override
	public Tuple getNextTuple() {
		
		Tuple Tr = leftChild.getNextTuple();
		Tuple Ts = rightChild.getNextTuple();
		Tuple returnTuple = null;
		
		this.rightChild.reset(partitionIndex);
		Tuple Gs = rightChild.getNextTuple();
		
		TupleComparator comp = new TupleComparator(this.leftSortConditions, this.rightSortConditions);
		
		while (Tr != null && Gs != null) {
			while (comp.joinCompare(Tr, Gs) < 0) {
				Tr = leftChild.getNextTuple();
			}
			
			while (comp.joinCompare(Tr, Gs) > 0) {
				partitionIndex++;
				Gs = rightChild.getNextTuple();
			}
			
			Ts = Gs;
			
			while (comp.joinCompare(Tr, Gs) == 0 && returnTuple != null) {
				this.rightChild.reset(partitionIndex);
				Ts = rightChild.getNextTuple();
				
				while (comp.joinCompare(Tr, Ts) == 0) {
					returnTuple = new Tuple(Tr, Ts);
					return returnTuple;
//					Ts = rightChild.getNextTuple();
//					break;
				}
				
//				Tr = leftChild.getNextTuple();
			}
			
//			Gs = Ts;
		}
		return returnTuple;
	}
}
