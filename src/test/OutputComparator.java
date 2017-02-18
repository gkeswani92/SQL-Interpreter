package test;

//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.nio.channels.FileChannel;
//import java.security.cert.CRLReason;
//import java.util.ArrayList;
//import java.util.List;
//import utils.Tuple;

public class OutputComparator {
	
//	private String binaryFile;
//	private String humanReadableFile;
//	private List<FileTuple> binaryTuples = new ArrayList<FileTuple>();
//	private List<FileTuple> humanReadableTuples = new ArrayList<FileTuple>();
//	
//	private FileInputStream fis;
//	private FileChannel channel;
//	private ByteBuffer bb;
//	private final static int INDEX_OF_FIRST_TUPLE = 2;
//	private Integer binaryNumAttr;
//	private Integer binaryNumTuples;	
//	private int[] binaryFileIntArr;
//	private int binaryTupleStartIndex;
//	
//	private FileReader fileReaderObj;
// 	private BufferedReader file;
// 	
//	public OutputComparator() {		
//		this.binaryFile = null;
//		this.humanReadableFile = null;		
//	}
//	
//	private boolean checkIfIdentical(String binaryFile, String humanReadableFile){
//		this.binaryFile = binaryFile;
//		this.humanReadableFile = humanReadableFile;	
//		
//		try {
//			readBinaryFileTuples();
//			sortBinaryFileTuples();
//			readHumanreadableFileTuples();
//			sortHumanreadableFileTuples();
//			//writeSortedFiles();
//			if(compareFileTuples()){
//				System.out.println("The files are identical");
//				return true;
//			}else{
//				System.out.println("The file entries don't match");
//				return false;
//			}
//			
//		} catch (FileNotFoundException e) {			
//			e.printStackTrace();
//			System.out.println("unable to read the given file");
//		}
//		return false;
//	}
//	
//	
//	private void writeSortedFiles() {
//		String sortedHumanReadableFile = humanReadableFile +"_sorted";
//		String sortedBinaryFile = binaryFile +"_sorted";
//		String writablesbf = getTextString(binaryTuples);
//		String writableshrf = getTextString(humanReadableTuples);
//		writeToFile(sortedHumanReadableFile,writableshrf);
//		writeToFile(sortedBinaryFile, writablesbf);		
//	}
//
//
//	private void writeToFile(String filename, String content) {
//		try {
//			FileWriter writer = new FileWriter(filename);
//			if(content!=null){
//				writer.write(content);
//				writer.close();				
//			}
//		} catch (IOException e) {				
//				e.printStackTrace();
//				System.out.println("Error occured while writing the sorted output");
//		}
//		
//	}
//
//
//	private String getTextString(List<FileTuple> fileTupleList) {
//		String fileFormattedTuples = "";
//		for(int i=0;i<fileTupleList.size();i++){
//			fileFormattedTuples = fileFormattedTuples + fileTupleList.get(i).toStringValues() +  "\n";
//		}
//		return fileFormattedTuples;
//	}
//
//
//	private boolean compareFileTuples() {
//		List<Integer> compareConditions = new ArrayList<Integer>();		
//		if(binaryTuples.size() != humanReadableTuples.size()){
//			System.out.println("Unequal number of tuples present");
//			return false;
//		}else{
//			int comparevalue = 0;
//			if(binaryTuples.size()!=0 && humanReadableTuples.size()!=0){
//				for(int i = 0; i< humanReadableTuples.get(0).getAttributeCount(); i++){
//					compareConditions.add(i, i);
//				}
//				FileTupleComparator validator = new FileTupleComparator(compareConditions);
//				for(int i=0;i<binaryTuples.size();i++){
//					comparevalue = validator.compare(binaryTuples.get(i), humanReadableTuples.get(i));
//					if(comparevalue != 0){
//						return false;
//					}				
//				}
//			}
//			
//			return true;
//		}
//		
//	}
//
//
//	private void sortHumanreadableFileTuples() {
//		List<Integer> sortConditions = new ArrayList<Integer>();
//		if(humanReadableTuples.size() !=0){
//			for(int i = 0; i< humanReadableTuples.get(0).getAttributeCount(); i++){
//				sortConditions.add(i, i);
//			}
//			humanReadableTuples.sort(new FileTupleComparator(sortConditions));	
//		}			
//	}
//
//
//	private void readHumanreadableFileTuples() {
//		try {
// 			fileReaderObj = new FileReader(humanReadableFile);
// 			file = new BufferedReader(fileReaderObj); 			
// 		 	FileTuple currentFileTuple = null;
// 		 	while(true){
// 		 		try {
// 	 		 		String line = file.readLine();
// 	 		 		if(line==null){
// 	 		 			return;
// 	 		 		}
// 	 		 		currentFileTuple = new FileTuple(line);
// 	 		 		humanReadableTuples.add(currentFileTuple);
// 	 		 	} catch (IOException e) {
// 	 		 			e.printStackTrace();
// 	 		 			System.out.println("Error reading from the hman readable file");
// 	 		 	}
// 		 	}
// 		 	
// 		} catch (FileNotFoundException e) {
// 			e.printStackTrace();
// 			System.out.println("Unable to open the hman readable file");
// 		}
//	}
//
//
//	private void sortBinaryFileTuples() {
//		//assign the sort condition on all the attributes
//		List<Integer> sortConditions = new ArrayList<Integer>();
//		if(binaryTuples.size() != 0){
//			for(int i = 0; i< binaryNumAttr; i++){
//				sortConditions.add(i, i);
//			}
//			binaryTuples.sort(new FileTupleComparator(sortConditions));	
//		}
//			
//	}
//	
//	//method to read the binary file data and construct the tuples all in memory
//	//assign the attributes not based on table.just based position
//
//	private void readBinaryFileTuples() throws FileNotFoundException {
//		fis = new FileInputStream(new File(binaryFile));
//		channel = fis.getChannel();
//		bb = ByteBuffer.allocateDirect(4*1024);
//		bb.clear();
//		boolean pagesLeft = true;
//		while(pagesLeft){
//			FileTuple nextFileTuple = null; 
//			if(updateBufferWithNextPage()){
//				//read in all tuples in the given page
//				nextFileTuple = getNextTuple();
//				while(nextFileTuple != null){
//					binaryTuples.add(nextFileTuple);
//					nextFileTuple = getNextTuple();
//				}				
//			}else{
//				pagesLeft=false;
//			}			
//		}		
//	}
//	
//	
//	private boolean updateBufferWithNextPage() {		
//		try {	
//			if (channel.read(bb) != -1) {  
//				bb.flip();
//				binaryNumAttr = bb.asIntBuffer().get(0);
//				binaryNumTuples = bb.asIntBuffer().get(1);
//				binaryFileIntArr = new int [binaryNumAttr*binaryNumTuples+INDEX_OF_FIRST_TUPLE];				
//				bb.asIntBuffer().get(binaryFileIntArr,0,binaryFileIntArr.length);
//				bb.clear();
//				channel.read(bb);
//		  } else {
//			  return false;
//		  }
//		} catch (IOException e) {
//			System.out.println("Something went wrong in updateBufferWithNextPage.");
//		}
//		binaryTupleStartIndex  = INDEX_OF_FIRST_TUPLE;
//		  return true;
//	}	
//	
//	public FileTuple getNextTuple() {
//		
//		if(binaryNumTuples != null){
//			if(binaryNumTuples == 0) {				
//					return null;
//			}
//		}
//		
//		int[] tuple = new int [binaryNumAttr];		
// 		for (int i = 0; i < binaryNumAttr; i++) {
//			tuple[i] = binaryFileIntArr[binaryTupleStartIndex];
//			binaryTupleStartIndex++;
//		}		
// 		binaryNumTuples--;
//		return new FileTuple(tuple);
//	}
//	
//
//	/*public static void main(String[] args) {
//		String binaryFile = "D:/Database_Practicals/SQL-Interpreter_old/samples/expected/query10";
//		String humanReadableFile= "D:/Database_Practicals/SQL-Interpreter_old/samples/expected/query10_humanreadable";	
//		OutputComparator test = new OutputComparator();
//		System.out.println(test.checkIfIdentical(binaryFile, humanReadableFile));
//	}*/

}
