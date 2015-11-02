package operators;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import utils.BinaryFileReader;
import utils.BinaryFileWriter;
import utils.ConfigFileReader;
import utils.Tuple;
import utils.TupleComparator;

public class ExternalSortOperator extends SortOperator {

	private Integer numBufferPages, fileStatusPass0, pass0RunCount, tuplesPerPage, passCount;
	private static Integer childCount = 0;
	private List<Tuple> buffer;
	private List<String> sortConditions, inputFilePaths, outputFilePaths;
	private List<BinaryFileReader> fanInBuffers;

	private String tempDir = ConfigFileReader.getInstance().getTempDir();
	private boolean pass0Done;
	private String tableName, sortedFile;
	private String[] attributes;
	private BinaryFileReader sortedFileReader;

	public ExternalSortOperator(List<String> sortConditions, Operator child, Integer numBufferPages) {
		super(sortConditions, child);
		this.numBufferPages = numBufferPages;
		buffer 				= new ArrayList<Tuple>();
		this.sortConditions = new ArrayList<String>();
		this.sortConditions.addAll(sortConditions);
		inputFilePaths 		= new ArrayList<String>(); //Keeps track of the files created in pass 0
		outputFilePaths 	= new ArrayList<String>();
		fanInBuffers 		= new ArrayList<BinaryFileReader>();
		pass0RunCount 		= 0;
		passCount			= 1;
		pass0Done 			= false;
		sortedFile 			= null;
		sortedFileReader    = null;
	}	

	public void fillBufferForPass0(){
		
		buffer.clear();
		Tuple t  = child.getNextTuple();
		
		if(t != null){
			
			//Calculating the number of tuples from the relation we can read in to
			//the buffer pages for pass 0
			Integer numTuples = ((4096-8) * numBufferPages) / (4 * t.getNumAttributes()); //TODO: Check if we need ceil. Maybe? :P
			tuplesPerPage = Math.floorDiv(numTuples, numBufferPages);
			numTuples = tuplesPerPage * numBufferPages;
			buffer.add(t);
			numTuples--;
			
			//Storing the table name and the list of all attributes for the 
			//condition when we have a join as the left child and need to create a 
			//buffered reader
			tableName = t.getTableName();
			attributes = t.getArributeArray();
			
			while(numTuples != 0){
								
				t = child.getNextTuple();
				
				//Indicator that all the tuples of the file have been read in. So once
				//you finish processing all the currently added tuples, dont come back for more
				if (t == null){
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
		
		fillBufferForPass0();
		sortCondition();
		childCount++;
		BinaryFileWriter bfw;
		
		while(true){
			try {
				//Sorts the tuples in the buffer currently using in memory sort and the 
				//sort conditions we had created
				buffer.sort(new TupleComparator(sortConditions));
				
				//Create the path of the pass 0 file and write all sorted tuples to it
				String passZeroDir = tempDir + childCount + "/pass0/" + pass0RunCount;
				bfw = new BinaryFileWriter(passZeroDir);
				bfw.writeTupleCollection(buffer);
				bfw.writeNextTuple(null);
				inputFilePaths.add(passZeroDir);
				
				//If file status is -1, it means it has no more tuples to give us. If not,
				//read the remaining tuples from the file
				if(fileStatusPass0 == -1){
					return;
				}			
				
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
		
		Integer availableBuffers 	   = numBufferPages - 1;
		Integer fanInSize 			   = availableBuffers;
		Integer fanInCount			   = 0;
		List<Tuple> mergeTuples		   = new ArrayList<Tuple>();
		List<Tuple> outputBuffer 	   = new ArrayList<Tuple>();
		List<Integer> indexToBeDropped = new ArrayList<Integer>();
		
		while(inputFilePaths != null && !inputFilePaths.isEmpty()) {
			
			//Number of file paths to consider in one merge fan in
			if(inputFilePaths.size() < availableBuffers)
				fanInSize = inputFilePaths.size();
			
			//Filling the buffers with the fan size of buffered file readers
			List<String> currSubList = inputFilePaths.subList(0, fanInSize);
			getBinaryFileReader(currSubList);
			inputFilePaths.removeAll(currSubList);
						
			//Gets one tuple each from all of the file readers and maintain a list of
			//indexes to be dropped if file reader hits null (no more tuples)
			for(int i=0; i<fanInBuffers.size(); i++){
				Tuple currentTuple = fanInBuffers.get(i).getNextTuple();
				
				if(currentTuple == null) {
					indexToBeDropped.add(i);
				}
				else{
					if (tableName != null) {
						currentTuple.updateTuple(tableName);
					}
					mergeTuples.add(i, currentTuple);
				}
			}
			
			//Drops the file readers and tuples that have hit null
			for (Integer i: indexToBeDropped){
				fanInBuffers.remove(i);
				mergeTuples.remove(i);
			}
			
			BinaryFileWriter bfw = null;
			try {
				String outputDir = tempDir + childCount + "/pass" + passCount + "/" + fanInCount;
				bfw = new BinaryFileWriter(outputDir);
				outputFilePaths.add(outputDir);
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			while (fanInBuffers != null && !fanInBuffers.isEmpty() && mergeTuples!= null && !mergeTuples.isEmpty()) {
				
				//Sort the current set of tuples and get the minimum one to be put into the output buffer
				List<Tuple> sortedTemp = new ArrayList<Tuple>();
				sortedTemp.addAll(mergeTuples);
				sortedTemp.sort(new TupleComparator(sortConditions));
				
				//Add the smallest tuple to the output buffer and increment its buffered
				//reader to the next tuple
				Integer minTupleIndex = mergeTuples.indexOf(sortedTemp.get(0));
				
				addToOutputBuffer(outputBuffer, mergeTuples.get(minTupleIndex), fanInCount, bfw);
				
				//Gets the next tuple for the appropriate BFR. If next tuple is null,
				//removes that entry from BFR and Merge Tuple List
				Tuple newTuple = fanInBuffers.get(minTupleIndex).getNextTuple();
				
				if(newTuple == null){
					fanInBuffers.remove(minTupleIndex.intValue());
					mergeTuples.remove(minTupleIndex.intValue());
				}
				else{
					if (tableName != null) {
						newTuple.updateTuple(tableName);
					}
					mergeTuples.remove(minTupleIndex.intValue());
					mergeTuples.add(minTupleIndex, newTuple);
				}
			}
			
			addToOutputBuffer(outputBuffer, null, fanInCount, bfw);
			fanInCount++; //B - 1 pages have been combined and written out to disk. Need to write to a new file now

		}
		
		if(outputFilePaths.size() == 1) {
			sortedFile = outputFilePaths.get(0);
			return;			
		}

		inputFilePaths.clear();
		inputFilePaths.addAll(outputFilePaths);
		outputFilePaths.clear();
		passCount++;
		sortAndMerge();
	}
	
	public void addToOutputBuffer(List<Tuple> outputBuffer, Tuple t, Integer fanInCount, 
			BinaryFileWriter bfw){
		
		// If t == null, flush output buffer, else add the tuple to the output 
		// buffer and flush as needed(when full)
		if (t != null) {
			outputBuffer.add(t);
		}
		
		if (t == null || outputBuffer.size() == tuplesPerPage) {
			bfw.writeTupleCollection(outputBuffer);
			outputBuffer.clear();
			
			if (t == null) {
				bfw.writeNextTuple(null);
			}
		}		
	}
	
	public void getBinaryFileReader(List<String> filePaths){
		try {
			for(String path: filePaths){
				BinaryFileReader bfr = new BinaryFileReader(path, tableName);
				if(tableName == null){
					bfr.setAttributes(attributes);
				}
				fanInBuffers.add(bfr);
			}		
		} 
		catch (FileNotFoundException e) {
			System.out.println("Bug in creating BFR's");
			e.printStackTrace();
		}
	}
	
	public BinaryFileReader getSortedFileReader(String filePath) {
		try {
			BinaryFileReader bfr = new BinaryFileReader(filePath, tableName);
			if(tableName == null){
				bfr.setAttributes(attributes);
			}
			return bfr;
		} 
		catch (FileNotFoundException e) {
			System.out.println("Bug in creating BFR for sorted file.");
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void doExternalSort() {
		//Sort the relation using in memory sort in pass 0 and get the list of files
		//it was broken down into
		if (!pass0Done) {
			passZero();
			pass0Done = true;
		}
		sortAndMerge();
	}
	
	
	@Override
	public Tuple getNextTuple() {
		
		if (sortedFile == null) {
			doExternalSort();
			sortedFileReader = getSortedFileReader(sortedFile);
		}
		
		Tuple tableLessTuple = sortedFileReader.getNextTuple();
		
		if (tableLessTuple != null) {
			tableLessTuple.setTableName(tableName);
			if (tableName != null) {
				tableLessTuple.updateTuple(tableName);
			}
		}
		
		return tableLessTuple;
	}
	
	@Override
	public void reset(int index) {
		if (sortedFile == null && index == 0) {
			return;
		}
		
		sortedFileReader.closeStuff();
		sortedFileReader = getSortedFileReader(sortedFile);
		
		for (int i = 0; i < index; i++) {
			sortedFileReader.getNextTuple();
		}
	}
}
