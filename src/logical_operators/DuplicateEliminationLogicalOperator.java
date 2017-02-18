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

	@Override
	public String getLogicalPlanToString(Integer level) {
		String plan = "";
		
		if (level == 0) {
			plan = "DupElim" + "\n";
		}
		
		plan = plan + child.getLogicalPlanToString(level+=1) + "\n";
		return plan;
	}
}
