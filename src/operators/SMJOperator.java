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
	private int innerPartitionStartIndex, currInnerIndex, currOuterIndex, count;
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
		this.count = 0;

		comp = new JoinTupleComparator(this.leftSortConditions, this.rightSortConditions);
		rightComp = new JoinTupleComparator(this.rightSortConditions, this.rightSortConditions);
	}

	@Override
	public Tuple getNextTuple() {
		
		leftChild.reset(currOuterIndex);
		rightChild.reset(currInnerIndex);
		
		Tr = leftChild.getNextTuple();
		Ts = rightChild.getNextTuple();
		
		//1,813,283,1,128
		if ( Tr !=null && Tr.getArributeList().contains("Sailors.A") && Tr.getValueForAttr("Sailors.A") == 1 && 
				Tr.getArributeList().contains("Sailors.B") && Tr.getValueForAttr("Sailors.B") == 813 &&
						Tr.getArributeList().contains("Sailors.C") && Tr.getValueForAttr("Sailors.C") == 283) {
			int test = 0;
			test = test;
		}
		
		//1,128
		if ( Ts !=null && Ts.getArributeList().contains("Reserves.G") && Ts.getValueForAttr("Reserves.G") == 1 && 
				Ts.getArributeList().contains("Reserves.H") && Ts.getValueForAttr("Reserves.H") == 128) {
			int test = 0;
			test = test;
		}
		
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
					
//					if ( returnTuple !=null && returnTuple.getArributeList().contains("Sailors.A") && returnTuple.getValueForAttr("Sailors.A") == 1 && 
//							returnTuple.getArributeList().contains("Sailors.B") && returnTuple.getValueForAttr("Sailors.B") == 263 &&
//									returnTuple.getArributeList().contains("Sailors.C") && returnTuple.getValueForAttr("Sailors.C") == 675 &&
//											returnTuple.getArributeList().contains("Reserves.G") && returnTuple.getValueForAttr("Reserves.G") == 1 &&
//													returnTuple.getArributeList().contains("Reserves.H") && returnTuple.getValueForAttr("Reserves.H") == 181) {
//						int test = 0;
//						test = test;
//					}

					if (rightComp.joinCompare(Ts, rightChild.getNextTuple()) == 0) {
						currInnerIndex++;
					} else {
					currOuterIndex++;
					currInnerIndex = innerPartitionStartIndex;
					}
				
					System.out.println(count++ + " " + returnTuple.toStringValues());
					return returnTuple;
				}	
			}			
		}
		
		return returnTuple;
	}
}