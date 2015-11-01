package operators;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import utils.BinaryFileReader;
import utils.BinaryFileWriter;
import utils.Tuple;
import utils.TupleComparator;

public class ExternalSortOperator extends SortOperator {

	private Integer numBufferPages, fileStatusPass0, pass0RunCount;
	private Integer childCount = 0;
	private ArrayList<Tuple> buffer;
	private List<String> sortConditions, passZeroFiles;
	private List<BinaryFileReader> passOneBuffers;
	private String tempDir = "/Users/gaurav/Documents/Eclipse/SQL-Interpreter/samples/external_sort/";

	public ExternalSortOperator(List<String> sortConditions, Operator child, Integer numBufferPages) {
		super(sortConditions, child);
		this.numBufferPages = numBufferPages;
		buffer 				= new ArrayList<Tuple>();
		sortConditions 		= new ArrayList<String>(); 
		passZeroFiles 		= new ArrayList<String>(); //Keeps track of the files created in pass 0
		pass0RunCount 		= 0;
		childCount++; //Added to the path of the temp directory because we dont want 2nd relation overwriting the first
		fillBufferForPass0();
		sortCondition();
	}	

	public void fillBufferForPass0(){
		
		buffer.clear();
		Tuple t  = child.getNextTuple();
		
		if(t != null){
			
			//Calculating the number of tuples from the relation we can read in to
			//the buffer pages for pass 0
			Integer numTuples = (4096 * numBufferPages) / (4 * t.getNumAttributes());
			buffer.add(t);
			numTuples--;
			
			while(numTuples != 0){
				
				t = child.getNextTuple();
				
				//Indicator that all the tuples of the file have been read in. So once
				//you finish processing all the currently added tuples, dont come back for more
				if(t == null){
					fileStatusPass0 = -1;
					return;
				}
				
				//Added a tuple to the buffer and decremented count by 1 and read in the next tuple
				buffer.add(t);
				numTuples--;
			}
		}
		
		//Indicator that there are more tuples that need to be read in
		fileStatusPass0 = 0;
	}
	
	public void sortCondition(){
		
		//If there are no sort conditions, we need to sort by all attributes
		//Doing this by reading the attributes of the first tuple in the buffer
		if(sortConditions == null){
			sortConditions = new ArrayList<String>(buffer.get(0).getArributeList());
		}
		
		//Add all the attributes that are not in the sorting condition at the 
		//end of the list by taking care of the order
		else{
			List<String> attributes = new ArrayList<String>(buffer.get(0).getArributeList());
			attributes.removeAll(sortConditions);
			sortConditions.addAll(attributes);
		}
	}
	
	public void passZero(){
		
		while(true){
			try {
				//Sorts the tuples in the buffer currently using in memory sort and the 
				//sort conditions we had created
				buffer.sort(new TupleComparator(sortConditions));
				
				//Create the path of the pass 0 file and write all sorted tuples to it
				String passZeroDir = tempDir + childCount + "/pass0/"+ pass0RunCount;
				BinaryFileWriter bfw = new BinaryFileWriter(passZeroDir);
				bfw.writeTupleCollection(buffer);
				passZeroFiles.add(passZeroDir);
				
				//If file status is -1, it means it has no more tuples to give us. If not,
				//read the remaining tuples from the file
				if(fileStatusPass0 == -1)
					return;
				
				fillBufferForPass0();
				pass0RunCount++;
			} 
			catch (FileNotFoundException e) {
				System.out.println("Exception in Pass 0");
				e.printStackTrace();
			}
		}
	}
	
	public void sortAndMerge(){
		//Need to code for further passes so as to merge files until we end up
		//with no tuples left. Leaving it here for tonight. 
		
		//PS - I have just cranked out the above code with my understanding. There
		//might be a few bugs in there somewhere. 
		
	}
	
	@Override
	public Tuple getNextTuple() {
		
		//Sort the relation using in memory sort in pass 0 and get the list of files
		//it was broken down into
		passZero();
		sortAndMerge();
		
		return null;	
	}
}
