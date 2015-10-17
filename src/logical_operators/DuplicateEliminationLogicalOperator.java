package logical_operators;

import operators.DuplicateEliminationOperator;
import operators.Operator;

public class DuplicateEliminationLogicalOperator extends LogicalOperator {
	
	private LogicalOperator child;
	
	public DuplicateEliminationLogicalOperator(LogicalOperator child) {
		this.child = child;
	}

	@Override
	public Operator getNextPhysicalOperator() {
		return new DuplicateEliminationOperator(this.child.getNextPhysicalOperator());
	}
}
