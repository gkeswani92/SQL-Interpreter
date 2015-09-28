package parser;

import operators.JoinOperator;
import operators.ProjectOperator;
import operators.ScanOperator;
import operators.SelectOperator;

public interface OperatorVisitor {
	
	void visit(ScanOperator node);
	
	void visit(SelectOperator node);
	
	void visit(ProjectOperator node);
	
	void visit(JoinOperator node);	
}
