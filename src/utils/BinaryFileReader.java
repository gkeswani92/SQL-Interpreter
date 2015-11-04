package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import indexing.Record;

public class BinaryFileReader implements TupleReader {

	private final static int INDEX_OF_FIRST_TUPLE = 2;
	private FileInputStream fis;
	private FileChannel channel;
	private ByteBuffer bb;
	private Integer numAttr;
	private Integer numTuples;
	private int[] tupleArr;
	private int tupleIndex;
	private String[] attributes;
	private String tableName;
	
	private Integer tupleId, pageId;
	
	public BinaryFileReader(String tableName) throws FileNotFoundException {
		attributes = DatabaseCatalog.getInstance().getTableAttributes(tableName);
		fis = new FileInputStream(new File(DatabaseCatalog.getInstance().getBinaryDataFilePath(tableName)));
		channel = fis.getChannel();
		bb = ByteBuffer.allocateDirect(4*1024);
		bb.clear();
		updateBufferWithNextPage();
		this.tableName = tableName;
		this.pageId = 0;
		this.tupleId = 0;
	}
	
	public BinaryFileReader(String fileName, String tableName) throws FileNotFoundException {	
		attributes = DatabaseCatalog.getInstance().getTableAttributes(tableName);
		fis = new FileInputStream(new File(fileName));
		channel = fis.getChannel();
		bb = ByteBuffer.allocateDirect(4*1024);
		bb.clear();
		updateBufferWithNextPage();
	}
	
	@Override
	public Tuple getNextTuple() {
		if(numTuples != null){
			if(numTuples == 0) {
				if (updateBufferWithNextPage() == 1) {
					return null;
				}
			}
		}
		
		int[] tuple = new int [numAttr];
		
 		for (int i = 0; i < numAttr; i++) {
			tuple[i] = tupleArr[tupleIndex];
			tupleIndex++;
		}
		
		
		numTuples--;
		return new Tuple(tuple, attributes, tableName);
	}
	
	private Integer updateBufferWithNextPage() {
		
		try {	
			if (channel.read(bb) != -1) {  
				bb.flip();
				numAttr = bb.asIntBuffer().get(0);
				numTuples = bb.asIntBuffer().get(1);
				tupleArr = new int [numAttr*numTuples+INDEX_OF_FIRST_TUPLE];				
				bb.asIntBuffer().get(tupleArr,0,tupleArr.length);
				bb.clear();
				channel.read(bb);
		  } else {
			  return 1;
		  }
		} catch (IOException e) {
			System.out.println("Something went wrong in updateBufferWithNextPage.");
		}
		  tupleIndex = INDEX_OF_FIRST_TUPLE;

		  return 0;
	}
	
	public void closeStuff() {
		try {
			channel.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setAttributes(String[] attributes) {
		this.attributes = attributes;
		this.numAttr = attributes.length;
	}
	
	public void setChannelToPage(int index) {
		try {
			bb.clear();
			this.channel.position(index*4096);
			updateBufferWithNextPage();
		} catch (Exception e) {
			System.out.println("Something went wrong in resetting the channel position");
			e.printStackTrace();
		}
	}
		
	public Record getNextRecord() {
		if(numTuples != null){
			if(numTuples == 0) {
				pageId++;
				tupleId = 0;
				if (updateBufferWithNextPage() == 1) {
					return null;
				}
			}
		}
		
		int[] tuple = new int [numAttr];
		
 		for (int i = 0; i < numAttr; i++) {
			tuple[i] = tupleArr[tupleIndex];
			tupleIndex++;
		}
		
		numTuples--;
		return new Record(pageId, tupleId++, new Tuple(tuple, attributes, tableName));
	}
	
	public List<Record> getAllRecords() {
		
		List<Record> allRecords = new ArrayList<Record>();
		Record r = getNextRecord();
		
		while (r != null) {
			allRecords.add(r);
			r = getNextRecord();
		}
		
		return allRecords;
	}
	
	public List<Tuple> getAllTuples() {
		
		List<Tuple> allTuples = new ArrayList<Tuple>();
		Tuple t = getNextTuple();
		
		while (t != null) {
			allTuples.add(t);
			t = getNextTuple();
		}
		
		return allTuples;
	}
}
