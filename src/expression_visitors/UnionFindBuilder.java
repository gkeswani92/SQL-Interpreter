package expression_visitors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
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
import union_find.UnionFind;
import union_find.UnionFindElement;

public class UnionFindBuilder implements ExpressionVisitor {

	UnionFind unionFind;
	Map<List<String>,Expression> unusableJoinConditions;
	List<Expression> unusableSelectConditions; 
	
	public UnionFindBuilder(UnionFind unionFind, Map<List<String>, Expression> unusableJoin, List<Expression> unusableSelect) {
		this.unionFind = unionFind;
		this.unusableJoinConditions = unusableJoin;
		this.unusableSelectConditions = unusableSelect;
	}
	
	@Override	
	public void visit(EqualsTo arg0) {
		
		//If the left half of the expression is a column
		if(arg0.getLeftExpression() instanceof Column){
			Column left = (Column) arg0.getLeftExpression();
			UnionFindElement leftElement = unionFind.find(left);
			
			//If a union find element does not exist, create one for the left column
			if(leftElement == null)
				leftElement = unionFind.create(left);
			
			if(arg0.getRightExpression() instanceof Column){
				Column right = ((Column) arg0.getRightExpression());
				UnionFindElement rightElement = unionFind.find(right);
				
				//If right element does not exist, add it to the left element
				//Else merge the left and right element
				if(rightElement == null) {
					leftElement.addAttributeToElement(right);
				} else {
					unionFind.merge(leftElement, rightElement);
				}
			} else {
				Long value = ((LongValue)arg0.getRightExpression()).toLong();
				leftElement.setEqualityConstraint(value);
			}
		} 
		
		else {
			Long value = ((LongValue)arg0.getLeftExpression()).toLong();
			Column right = ((Column) arg0.getRightExpression());
			UnionFindElement rightElement = unionFind.find(right);
			
			//If a union find element does not exist, create one for the right column
			if(rightElement == null) {
				rightElement = unionFind.create(right);
			}
			rightElement.setEqualityConstraint(value);
		}
	}

	@Override
	public void visit(GreaterThan arg0) {
		//If the left half of the expression is a column
		if(arg0.getLeftExpression() instanceof Column){
			Column left = (Column) arg0.getLeftExpression();
			UnionFindElement leftElement = unionFind.find(left);
			
			//If a union find element does not exist, create one for the left column
			if(leftElement == null)
				leftElement = unionFind.create(left);
			
			if (arg0.getRightExpression() instanceof Column) {
				Column right = (Column)arg0.getRightExpression();
				if (left.getTable().toString().equals(right.getTable().toString())) {
					unusableSelectConditions.add(arg0);
				} else {
					List<String> attributeNames = new ArrayList<String>();
					attributeNames.add(left.getTable().toString());
					attributeNames.add(right.getTable().toString());
					// To make sure order of table names in condition does not matter
					java.util.Collections.sort(attributeNames);
					
					if (unusableJoinConditions.containsKey(attributeNames)){
						Expression aggregate = new AndExpression(unusableJoinConditions.get(attributeNames), arg0);
						unusableJoinConditions.put(attributeNames, aggregate);
					} else {
						unusableJoinConditions.put(attributeNames, arg0);
					}

				}
			} else {
				Long value = ((LongValue)arg0.getRightExpression()).toLong();
				leftElement.setLowerBound(value + 1);
			}
		} 
		
		else {
			Long value = ((LongValue)arg0.getLeftExpression()).toLong();
			Column right = ((Column) arg0.getRightExpression());
			UnionFindElement rightElement = unionFind.find(right);
			
			//If a union find element does not exist, create one for the right column
			if(rightElement == null) {
				rightElement = unionFind.create(right);
			}
			rightElement.setUpperBound(value -1);
		}
	}

	@Override
	public void visit(GreaterThanEquals arg0) {
		//If the left half of the expression is a column
		if(arg0.getLeftExpression() instanceof Column){
			Column left = (Column) arg0.getLeftExpression();
			UnionFindElement leftElement = unionFind.find(left);
			
			//If a union find element does not exist, create one for the left column
			if(leftElement == null)
				leftElement = unionFind.create(left);
			
			if(arg0.getRightExpression() instanceof Column) {
				Column right = (Column)arg0.getRightExpression();
				if (left.getTable().toString().equals(right.getTable().toString())) {
					unusableSelectConditions.add(arg0);
				} else {
					List<String> attributeNames = new ArrayList<String>();
					attributeNames.add(left.getTable().toString());
					attributeNames.add(right.getTable().toString());
					// To make sure order of table names in condition does not matter
					java.util.Collections.sort(attributeNames);
					
					if (unusableJoinConditions.containsKey(attributeNames)){
						Expression aggregate = new AndExpression(unusableJoinConditions.get(attributeNames), arg0);
						unusableJoinConditions.put(attributeNames, aggregate);
					} else {
						unusableJoinConditions.put(attributeNames, arg0);
					}
				}			
			} else {
				Long value = ((LongValue)arg0.getRightExpression()).toLong();
				leftElement.setLowerBound(value);
			}
		} 
		
		else {
			Long value = ((LongValue)arg0.getLeftExpression()).toLong();
			Column right = ((Column) arg0.getRightExpression());
			UnionFindElement rightElement = unionFind.find(right);
			
			//If a union find element does not exist, create one for the right column
			if(rightElement == null) {
				rightElement = unionFind.create(right);
			}
			rightElement.setUpperBound(value);
		}
	}
	
	@Override
	public void visit(MinorThan arg0) {
		//If the left half of the expression is a column
		if(arg0.getLeftExpression() instanceof Column){
			Column left = (Column) arg0.getLeftExpression();
			UnionFindElement leftElement = unionFind.find(left);
			
			//If a union find element does not exist, create one for the left column
			if(leftElement == null)
				leftElement = unionFind.create(left);
			
			if(arg0.getRightExpression() instanceof Column){
				Column right = (Column)arg0.getRightExpression();
				if (left.getTable().toString().equals(right.getTable().toString())) {
					unusableSelectConditions.add(arg0);
				} else {
					List<String> attributeNames = new ArrayList<String>();
					attributeNames.add(left.getTable().toString());
					attributeNames.add(right.getTable().toString());
					// To make sure order of table names in condition does not matter
					java.util.Collections.sort(attributeNames);
					
					if (unusableJoinConditions.containsKey(attributeNames)){
						Expression aggregate = new AndExpression(unusableJoinConditions.get(attributeNames), arg0);
						unusableJoinConditions.put(attributeNames, aggregate);
					} else {
						unusableJoinConditions.put(attributeNames, arg0);
					}
				}
			} else {
				Long value = ((LongValue)arg0.getRightExpression()).toLong();
				leftElement.setUpperBound(value-1);
			}
		} 
		
		else {
			Long value = ((LongValue)arg0.getLeftExpression()).toLong();
			Column right = ((Column) arg0.getRightExpression());
			UnionFindElement rightElement = unionFind.find(right);
			
			//If a union find element does not exist, create one for the right column
			if(rightElement == null) {
				rightElement = unionFind.create(right);
			}
			rightElement.setLowerBound(value+1);
		}
	}

	@Override
	public void visit(MinorThanEquals arg0) {
		//If the left half of the expression is a column
		if(arg0.getLeftExpression() instanceof Column){
			Column left = (Column) arg0.getLeftExpression();
			UnionFindElement leftElement = unionFind.find(left);
			
			//If a union find element does not exist, create one for the left column
			if(leftElement == null)
				leftElement = unionFind.create(left);
			
			if(arg0.getRightExpression() instanceof Column){
				Column right = (Column)arg0.getRightExpression();
				if (left.getTable().toString().equals(right.getTable().toString())) {
					unusableSelectConditions.add(arg0);
				} else {
					List<String> attributeNames = new ArrayList<String>();
					attributeNames.add(left.getTable().toString());
					attributeNames.add(right.getTable().toString());
					// To make sure order of table names in condition does not matter
					java.util.Collections.sort(attributeNames);
					
					if (unusableJoinConditions.containsKey(attributeNames)){
						Expression aggregate = new AndExpression(unusableJoinConditions.get(attributeNames), arg0);
						unusableJoinConditions.put(attributeNames, aggregate);
					} else {
						unusableJoinConditions.put(attributeNames, arg0);
					}
				}
			} else {
				Long value = ((LongValue)arg0.getRightExpression()).toLong();
				leftElement.setUpperBound(value);
			}
		} 
		
		else {
			Long value = ((LongValue)arg0.getLeftExpression()).toLong();
			Column right = ((Column) arg0.getRightExpression());
			UnionFindElement rightElement = unionFind.find(right);
			
			//If a union find element does not exist, create one for the right column
			if(rightElement == null) {
				rightElement = unionFind.create(right);
			}
			rightElement.setLowerBound(value);
		}
	}

	@Override
	public void visit(NotEqualsTo arg0) {
		if (!(arg0.getLeftExpression() instanceof Column) || !(arg0.getRightExpression() instanceof Column)) {
			unusableSelectConditions.add(arg0);
		} else {
			Column left = (Column)arg0.getLeftExpression();
			Column right = (Column)arg0.getRightExpression();
			List<String> attributeNames = new ArrayList<String>();
			attributeNames.add(left.getTable().toString());
			attributeNames.add(right.getTable().toString());
			unusableJoinConditions.put(attributeNames, arg0);
		}
	}
	
	@Override
	public void visit(AndExpression arg0) {
		arg0.getLeftExpression().accept(this);
		arg0.getRightExpression().accept(this);
	}
	
	@Override
	public void visit(LongValue arg0) {
	}
	

	@Override
	public void visit(Column arg0) {
		
		
	}

	

	
	@Override
	public void visit(NullValue arg0) {
		
		
	}

	@Override
	public void visit(Function arg0) {
		
		
	}

	@Override
	public void visit(InverseExpression arg0) {
		
		
	}

	@Override
	public void visit(JdbcParameter arg0) {
		
		
	}

	@Override
	public void visit(DoubleValue arg0) {
		
		
	}

	@Override
	public void visit(DateValue arg0) {
		
		
	}

	@Override
	public void visit(TimeValue arg0) {
		
		
	}

	@Override
	public void visit(TimestampValue arg0) {
		
		
	}

	@Override
	public void visit(Parenthesis arg0) {
		
		
	}

	@Override
	public void visit(StringValue arg0) {
		
		
	}

	@Override
	public void visit(Addition arg0) {
		
		
	}

	@Override
	public void visit(Division arg0) {
		
		
	}

	@Override
	public void visit(Multiplication arg0) {
		
		
	}

	@Override
	public void visit(Subtraction arg0) {
		
		
	}

	@Override
	public void visit(OrExpression arg0) {
		
		
	}

	@Override
	public void visit(Between arg0) {
		
		
	}



	@Override
	public void visit(InExpression arg0) {
		
		
	}

	@Override
	public void visit(IsNullExpression arg0) {
		
		
	}

	@Override
	public void visit(LikeExpression arg0) {
		
		
	}



	@Override
	public void visit(SubSelect arg0) {
		
		
	}

	@Override
	public void visit(CaseExpression arg0) {
		
		
	}

	@Override
	public void visit(WhenClause arg0) {
		
		
	}

	@Override
	public void visit(ExistsExpression arg0) {
		
		
	}

	@Override
	public void visit(AllComparisonExpression arg0) {
		
		
	}

	@Override
	public void visit(AnyComparisonExpression arg0) {
		
		
	}

	@Override
	public void visit(Concat arg0) {
		
		
	}

	@Override
	public void visit(Matches arg0) {
		
		
	}

	@Override
	public void visit(BitwiseAnd arg0) {
		
		
	}

	@Override
	public void visit(BitwiseOr arg0) {
		
		
	}

	@Override
	public void visit(BitwiseXor arg0) {
		
	}
}