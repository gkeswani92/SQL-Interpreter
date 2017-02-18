package logical_operators;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import operators.Operator;
import operators.ProjectOperator;

public class ProjectLogicalOperator extends LogicalOperator {

	private LogicalOperator child;
	private List<String> requiredColumns; 
	
	public List<String> getRequiredColumns() {
		return requiredColumns;
	}

	public ProjectLogicalOperator(PlainSelect body, LogicalOperator child) {
		this.child = child;
		this.requiredColumns = new ArrayList<String>();
		@SuppressWarnings("unchecked")
		List<SelectExpressionItem> selectColumns = body.getSelectItems();
		
		//Converting each select item to string
		for(Object c: selectColumns) 
			requiredColumns.add(c.toString());
	}

	@Override
	public Operator getNextPhysicalOperator() {
		return new ProjectOperator(child.getNextPhysicalOperator(), requiredColumns);
	}

	@Override
	public String getLogicalPlanToString(Integer level) {
		String plan = "";
		
		// Level
		if (level > 0) {
			for (int i = 0; i < level; i++) {
				plan = plan + "-";
			}
		}
		
		plan = plan + "Project" + "[";
		for (String col: requiredColumns) {
			plan = plan + col + ",";
		}
		plan = plan.substring(0, plan.length()-1);
		plan = plan + "]\n";
		
		plan = plan + child.getLogicalPlanToString(level+=1);
		
		return plan;
	}
}
