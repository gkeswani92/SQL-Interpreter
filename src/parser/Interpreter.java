package parser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import operators.Operator;
import operators.ProjectOperator;
import operators.ScanOperator;
import operators.SelectOperator;

/**
 * Class for getting started with JSQLParser. Reads SQL statements from
 * a file and extracts the elements of the SQL query to be further evaluated.
 * @author Tanvi Mehta
 */
public class Interpreter {

	private static final String queriesFile = "sql/testQueries.sql";

	public static void main(String[] args) {
		try {
			CCJSqlParser parser = new CCJSqlParser(new FileReader(queriesFile));
			Statement statement;
			while ((statement = parser.Statement()) != null) {
				System.out.println("Read statement: " + statement);

				Select select = (Select) statement;
                PlainSelect body = (PlainSelect) select.getSelectBody();
                @SuppressWarnings("unchecked")
				List<SelectItem> selectAttr = body.getSelectItems();
                
                if (body.getJoins() == null) {
	                //Decision statements to decide from which operator to enter
	                if (selectAttr.size() == 1 && selectAttr.get(0).toString() == "*") { 
	                	
	                	//Enter from the scan clause if query has no where clause
	                	if (body.getWhere() == null) {
	                		ScanOperator op = new ScanOperator(body.getFromItem().toString());
	                		op.dump();
	                	}
	                	
	                	// Enter from selectOperator if query has where clause
	                	else {  		
	                		System.out.println("Where clause is:  " + body.getWhere());
	                        ScanOperator scanOp = new ScanOperator(body.getFromItem().toString());
	                        SelectOperator selOp = new SelectOperator(body.getWhere(), scanOp);	                              
	                        selOp.dump();
	                	}
	                } 
	                
	                else {    	
	            		//Depending on whether the where clause is present or not, we decide the child i.e scan or select              	
	                    ScanOperator scanOp = new ScanOperator(body.getFromItem().toString());
	                    ProjectOperator projOp = null;
	            		if(body.getWhere()!=null) {
	                        SelectOperator child = new SelectOperator(body.getWhere(), scanOp);
	                        projOp = new ProjectOperator(body, child);
	            		}
	            		else {
	            			projOp = new ProjectOperator(body, scanOp);
	            		}
	                	projOp.dump();
	                }    
				} else {
					// ADD JOIN LOGIC HERE
					handleJoin(body);
				}
			}
		} catch (Exception e) {
			System.err.println("Exception occurred during parsing");
			e.printStackTrace();
		}
	}
	
	public static void handleJoin(PlainSelect body) {
		Map<String, List<Operator>> tableOperators = new HashMap<String, List<Operator>>();
		List<Expression> joins = new ArrayList<>();
		
		WhereBuilder where = new WhereBuilder(tableOperators, joins);
		body.getWhere().accept(where);
		
	}
}