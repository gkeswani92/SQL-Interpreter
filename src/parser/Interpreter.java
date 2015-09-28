package parser;

import java.io.FileReader;
import java.util.Iterator;
import java.util.List;

import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
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
                List<SelectItem> selectAttr = body.getSelectItems();
                
                //Decision statements to decide from which operator to enter
                if (selectAttr.size() == 1 && selectAttr.get(0).toString() == "*") { 
                	
                	//Enter from the scan clause if query has no where clause
                	if (body.getWhere() == null) {
                		ScanOperator op = new ScanOperator(body.getFromItem().toString());
                		EvaluateOperatorVisitor eval = new EvaluateOperatorVisitor();
                		op.accept(eval);
                	}
                	
                	// Enter from selectOperator if query has where clause
                	else {  		
                		System.out.println("Where clause is:  " + body.getWhere());
                		ScanOperator op = new ScanOperator(body.getFromItem().toString());
                        SelectOperator selOp = new SelectOperator(body, op);
                        EvaluateOperatorVisitor eval = new EvaluateOperatorVisitor();
                        selOp.accept(eval);
                	}
                } 
                
                else {
                	ProjectOperator projOp = new ProjectOperator(body);
                	projOp.dump();
                }
    
			}
		} catch (Exception e) {
			System.err.println("Exception occurred during parsing");
			e.printStackTrace();
		}
	}
}