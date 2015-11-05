package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Set;
import indexing.IndexNode;
import indexing.LeafNode;
import indexing.Node;
import indexing.Record;

public class IndexBinaryFileWriter {

	private String filePath;
	private ByteBuffer buffer;
	private FileOutputStream fos;
	private FileChannel channel;
	private File outputFile;
	
	public IndexBinaryFileWriter(String filePath) throws FileNotFoundException {		
		this.filePath = filePath;
		outputFile = new File(filePath);
		if(!outputFile.exists()) {
			try {
				outputFile.getParentFile().mkdirs();
				outputFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Unable to create the output file");
			}
		}
		fos = new FileOutputStream(filePath);
	    // allocate a channel to write that file
	    channel = fos.getChannel();
	    skipHeaderPage();
	}
	
	public void skipHeaderPage() {
		
		buffer = ByteBuffer.allocate(1024 * 4);	
		try {
			channel.write(buffer);
			buffer.clear();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Writes the leaf nodes to the file
	 * @param leaf
	 */
	public void writeLeafNodeToFile(LeafNode leaf) {
		
		buffer = ByteBuffer.allocate(1024 * 4);
		
		// Flag to show this is a leaf
		buffer.putInt(0);
		buffer.putInt(leaf.size());
		
		Set<Integer> dataKeys = leaf.getDataEntries().keySet();
		for (Integer key: dataKeys) {
			List<Record> records = leaf.getDataEntries().get(key);
			buffer.putInt(key);
			buffer.putInt(records.size());
			
			//Writes the page id, tuple id pairs one record at a time
			for (Record rec: records) {
				buffer.putInt(rec.getPageId());
				buffer.putInt(rec.getTupleId());
			}	
		}
		flushBuffer();
	}
	
	/**
	 * Serializes the index nodes to the file
	 * @param currentIndexNode
	 */
	public void writeIndexToFile(IndexNode currentIndexNode){
		
		buffer = ByteBuffer.allocate(1024 * 4);
		
		//Flag to show that it is an index node
		buffer.putInt(1);
		buffer.putInt(currentIndexNode.getKeys().size());
		
		//Writes all the keys to the buffer
		for(Integer key: currentIndexNode.getKeys()){
			buffer.putInt(key);
		}
		
		//Writes the address of the children to the buffer
		for(int i=0; i<currentIndexNode.getChildren().size(); i++){
			buffer.putInt(currentIndexNode.getChildren().get(i).getAddress());
		}
		flushBuffer();
	}
	
	/**
	 * Writes the current contents of the buffer to the channel and
	 * clears it
	 */
	public void flushBuffer(){
		try {
			channel.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		buffer.clear();
	}
	
	/**
	 * Writes the address of the root, number of leaves and the order of the binary
	 * plus tree in the header page of the file
	 * @param root
	 * @param numLeaves
	 * @param order
	 */
	public void serializeHeader(Node root, Integer numLeaves, Integer order){
		try{
			channel.position(0);
			buffer = ByteBuffer.allocate(1024 * 4);
			buffer.putInt(root.getAddress());
			buffer.putInt(numLeaves);
			buffer.putInt(order);
			flushBuffer();
			
			
			channel.close();
			fos.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
