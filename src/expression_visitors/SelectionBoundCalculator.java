package expression_visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import indexing.Index;
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
import statistics.AttributeSelectionStatistics;

/**
 * Visitor class to traverse through where clause and keep track of select operations(without index) on each attribute of table
 * and index scan operators(with index) on attribute of table.
 */
public class SelectionBoundCalculator implements ExpressionVisitor {
	
	private Map<String, AttributeSelectionStatistics> ass;
	
	public SelectionBoundCalculator() {
		this.ass = new HashMap<String, AttributeSelectionStatistics>();
	}
	
	public Map<String, AttributeSelectionStatistics> getAss() {
		return ass;
	}
	
	@Override
	public void visit(EqualsTo arg0) {
		
		// If left exp is a column
		if (arg0.getLeftExpression() instanceof Column) {
			String leftColName = ((Column)arg0.getLeftExpression()).getColumnName();
			// If right exp is a column, add columns to map if they don't exist, else remains unchanged
			if (arg0.getRightExpression() instanceof Column) {
				if (ass.isEmpty() || !ass.containsKey(leftColName)) {
					ass.put(leftColName, new AttributeSelectionStatistics());
				}
				
				String rightColName = ((Column)arg0.getRightExpression()).getColumnName();
				if (ass.isEmpty() || !ass.containsKey(rightColName)) {
					ass.put(rightColName, new AttributeSelectionStatistics());
				}
			} else {
				Long bound = ((LongValue)arg0.getRightExpression()).getValue();
				// No entry exists for the attribute
				if (ass.isEmpty() || !ass.containsKey(leftColName)) {
					ass.put(leftColName, new AttributeSelectionStatistics(bound, bound));
				} else {
					ass.get(leftColName).setLowerBound(bound);
					ass.get(leftColName).setUpperBound(bound);
				}
			}
		} 
		// If left exp is a long value, right exp is column
		else {
			String rightColName = ((Column)arg0.getLeftExpression()).getColumnName();
			Long bound = ((LongValue)arg0.getLeftExpression()).getValue();
			// No entry exists for the attribute
			if (ass.isEmpty() || !ass.containsKey(rightColName)) {
				ass.put(rightColName, new AttributeSelectionStatistics(bound, bound));
			} else {
				ass.get(rightColName).setLowerBound(bound);
				ass.get(rightColName).setUpperBound(bound);
			}
		}
	}

	@Override
	public void visit(GreaterThanEquals arg0) {
		// If left exp is a column
		if (arg0.getLeftExpression() instanceof Column) {
			String leftColName = ((Column)arg0.getLeftExpression()).getColumnName();
			// If right exp is a column, add columns to map if they don't exist, else remains unchanged
			if (arg0.getRightExpression() instanceof Column) {
				if (ass.isEmpty() || !ass.containsKey(leftColName)) {
					ass.put(leftColName, new AttributeSelectionStatistics());
				}
				
				String rightColName = ((Column)arg0.getRightExpression()).getColumnName();
				if (ass.isEmpty() || !ass.containsKey(rightColName)) {
					ass.put(rightColName, new AttributeSelectionStatistics());
				}
			} else {
				Long lowerBound = ((LongValue)arg0.getRightExpression()).getValue();
				// No entry exists for the attribute
				if (ass.isEmpty() || !ass.containsKey(leftColName)) {
					ass.put(leftColName, new AttributeSelectionStatistics(lowerBound, null));
				} else {
					Long currLowerBound = ass.get(leftColName).getLowerBound();
					if (currLowerBound != null && lowerBound.compareTo(currLowerBound) > 0) {
						ass.get(leftColName).setUpperBound(lowerBound);
					}
				}
			}
		} 
		// If left exp is a long value, right exp is column
		else {
			String rightColName = ((Column)arg0.getLeftExpression()).getColumnName();
			Long upperBound = ((LongValue)arg0.getLeftExpression()).getValue();
			// No entry exists for the attribute
			if (ass.isEmpty() || !ass.containsKey(rightColName)) {
				ass.put(rightColName, new AttributeSelectionStatistics(null, upperBound));
			} else {
				Long currUpperBound = ass.get(rightColName).getUpperBound();
				if (currUpperBound != null && upperBound.compareTo(currUpperBound) < 0) {
					ass.get(rightColName).setUpperBound(upperBound);
				}
			}
		}
	}

	@Override
	public void visit(MinorThanEquals arg0) {	
		// If left exp is a column
		if (arg0.getLeftExpression() instanceof Column) {
			String leftColName = ((Column)arg0.getLeftExpression()).getColumnName();
			// If right exp is a column, add columns to map if they don't exist, else remains unchanged
			if (arg0.getRightExpression() instanceof Column) {
				if (ass.isEmpty() || !ass.containsKey(leftColName)) {
					ass.put(leftColName, new AttributeSelectionStatistics());
				}
				
				String rightColName = ((Column)arg0.getRightExpression()).getColumnName();
				if (ass.isEmpty() || !ass.containsKey(rightColName)) {
					ass.put(rightColName, new AttributeSelectionStatistics());
				}
			} else {
				Long upperBound = ((LongValue)arg0.getRightExpression()).getValue();
				// No entry exists for the attribute
				if (ass.isEmpty() || !ass.containsKey(leftColName)) {
					ass.put(leftColName, new AttributeSelectionStatistics(null, upperBound));
				} else {
					Long currUpperBound = ass.get(leftColName).getUpperBound();
					if (currUpperBound != null && upperBound.compareTo(currUpperBound) < 0) {
						ass.get(leftColName).setUpperBound(upperBound);
					}
				}
			}
		} 
		// If left exp is a long value, right exp is column
		else {
			String rightColName = ((Column)arg0.getLeftExpression()).getColumnName();
			Long lowerBound = ((LongValue)arg0.getLeftExpression()).getValue();
			// No entry exists for the attribute
			if (ass.isEmpty() || !ass.containsKey(rightColName)) {
				ass.put(rightColName, new AttributeSelectionStatistics(lowerBound, null));
			} else {
				Long currLowerBound = ass.get(rightColName).getLowerBound();
				if (currLowerBound != null && lowerBound.compareTo(currLowerBound) > 0) {
					ass.get(rightColName).setUpperBound(lowerBound);
				}
			}
		}
	}

	@Override
	public void visit(NotEqualsTo arg0) {
		if (arg0.getLeftExpression() instanceof Column) {
			String leftColName = ((Column)arg0.getLeftExpression()).getColumnName();
			if (!ass.containsKey(leftColName)) {
				ass.put(leftColName, new AttributeSelectionStatistics());
			}
		}
		
		if (arg0.getRightExpression() instanceof Column) {
			String rightColName = ((Column)arg0.getRightExpression()).getColumnName();
			if (!ass.containsKey(rightColName)) {
				ass.put(rightColName, new AttributeSelectionStatistics());
			}
		}
	}
	
	@Override
	public void visit(GreaterThan arg0) {
	}
	
	@Override
	public void visit(MinorThan arg0) {
	}
	
	@Override
	public void visit(LongValue arg0) {		
	}
	
	@Override
	public void visit(Column arg0) {		
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

