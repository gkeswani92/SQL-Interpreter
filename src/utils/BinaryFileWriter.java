package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;


public class BinaryFileWriter implements TupleWriter {	
	
	private String fileName;
	ByteBuffer buffer;
	private FileOutputStream fos;
	private FileChannel channel;
	private Integer noOfTuplesWritten;
	private Integer tupleSize;
	boolean append = true;
	Integer bufferPos;
	File outputFile;
	
	public BinaryFileWriter(String fileName) throws FileNotFoundException {		
		this.fileName = fileName;
		outputFile = new File(fileName);
		if(!outputFile.exists()) {
			try {
				outputFile.getParentFile().mkdirs();
				outputFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Unable to create the output file");
			}
		}
		fos = new FileOutputStream(fileName);
	    // allocate a channel to write that file
	    channel = fos.getChannel();
	    updateBufferWithNewByteBuffer();	
	}	

	private void updateBufferWithNewByteBuffer() {
		
		noOfTuplesWritten = 0;
		tupleSize = 0;
		buffer = ByteBuffer.allocate( 1024 * 4 );	
		//buffer.clear();
		buffer.putInt(tupleSize);
		buffer.putInt(noOfTuplesWritten);	
	}

	@Override
	public int writeNextTuple(Tuple tuple) {		
		
		Integer element;
		// check if the buffer can take the entire tuple
		if(tuple != null && ! (buffer.remaining()>(tuple.getArributeList().size()*4))){			
			// get a new bytebuffer and set the value for prev page buffer with tuple details
			writeByteBufferToFile();
			updateBufferWithNewByteBuffer();			
		} 
		else if(tuple == null){
			writeByteBufferToFile();
			try {
				channel.close();
				fos.close();
			} 
			catch (IOException e) {
				System.out.println("Unable to close streams after writing");
				e.printStackTrace();
			}			
			return 0;			
		}
		
		//add all the tuple values into the buffer
		Iterator<?> iterator = tuple.getAttributeValues().values().iterator();
		while(iterator.hasNext()){
			element = (Integer) iterator.next();
			buffer.putInt(element);				
		}
		noOfTuplesWritten++;
		return tupleSize = tuple.getArributeList().size();		
	} 
	
	public void writeByteBufferToFile(){
		buffer.position(0);
		buffer.putInt(tupleSize);
		buffer.putInt(noOfTuplesWritten);
		buffer.position(0);
		try {
			channel.write(buffer);
		} 
		catch (IOException e) {
			System.out.println("Couldn't write the buffer to the output file"+ fileName);
			e.printStackTrace();
		}
	}
	
	public void writeTupleCollection(ArrayList<Tuple> tuples){
		for(Tuple t: tuples)
			writeNextTuple(t);
		writeNextTuple(null);
	}
}
