package parser;

import java.io.FileReader;
import java.util.List;

import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import operators.ScanOperator;

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


                if (selectAttr.size() == 1 && selectAttr.get(0).toString() == "*") {
                    ScanOperator op = new ScanOperator(body.getFromItem().toString());
                    op.dump();
                }

                System.out.println("Where clause is:  " + body.getWhere());

//                Expression exp = body.getWhere();

//                System.out.println("Select Items are:  " + body.getSelectItems());
//                System.out.println("From clause is " + body.getFromItem().toString());
//                Scan op = new Scan(body.getFromItem().toString());
//                System.out.println(op.getNextTuple().toStringValues());
//                System.out.println(op.getNextTuple().toStringValues());
//                op.reset();
//                System.out.println(op.getNextTuple().toStringValues());
//                System.out.println(op.getNextTuple().toStringValues());

			}
		} catch (Exception e) {
			System.err.println("Exception occurred during parsing");
			e.printStackTrace();
		}
	}
}