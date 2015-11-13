package expression_visitors;

import java.util.List;

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

/**
 * Visitor class to traverse through where clause and keep track of select operations(without index) on each attribute of table
 * and index scan operators(with index) on attribute of table.
 */
public class IndexExpressionBuilder implements ExpressionVisitor {
	
	Integer lowKey, highKey;
	Index index;
	List<Expression> selectConditions;
	
	public IndexExpressionBuilder(Index index, List<Expression> selectConditions) {
		this.lowKey = null;
		this.highKey = null;
		this.index = index;
		this.selectConditions = selectConditions;
	}
	
	public Integer getLowKey() {
		return lowKey;
	}
	
	public Integer getHighKey() {
		return highKey;
	}
	
	/**
	 * Checks if column(attribute) has an index
	 * @param exp
	 * @return true if has index, false otherwise
	 */
	public boolean hasIndex(BinaryExpression exp) {
		if ((exp.getLeftExpression() instanceof Column && 
				index.getAttribute().equals(((Column)exp.getLeftExpression()).getColumnName().toString())) ||
				(exp.getRightExpression() instanceof Column && 
						index.getAttribute().equals(((Column)exp.getRightExpression()).getColumnName().toString()))) {
			return true;
		}
		return false;
	}
	
	@Override
	public void visit(EqualsTo arg0) {
		if (hasIndex(arg0)) {
			if (arg0.getRightExpression() instanceof LongValue) {
				lowKey = Integer.parseInt(arg0.getRightExpression().toString());
				highKey = Integer.parseInt(arg0.getRightExpression().toString());
			} else if (arg0.getLeftExpression() instanceof LongValue){
				lowKey = Integer.parseInt(arg0.getLeftExpression().toString());
				highKey = Integer.parseInt(arg0.getLeftExpression().toString());
			}
		} else {
			selectConditions.add(arg0);
		}
	}

	@Override
	public void visit(GreaterThan arg0) {
		if (hasIndex(arg0)) {
			
			if (arg0.getRightExpression() instanceof LongValue) {
				Integer currLowKey = Integer.parseInt(arg0.getRightExpression().toString());
				// Set lowKey as curr+1 because the index scan operator does an inclusive comparison 
				if (lowKey == null) {
					lowKey = currLowKey + 1;
				} else if (lowKey != null && currLowKey.compareTo(lowKey) > 0) {
					lowKey = currLowKey + 1;
				}
			} else if (arg0.getLeftExpression() instanceof LongValue) {
				Integer currHighKey = Integer.parseInt(arg0.getLeftExpression().toString());
				// Set lowKey as curr-1 because the index scan operator does an inclusive comparison 
				if (highKey == null) {
					highKey = currHighKey - 1;
				} else if (highKey != null && currHighKey.compareTo(highKey) < 0) {
					highKey = currHighKey - 1;
				}
			}

		} else {
			selectConditions.add(arg0);
		}
	}

	@Override
	public void visit(GreaterThanEquals arg0) {
		if (hasIndex(arg0)) {
			if (arg0.getRightExpression() instanceof LongValue) {
				Integer currLowKey = Integer.parseInt(arg0.getRightExpression().toString());
				if (lowKey == null) {
					lowKey = currLowKey;
				} else if (lowKey != null && currLowKey.compareTo(lowKey) > 0) {
					lowKey = currLowKey;
				}
			} else if (arg0.getLeftExpression() instanceof LongValue) {
				Integer currHighKey = Integer.parseInt(arg0.getLeftExpression().toString());
				if (highKey == null) {
					highKey = currHighKey;
				} else if (highKey != null && currHighKey.compareTo(highKey) < 0) {
					highKey = currHighKey;
				}
			}

		} else {
			selectConditions.add(arg0);
		}
	}
	
	@Override
	public void visit(MinorThan arg0) {
		if (hasIndex(arg0)) {
			if (arg0.getRightExpression() instanceof LongValue) {
				Integer currHighKey = Integer.parseInt(arg0.getRightExpression().toString());
				// Set lowKey as curr-1 because the index scan operator does an inclusive comparison 
				if (highKey == null) {
					highKey = currHighKey - 1;
				} else if (highKey != null && currHighKey.compareTo(highKey) < 0) {
					highKey = currHighKey - 1;
				}
			} else if (arg0.getLeftExpression() instanceof LongValue) {
				Integer currLowKey = Integer.parseInt(arg0.getLeftExpression().toString());
				// Set lowKey as curr+1 because the index scan operator does an inclusive comparison 
				if (lowKey == null) {
					lowKey = currLowKey + 1;
				} else if (lowKey != null && currLowKey.compareTo(lowKey) > 0) {
					lowKey = currLowKey + 1;
				}
			}


		} else {
			selectConditions.add(arg0);
		}
	}

	@Override
	public void visit(MinorThanEquals arg0) {	
		if (hasIndex(arg0)) {
			if (arg0.getRightExpression() instanceof LongValue) {
				Integer currHighKey = Integer.parseInt(arg0.getRightExpression().toString());
				if (highKey == null) {
					highKey = currHighKey;
				} else if (highKey != null && currHighKey.compareTo(highKey) < 0) {
					highKey = currHighKey;
				}
			} else if (arg0.getLeftExpression() instanceof LongValue) {
				Integer currLowKey = Integer.parseInt(arg0.getLeftExpression().toString());
				if (lowKey == null) {
					lowKey = currLowKey;
				} else if (lowKey != null && currLowKey.compareTo(lowKey) > 0) {
					lowKey = currLowKey;
				}
			}

		} else {
			selectConditions.add(arg0);
		}
	}

	@Override
	public void visit(NotEqualsTo arg0) {
		selectConditions.add(arg0);
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

