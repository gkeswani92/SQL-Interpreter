package operators;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import utils.Tuple;

/**
 * Abstract class for Operator. Consists of common methods
 * to be implemented by all operators.
 * @author Gaurav, Tanvi and Sahana (gk368,tmm259 and sv387)
 */
public abstract class Operator {

	/**
	 * Gets the next tuple from the table
	 * @return next tuple that satifies operator's conditions
	 */
	public abstract Tuple getNextTuple();
    
	/**
	 * Resets the getNextTuple to start from the beginning of the table
	 */
	public abstract void reset();
	
	public void reset(int index) {
		
	}
	
	/**
	 * Prints the output generated by the operator
	 * @throws FileNotFoundException 
	 */
    public void dump() {
		Tuple currentTuple = getNextTuple();
		String tableDump = null;
		if (currentTuple != null)
			tableDump = new String(currentTuple.toStringAttributes()+"\n");
		while(currentTuple != null) {
			tableDump = tableDump + currentTuple.toStringValues() +  "\n";
			currentTuple = getNextTuple();
		}
		PrintWriter out = null;
		try {
			out = new PrintWriter("D:/Database_Practicals/SQL-Interpreter/hello.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.write(tableDump);
		out.close();
    }
}
