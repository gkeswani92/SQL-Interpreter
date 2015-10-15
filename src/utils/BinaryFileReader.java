package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class BinaryFileReader implements TupleReader {

	private final static int INDEX_OF_FIRST_TUPLE = 2;
	private FileInputStream fis;
	private FileChannel channel;
	private ByteBuffer bb;
	private Integer numAttr;
	private Integer numTuples;
	private int[] tupleArr;
	private int tupleIndex;
	
	public BinaryFileReader(String filename, String tableName) throws FileNotFoundException {
		fis = new FileInputStream(new File(filename));
		channel = fis.getChannel();
		bb = ByteBuffer.allocateDirect(4*1024);
		bb.clear();
		updateBufferWithNextPage();
	}
	
	@Override
	public int[] getNextTuple() {
		if(numTuples == 0) {
			if (updateBufferWithNextPage() == 1) {
				return null;
			}
		}
		
		int[] tuple = new int [numAttr];
		
		for (int i = 0; i < numAttr; i++) {
			tuple[i] = tupleArr[tupleIndex];
			tupleIndex++;
		}
		
		numTuples--;
		return tuple;
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
	
	public static void main(String[] args) throws FileNotFoundException {
		
		PrintWriter writer = new PrintWriter("/Users/tanvimehta/Desktop/CORNELL..YAY!!/Courses/CS5321/project2/samples/input/db/data/test");
		BinaryFileReader bfr = new BinaryFileReader("/Users/tanvimehta/Desktop/CORNELL..YAY!!/Courses/CS5321/project2/samples/input/db/data/Boats", "Boats");
		
		int[] tuple = bfr.getNextTuple();
		while (tuple != null) {
			String tu = "";
			for (int i = 0; i < tuple.length; i++) {
				tu = tu + " " + tuple[i];
			}
			writer.println(tu);
			tuple = bfr.getNextTuple();
		}
		writer.close();
	}
}
