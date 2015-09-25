package operators;
import utils.Tuple;

public abstract class Operator {

	public abstract Tuple getNextTuple();
    
	public abstract void reset();
	
    public void dump() {
		Tuple currentTuple = getNextTuple();
		String tableDump = null;
		if (currentTuple != null)
			tableDump = new String(currentTuple.toStringAttributes()+"\n");
		while(currentTuple != null) {
			tableDump = tableDump + currentTuple.toStringValues() +  "\n";
			currentTuple = getNextTuple();
		}
		System.out.println(tableDump);
    }
}
