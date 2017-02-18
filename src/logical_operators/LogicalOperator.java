package logical_operators;

import operators.Operator;

public abstract class LogicalOperator {
	
	public abstract Operator getNextPhysicalOperator();
	
	public abstract String getLogicalPlanToString(Integer level);
}