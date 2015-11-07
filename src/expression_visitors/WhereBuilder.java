package expression_visitors;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import logical_operators.LogicalOperator;
import logical_operators.ScanLogicalOperator;
import logical_operators.SelectLogicalOperator;
import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.InverseExpression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 * Visitor class to traverse through where clause and keep track of select operations on each table
 * and join conditions on tables.
 * @author Gaurav, Tanvi and Sahana (gk368,tmm259 and sv387)
 */
public class WhereBuilder implements ExpressionVisitor {

	private Map<String, List<LogicalOperator>> tableOperators;
	private List<Entry<List<String>,Expression>> joins;
	
	public WhereBuilder(Map<String, List<LogicalOperator>> tableOperators, List<Entry<List<String>,Expression>> joins) {
		this.tableOperators = tableOperators;
		this.joins = joins;
	}
	
	/**
	 * Checks if expression on table is basic(select) or a join condition
	 * @param exp expression on the table
	 * @return <true or false, table name> true - if exp is select fale - if exp is join 
	 */
	private Entry<Boolean, String> isBasicExpression(BinaryExpression exp) {
		if (exp.getLeftExpression() instanceof LongValue && exp.getRightExpression() instanceof Column) {
			return new AbstractMap.SimpleEntry<Boolean, String>(true, ((Column)exp.getRightExpression()).getTable().toString());
		} 
		else if (exp.getRightExpression() instanceof LongValue && exp.getLeftExpression() instanceof Column) {
			return new AbstractMap.SimpleEntry<Boolean, String>(true, ((Column)exp.getLeftExpression()).getTable().toString());
		} 
		else if (exp.getRightExpression() instanceof Column && exp.getLeftExpression() instanceof Column) {
			if (((Column)exp.getRightExpression()).getTable() == ((Column)exp.getLeftExpression()).getTable()) {
				return new AbstractMap.SimpleEntry<Boolean, String>(true, ((Column)exp.getLeftExpression()).getTable().toString());
			}
		}
		return new AbstractMap.SimpleEntry<Boolean, String>(false, null);
	}
	
	/**
	 * Checks if the expression is a select, if so then creates a single select(with AND conjuct) for the tables
	 * Keeps track of all select clauses on the each table and maintain a list of all join conditions in where clause
	 * @param arg0 expression to be evaluated
	 */
	public void buildHelper(Expression arg0) {
		Entry<Boolean, String> entry = isBasicExpression((BinaryExpression) arg0);
		
		if (entry.getKey()) {
			List<LogicalOperator> operators = tableOperators.get(entry.getValue());
			
			// If this is the first operator we are adding for the table
			if (operators == null || operators.isEmpty()) {
				ScanLogicalOperator scanOp = new ScanLogicalOperator(entry.getValue());
				SelectLogicalOperator selectOp = new SelectLogicalOperator(arg0, scanOp);
				List<LogicalOperator> opList = new ArrayList<LogicalOperator>();
				opList.add(selectOp);
				tableOperators.put(entry.getValue(), opList);
			}
			
			// If we need to conjunct multiple select operations
			else {
				SelectLogicalOperator currentSelectOp = (SelectLogicalOperator) operators.get(0);
				AndExpression expression = new AndExpression();
				expression.setLeftExpression(currentSelectOp.getExpression());
				expression.setRightExpression(arg0);
				
				SelectLogicalOperator newSelect = new SelectLogicalOperator(expression, new ScanLogicalOperator(entry.getValue()));
				List<LogicalOperator> opList = new ArrayList<LogicalOperator>();
				opList.add(newSelect);
				tableOperators.put(entry.getValue(), opList);
			}
		} else {
			// If the expression is a join condition, add to joins list
			List<String> joinTables = new ArrayList<String>();
			joinTables.add(((Column)((BinaryExpression)arg0).getLeftExpression()).getTable().toString());
			joinTables.add(((Column)((BinaryExpression)arg0).getRightExpression()).getTable().toString());
			Entry<List<String>,Expression> joinEntry= new AbstractMap.SimpleEntry<List<String>, Expression>(joinTables, arg0);
			joins.add(joinEntry);
		}
	}
	
	@Override
	public void visit(EqualsTo arg0) {
		buildHelper(arg0);
	}

	@Override
	public void visit(GreaterThan arg0) {
		buildHelper(arg0);
	}

	@Override
	public void visit(GreaterThanEquals arg0) {
		buildHelper(arg0);
	}
	
	@Override
	public void visit(MinorThan arg0) {
		buildHelper(arg0);
	}

	@Override
	public void visit(MinorThanEquals arg0) {	
		buildHelper(arg0);
	}

	@Override
	public void visit(NotEqualsTo arg0) {
		buildHelper(arg0);
	}
	
	@Override
	public void visit(LongValue arg0) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void visit(Column arg0) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void visit(AndExpression arg0) {
		arg0.getLeftExpression().accept(this);
		arg0.getRightExpression().accept(this);
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	@Override
	public void visit(NullValue arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Function arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(InverseExpression arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(JdbcParameter arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(DoubleValue arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(DateValue arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(TimeValue arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(TimestampValue arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Parenthesis arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(StringValue arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Addition arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Division arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Multiplication arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Subtraction arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OrExpression arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Between arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void visit(InExpression arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(IsNullExpression arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(LikeExpression arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void visit(SubSelect arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(CaseExpression arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(WhenClause arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ExistsExpression arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AllComparisonExpression arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AnyComparisonExpression arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Concat arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Matches arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(BitwiseAnd arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(BitwiseOr arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(BitwiseXor arg0) {
		// TODO Auto-generated method stub
		
	}
}
