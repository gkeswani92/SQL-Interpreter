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
}
