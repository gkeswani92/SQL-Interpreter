package parser;

import java.io.FileReader;
import java.util.List;

import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
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
                
                // Enter from scanOperator if select * and where clause is null
                List<SelectItem> selectAttr = body.getSelectItems();
                if (selectAttr.size() == 1 && selectAttr.get(0).toString() == "*" && body.getWhere() == null) {
                    ScanOperator op = new ScanOperator(body.getFromItem().toString());
                    op.dump();
                } 
                
                // Enter from selectOperator if query has where clause
                else if (body.getWhere() != null) {
                    System.out.println("Where clause is:  " + body.getWhere());
                    SelectOperator selOp = new SelectOperator(body);
                    selOp.dump();
                }

			}
		} catch (Exception e) {
			System.err.println("Exception occurred during parsing");
			e.printStackTrace();
		}
	}
}