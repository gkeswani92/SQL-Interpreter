package logical_operators;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.statement.select.OrderByElement;
import operators.ExternalSortOperator;
import operators.InMemorySortOperator;
import operators.Operator;
import utils.PlanBuilderConfigFileReader;
import utils.Tuple;

public class SortLogicalOperator extends LogicalOperator {

	LogicalOperator child;
	List<String> sortConditions;
	List<Tuple> tuples;
	Integer currIndex;
	
	public SortLogicalOperator(List<OrderByElement> sortConditions, LogicalOperator child) {
		this.child = child;
		this.currIndex = 0;
		this.tuples = new ArrayList<Tuple>();
		this.sortConditions = new ArrayList<String>();
		
		if(sortConditions != null) {
			for (OrderByElement el : sortConditions) {
				this.sortConditions.add(el.toString());				
			}
		}
	}

	@Override
	public Operator getNextPhysicalOperator() {
		
		PlanBuilderConfigFileReader config = PlanBuilderConfigFileReader.getInstance();
		
		// Read config file and decide which sort to invoke
		if (config.getSortType() == 0) {
			return new InMemorySortOperator(this.child.getNextPhysicalOperator(), this.sortConditions,
					this.tuples, this.currIndex);
		} else {
			return new ExternalSortOperator(this.sortConditions, this.child.getNextPhysicalOperator(), config.getSortBuffer());
		}

	}
}
