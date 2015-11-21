package parser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import expression_visitors.UnionFindBuilder;
import indexing.BuildIndex;
import logical_operators.DuplicateEliminationLogicalOperator;
import logical_operators.JoinLogicalOperator;
import logical_operators.LogicalOperator;
import logical_operators.ProjectLogicalOperator;
import logical_operators.ScanLogicalOperator;
import logical_operators.SelectLogicalOperator;
import logical_operators.SortLogicalOperator;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import operators.Operator;
import operators.ScanOperator;
import statistics.GatherStatistics;
import union_find.UnionFind;
import union_find.UnionFindElement;
import utils.DatabaseCatalog;
import utils.DirectoryCleanUp;
import utils.DumpRelations;
import utils.IndexConfigFileReader;
import utils.PlanBuilderConfigFileReader;

/**
 * Class for getting started with JSQLParser. Reads SQL statements from
 * a file and extracts the elements of the SQL query to be further evaluated.
 * @author Gaurav, Tanvi and Sahana (gk368,tmm259 and sv387)
 */
public class Interpreter {

	private static String queriesFile;
	private static String inputSrcDir;
	private static String outputScrDir;
	private static String tempMergeOutput;
	private static String interpreterConfigPath;
	private static DumpRelations writeToFile;
	private static Integer buildIndex;
	private static Integer executeQueries;
	
	public static void main(String[] args) {
		
		//Building the single instance of the database catalog and config file reader
		if(args.length == 1){
			
			//Reading in the new config file for input, out, temp dirs and flags
			interpreterConfigPath = args[0];
			ArrayList<String> params = DatabaseCatalog.getInstance().getConfigPaths(interpreterConfigPath);
			
			inputSrcDir  	= params.get(0);
            outputScrDir 	= params.get(1);
            tempMergeOutput = params.get(2);
            buildIndex 		= Integer.parseInt(params.get(3));
            executeQueries  = Integer.parseInt(params.get(4));
            queriesFile 	= inputSrcDir+"/queries.sql";
            
			DatabaseCatalog.getInstance().buildDbCatalog(inputSrcDir);
			PlanBuilderConfigFileReader.getInstance().readConfigFile(inputSrcDir);
			PlanBuilderConfigFileReader.getInstance().setTempDir(tempMergeOutput);
			IndexConfigFileReader.getInstance().readConfigFile(inputSrcDir);
			writeToFile = new DumpRelations(outputScrDir);
			
			//Gathering statistics about the given relations
			Set<String> tableNames = DatabaseCatalog.getInstance().getTableNames();
			GatherStatistics.gatherStatistics(tableNames);
		}
		
		if(buildIndex == 1)
			BuildIndex.buildIndexes();
		
		if(executeQueries == 1)
			executeQueries();
	}

	/**
	 * Method to read the queries file and execute them if the execute queries 
	 * flag is set to True
	 */
	private static void executeQueries() {
		
		long startTime = System.nanoTime();
		
		try {
			CCJSqlParser parser = new CCJSqlParser(new FileReader(queriesFile));
			Statement statement = parser.Statement();
			LogicalOperator root;
			Integer queryCount = 1;
							
			while (statement != null) {
				
				try {
					System.out.println("Read statement: " + statement);
					Select select = (Select) statement;
	                PlainSelect body = (PlainSelect) select.getSelectBody();
	                
	                //Handling the alias for tables 
	                if(body.getFromItem().getAlias()!=null){	        			
	        			String basetableName = body.getFromItem().toString().substring(0,body.getFromItem().toString().indexOf(" "));
	        			DatabaseCatalog.getInstance().setEntryForAlias(basetableName, body.getFromItem().getAlias());
	        		}
	                if(body.getJoins()!=null){
		                for (Object exp: body.getJoins()) {
		                	if(exp.toString().contains("AS")){
		        				String basetableName = ((Join)exp).getRightItem().toString().substring(0,((Join)exp).getRightItem().toString().indexOf(" "));
		        				DatabaseCatalog.getInstance().setEntryForAlias(basetableName, ((Join)exp).getRightItem().getAlias());
		                	}
		                }
	                }
	        			
	                
	                @SuppressWarnings("unchecked")
					List<SelectItem> selectAttr = body.getSelectItems();
	             
	                //Decision statement to check if the query consists of a join or not
	                if (body.getJoins() == null) 
		                root = handleWithoutJoin(body, selectAttr);
					
	                else {
	                	root = handleJoin(body);
	                	
	                	//Making join the child of project operation
		                if (selectAttr.size() != 1 || selectAttr.get(0).toString() != "*") {
		                	root = new ProjectLogicalOperator(body, root);
		                }
	                }
	                
	                // If order by clause exists or distinct operator exists, first make sort the parent
	                if (body.getOrderByElements() != null || body.getDistinct() != null) {
						@SuppressWarnings("unchecked")
						LogicalOperator temp = new SortLogicalOperator(body.getOrderByElements(), root);
	                	root = temp;
	                			
	                	//If distinct exists, make it the parent
	                	if(body.getDistinct() != null)
	                		root = new DuplicateEliminationLogicalOperator(root);
	                }
	    			
	                Operator physicalRoot = constructPhysicalPlan(root);
	                //writeToFile.writeRelationToBinaryFile(physicalRoot, queryCount);
	                physicalRoot.dump();
	    			//writeToFile.writeTestFile(physicalRoot, queryCount, outputFileFormat);
	    			
	    			long endTime = System.nanoTime();
	    			System.out.println("Took "+(endTime - startTime)/10e8 + " sec"); 
	    			System.out.println("<------------End of query----------->");
	    			DirectoryCleanUp.cleanupTempDir();
	    			
	    			//Reading the next statement
	    			statement = parser.Statement();
	    			queryCount ++;
				}
			
				catch(Exception e) {
					System.err.println("Exception occurred during parsing current query. Moving to next query");
					e.printStackTrace();
					statement = parser.Statement();
					queryCount ++;
				}	
			}
		}
		catch (Exception e) {
			System.err.println("Exception occurred during parsing");
			e.printStackTrace();
		}
	}
	
	private static Operator constructPhysicalPlan (LogicalOperator root) {
		Operator opRoot = root.getNextPhysicalOperator();
		return opRoot;
	}
	
	private static LogicalOperator handleWithoutJoin(PlainSelect body, List<SelectItem> selectAttr) {
		LogicalOperator root;
		//If the query has a SELECT *
		if (selectAttr.size() == 1 && selectAttr.get(0).toString() == "*") { 
			
			//Enter from scan if there is no where clause
			if (body.getWhere() == null) 
				root = new ScanLogicalOperator(body);

			//Enter from select if query has where clause
			else {  		
				System.out.println("Where clause is:  " + body.getWhere());
		        ScanLogicalOperator scanOp = new ScanLogicalOperator(body);
		        root = new SelectLogicalOperator(body.getWhere(), scanOp);	                              
			}
		} 
		
		//If the query consists of projections
		else 
			root = handleProjectWithoutJoin(body);
		return root;
	}

	/**
	 * Depending on whether the where clause is present or not, we 
	 * decide the child i.e scan or select 
	 * @param body
	 * @return 
	 */
	private static LogicalOperator handleProjectWithoutJoin(PlainSelect body) {
		             	
		ScanLogicalOperator scanOp = new ScanLogicalOperator(body);
		ProjectLogicalOperator projOp = null;
		
		if(body.getWhere()!=null) {
			SelectLogicalOperator child = new SelectLogicalOperator(body.getWhere(), scanOp);
		    projOp = new ProjectLogicalOperator(body, child);
		}
		else 
			projOp = new ProjectLogicalOperator(body, scanOp);
		
		return projOp;
	}
	
	/**
	 * Takes in the select body, visits the where clause(to construct the query plan) and calls the joinOperator
	 * @param body select body
	 */
	public static LogicalOperator handleJoin(PlainSelect body) {
		
		List<String> allChildren = new ArrayList<String>();
		getChildrenFromQuery(body, allChildren);
		
		UnionFind unionFind = new UnionFind();
		List<Expression> unusableJoinConditions = new ArrayList<Expression>();
		List<Expression> unusableSelectConditions = new ArrayList<Expression>();
		
		// If where clause exists, call visitor class to get map of basic operators and list of join conditions
		if (body.getWhere() != null) {
			UnionFindBuilder ufb = new UnionFindBuilder(unionFind, unusableJoinConditions, unusableSelectConditions);
			body.getWhere().accept(ufb);
		}
		
		Map<String, LogicalOperator> children = new LinkedHashMap<String, LogicalOperator>();
		
		for(String tableName: allChildren){
			List<UnionFindElement> unionFindElements = unionFind.findElementsForRelation(tableName);
			Expression unsuableSelectionConditions = getUnusableConditionsForRelation(tableName, unusableSelectConditions);
			Expression unionFindConditions= unionFind.getExpressionForUnionFindElements(unionFindElements, tableName);
			Expression finalExpressionRelation = null;
			
			if(unionFindConditions != null) {
				if(unsuableSelectionConditions != null) {
					finalExpressionRelation = new AndExpression(unionFindConditions, unsuableSelectionConditions);
				} else {
					finalExpressionRelation = unionFindConditions;
				}
			} else {
				if(unsuableSelectionConditions != null) {
					finalExpressionRelation = unsuableSelectionConditions;
				}
			}
			
			
			if(finalExpressionRelation == null){
				children.put(tableName, new ScanLogicalOperator(tableName));
			} else {
				children.put(tableName, new SelectLogicalOperator(finalExpressionRelation, new ScanLogicalOperator(tableName)));
			}
			
		}
		return new JoinLogicalOperator(unusableJoinConditions, children);
	}
	
	private static Expression getUnusableConditionsForRelation(String tableName, List<Expression> unusableSelectConditions) {
		
		List<Expression> conditions = new ArrayList<Expression>();
		
		for(Expression currentExp: unusableSelectConditions){
			BinaryExpression currentExpression = ((BinaryExpression)currentExp);
			
			//Left is a column
			if(currentExpression.getLeftExpression() instanceof Column){
				if(((Column)currentExpression.getLeftExpression()).getTable().toString().equals(tableName)){
					conditions.add(currentExp);
				}
			} else {
				if(((Column)currentExpression.getRightExpression()).getTable().toString().equals(tableName)){
					conditions.add(currentExp);
				}
			}
		}
		
		//Creating a conjunction of all the unusable conditions
		if (conditions.size() == 0){
			return null;
		} else if (conditions.size() == 1) {
			return conditions.get(0);
		}
		
		Expression finalExpression = conditions.get(0);
		for(int i=1; i<conditions.size(); i++){
			finalExpression = new AndExpression(finalExpression, conditions.get(i));
		}
		return finalExpression;
	}
	
	private static void getChildrenFromQuery(PlainSelect body, List<String> allChildren) {
		if (body.getFromItem().getAlias() == null) {
			allChildren.add(body.getFromItem().toString());
		} else {
			allChildren.add(body.getFromItem().getAlias());
		}
		
		if (body.getJoins()!=null) {
            for (Object exp: body.getJoins()) {
            	if (exp.toString().contains("AS")) {
    				allChildren.add(((Join)exp).getRightItem().getAlias());
            	} else {
            		allChildren.add(((Join)exp).getRightItem().toString());
            	}
            }
        }
	}
	
	/**
	 * Get the basic(select or scan) operator tree (could be multiple select operators with AND conjunct) for the input table
	 * @param tableOperators map of table name to basic operators from where clause
	 * @param tableName
	 * @return operator tree for table name
	 */
	public static LogicalOperator getBasicOperator(Map<String, List<LogicalOperator>> tableOperators, String tableName) {
		
		LogicalOperator op;
		// If entry for table name does not exist in the map, use scan operator
		if (tableOperators.get(tableName) == null || tableOperators.get(tableName).isEmpty()) {
			op = new ScanLogicalOperator(tableName);
		} else {
			op = tableOperators.get(tableName).get(0);
		}
		return op;
	}
	
	/**
	 * Get join condition on the input tables, if multiple join conditions exists, conjunct them using AND
	 * @param joins list of all joins from the where clause
	 * @param leftTable left join table
	 * @param rightTable right join table
	 * @return final join condition
	 */
	public static Expression getJoinCondition(List<Entry<List<String>,Expression>> joins, String leftTable, String rightTable) {
		
		Expression finalJoinExpression = null;
		for (Entry<List<String>,Expression> join: joins) {
			List<String> tables = join.getKey();
			// If join condition contains both tables
			if (tables.contains(leftTable) && tables.contains(rightTable)) {
				if (finalJoinExpression == null) {
					finalJoinExpression = join.getValue();
				}
				// If tables have multiple join conditions, conjunct using AND
				else {
					finalJoinExpression = new AndExpression(finalJoinExpression, join.getValue());
				}
			}
		}
		return finalJoinExpression;
	}
}