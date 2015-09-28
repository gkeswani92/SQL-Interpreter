package parser;

import net.sf.jsqlparser.statement.select.PlainSelect;
import operators.JoinOperator;
import operators.ProjectOperator;
import operators.ScanOperator;
import operators.SelectOperator;

public class EvaluateOperatorVisitor implements OperatorVisitor {
	private PlainSelect body;
	
	@Override
	public void visit(ScanOperator node) {
		node.getNextTuple();
	}

	@Override
	public void visit(SelectOperator node) {
		node.getChild().accept(this);
	}

	@Override
	public void visit(ProjectOperator node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(JoinOperator node) {
		// TODO Auto-generated method stub

	}

}
