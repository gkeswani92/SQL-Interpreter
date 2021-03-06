package operators;

import java.util.ArrayList;

import expression_visitors.ExpressionEvaluator;
import net.sf.jsqlparser.expression.Expression;
import utils.Tuple;

public class BNLJOperator extends JoinOperator {

	private ArrayList<Tuple> outerBuffer, innerBuffer;
	private Integer numBufferPages;
	private Integer outerBufferMaxTupleCount,innerBufferMaxTupleCount;	
	private Integer outerBlockStatus, innerBlockStatus;
	private Integer outerIndex, innerIndex;
	
	public BNLJOperator(Expression joinCondition, Operator leftChild, 
			Operator rightChild, Integer numBufferPages) {
		
		super(joinCondition, leftChild, rightChild);
		this.numBufferPages = numBufferPages;
		outerBuffer = new ArrayList<Tuple>();
		innerBuffer = new ArrayList<Tuple>();
		outerIndex  = 0;
		innerIndex  = 0;
		refillOuterBuffer();
		refillInnerBuffer();
	}

	public void refillOuterBuffer(){
		
		Tuple t = null;
		outerBuffer.clear();
		outerIndex = 0;
			
		//We get the first tuple inside this to check the size of each tuple
		//This is to decide how many tuples we are going to fill in the outer buffer
		t = leftChild.getNextTuple(); 
		if (t != null) {
			outerBufferMaxTupleCount = (this.numBufferPages * 4096) / (4 *  t.getArributeList().size());
			
			//Adding the first tuple to the outer buffer and reducing the count of tuples needed
			outerBuffer.add(t);
			outerBufferMaxTupleCount--;
		} else {
			outerBlockStatus = -1;
			return;
		}
		
		while(outerBufferMaxTupleCount != 0){
			t = leftChild.getNextTuple();
			
			//If there are no more tuples left in the outer relation, we return -1 
			//to signify that there are no more blocks left after the current one
			if(t == null) {
				outerBlockStatus = -1;
				return;
			}
			
			outerBuffer.add(t);
			outerBufferMaxTupleCount--;
		}
		
		//This signifies that we were able to completely fill the outer block
		//and may still have more tuples left for the outer block for refilling later
		outerBlockStatus = 0;
	}
	
	public void refillInnerBuffer(){
		
		Tuple t = null;
		innerBuffer.clear();
		innerIndex = 0;
		
		t = rightChild.getNextTuple();
		if (t != null) {
			innerBufferMaxTupleCount = 4096 / (4 * t.getArributeList().size());
			
			innerBuffer.add(t);
			innerBufferMaxTupleCount--;
		} else {
			innerBlockStatus = -1;
			return;
		}
		
		while(innerBufferMaxTupleCount != 0){
			t = rightChild.getNextTuple();
			
			//If there are no more tuples left in the inner relation, we return -1 
			//to signify that we are done scanning the complete inner relation
			if(t == null) {
				innerBlockStatus = -1;
				return;
			}
			innerBuffer.add(t);
			innerBufferMaxTupleCount--;
		}
		
		//This signifies that we were able to completely fill the inner block
		//and may still have more tuples left for the outer block for refilling later
		innerBlockStatus = 0;
	}
	
	@Override
	public Tuple getNextTuple() {
		
		Tuple left  = null;
		Tuple right = null;
		Tuple joinTuple  = null;
		
		while(true){
			
			//If we have scanned the complete block for this tuple, we reset the
		    //index of the outer block back to the beginning and move to the next
		    //tuple of the inner page
		    if(outerIndex >= outerBuffer.size()){
		    	outerIndex = 0;
		    	innerIndex++;
		    } 
			
		    //If the inner page has been completely read, replace it with the 
		    //next inner page
		    if(innerIndex >= innerBuffer.size()){
		    	if(innerBlockStatus == 0){
		    		refillInnerBuffer();
		    		outerIndex = 0;
		    	} 
		    	//If all inner pages are over, refill the outer block and start again
		    	else {
		    		if(outerBlockStatus == 0){
						refillOuterBuffer();
						rightChild.reset();
						refillInnerBuffer();
					} else {
						return null;
					}
		    	}
		    }
		    
		    try{
		    	left = outerBuffer.get(outerIndex);
		    	right = innerBuffer.get(innerIndex);
		    	joinTuple = new Tuple(left, right);
		    } catch(Exception e) {
		    	return null;
		    }
		    
			//Base case where we run out of tuples 
			if(left == null && right == null)
				break;
			
			//If it is a cartesian product, just return the tuple
			if(joinCondition == null){
				outerIndex++;
				return joinTuple;
			}
			
			//If the joined tuple, satisfies the join condition, return it
			ExpressionEvaluator ob = new ExpressionEvaluator(joinTuple);
		    joinCondition.accept(ob);
		    if (joinTuple != null && joinTuple.getIsSatisfies()) {
		    	outerIndex++;
		        return joinTuple;
		    }  
		    outerIndex++;
		}	
		return null;
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
			plan = plan + "BNLJ[" + joinCondition.toString()+ "]\n";
		} else {
			plan = plan + "BNLJ\n";
		}
		
		level += 1;
		plan = plan + leftChild.getPhysicalPlanToString(level);
		plan = plan + rightChild.getPhysicalPlanToString(level);
		return plan;
	}
}
