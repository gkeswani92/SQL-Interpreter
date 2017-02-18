package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import indexing.Index;
import indexing.LeafNode;
import indexing.Record;

public class IndexBinaryFileReader {

	private FileInputStream fis;
	private FileChannel channel;
	private ByteBuffer bb; 
	
	public IndexBinaryFileReader(Index index){
		
		try {
			String filePath = DatabaseCatalog.getInstance().getInputDir() + "/db/indexes/" + index.getTableName() + "." + index.getAttribute();
			fis = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		channel = fis.getChannel();
		bb = ByteBuffer.allocate(1024*4);
	}
	
	/**
	 * Caller method that controls the navigation to the correct leaf node
	 * @param lowKey
	 */
	public void navigateToLeafNode(Integer lowKey) {
		
		//If lower end is null, we need to start scanning from the leftmost leaf
		//which is the first page after the header
		if (lowKey == null) {
			setChannelPosition(4096);
			readPageIntoBuffer();
			int[] contentsOfPage = new int[1024];
			bb.asIntBuffer().get(contentsOfPage,0,contentsOfPage.length);
		} 
		
		else {
			//Traverse down the tree starting at the root
			Integer rootAddr = getRootAddress();
			
			//After this statement, the buffer will have the leaf node which
			//contains the key we are looking for
			traverseDownTheIndex(lowKey, rootAddr);
		}
	}
	
	/**
	 * Recursively traverses down the tree to the leaf node which contains the 
	 * low key we are searching for
	 * @param lowKey
	 * @param address
	 */
	public void traverseDownTheIndex(Integer lowKey, Integer address){
		
		//Setting the channel position to the page we need to read, reading it into 
		//the buffer and converting it to an integer array
		setChannelPosition(address*4096);
		readPageIntoBuffer();
		int[] contentsOfPage = new int[1024];
		bb.asIntBuffer().get(contentsOfPage,0,contentsOfPage.length);
		
		//If this is a leaf node, we return since the buffer contains the key
		//we are looking for
		Integer nodeType = contentsOfPage[0];
		if(nodeType == 0)
			return;
		
		//Figuring out which child to traverse down from the current index node
		Integer numKeys = contentsOfPage[1];
		for(int i=2; i<numKeys+2; i++){
			if (lowKey < contentsOfPage[i]){
				// If it is the leftmost key then don't do a -1 to 
				// avoid going into the last key rather than the 1st child
				traverseDownTheIndex(lowKey, contentsOfPage[i+numKeys]);
				return;
			}
		}
		
		//If the low key is not smaller than any of the keys, it means it is
		//the last child
		traverseDownTheIndex(lowKey, contentsOfPage[2+2*numKeys]);
		return;
	}
	
	/**
	 * Sets the channels positions to the integer that has been passed in
	 * @param position
	 */
	public void setChannelPosition(long position){
		try {
			channel.position(position);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the address for the page the root is on in the file
	 * @return
	 * 		Page number on which root data has been stored
	 */
	public Integer getRootAddress(){
		Integer rootAddr = -1;
		try{
			bb = ByteBuffer.allocate(4);
			channel.read(bb);
			bb.position(0);
			rootAddr= bb.getInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rootAddr;
	}
	
	/**
	 * Gets the number of leaves of the index
	 * @return
	 * 		number of leaves
	 */
	public Integer getNumLeaves(){
		Integer numLeaves = -1;
		try{
			bb = ByteBuffer.allocate(8);
			channel.read(bb);
			bb.position(0);
			numLeaves= bb.getInt();
			numLeaves = bb.getInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return numLeaves;
	}
	
	/**
	 * Method to read one complete page into the byte buffer from the 
	 * current position of the channel
	 */
	public void readPageIntoBuffer(){
		try{
			bb = ByteBuffer.allocate(4096);
			channel.read(bb);
			bb.position(0);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public LeafNode getNextLeafNode() {
		
		LeafNode currentLeaf = null;
		int[] contentsOfPage = new int[1024];
		bb.asIntBuffer().get(contentsOfPage,0,contentsOfPage.length);
		
		if (contentsOfPage[0] == 1) {
			return null;
		}
		
		int numDataEntries = contentsOfPage[1];
		int countDataEntriesProcessed = 0;
		int i = 2;
		
		while(countDataEntriesProcessed < numDataEntries){
			Integer key = contentsOfPage[i++];
			Integer numRecords = contentsOfPage[i++];
			int j;
			
			for(j = i; j < (i + 2 * numRecords); j = j + 2){
				Record currentRecord = new Record(contentsOfPage[j],  contentsOfPage[j+1]);	
				if(currentLeaf == null)
					currentLeaf = new LeafNode(key, currentRecord);
				else
					currentLeaf.addRecord(key, currentRecord);
			}
			countDataEntriesProcessed++;
			i = j;
		}
		
		readPageIntoBuffer();
		return currentLeaf;
	}
	
	public void closeStuff() {
		try {
			channel.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
