package operators;
import utils.Tuple;

public abstract class Operator {

	public abstract Tuple getNextTuple();
	public abstract void dump();
	public abstract void reset();
}
