package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;

public class BinaryFileReader implements TupleReader {

	private final static int INDEX_OF_FIRST_TUPLE = 2;
	private FileInputStream fis;
	private FileChannel channel;
	private ByteBuffer bb;
	private Integer numAttr;
	private Integer numTuples;
	private int[] tupleArr;
	private int ipIndex, fileOffset;
	
	public BinaryFileReader(String filename, String tableName) throws FileNotFoundException {
		fis = new FileInputStream(new File(filename));
		channel = fis.getChannel();
		bb = ByteBuffer.allocateDirect(4*1024);
		bb.clear();
		fileOffset = 0;
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
			tuple[i] = tupleArr[ipIndex];
			ipIndex++;
		}
		
		numTuples--;
		return tuple;
	}

	
	private Integer updateBufferWithNextPage() {
		
		long len = 0;
		try {	
		 tupleArr = new int [(int)channel.size()/4];
		 System.out.println("File size: "+channel.size()/4);
		  
		  while ((len = channel.read(bb)) != -1){  
		    bb.flip();
		    bb.asIntBuffer().get(tupleArr,fileOffset,(int)len/4);
		  }
		  fileOffset = fileOffset + (int)len/4;
		  if (fileOffset > (channel.size()/4)) {
			  return 1;
		  }
		} catch (IOException e) {
			System.out.println("Something went wrong in updateBufferWithNextPage.");
		}
		  ipIndex = 2;

		  numAttr = tupleArr[0];
		  numTuples = tupleArr[1];
		  return 0;
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		
		PrintWriter writer = new PrintWriter("/Users/tanvimehta/Desktop/CORNELL..YAY!!/Courses/CS5321/project2/samples/input/db/data/cryBabyBoats");
		BinaryFileReader bfr = new BinaryFileReader("/Users/tanvimehta/Desktop/CORNELL..YAY!!/Courses/CS5321/project2/samples/input/db/data/Boats", "Boats");
		
		int[] tuple = bfr.getNextTuple();
		while (tuple != null) {
			String tu = "";
			for (int i = 0; i < tuple.length; i++) {
				tu = tu + " " + tuple[i];
			}
			writer.println(tu);
//			System.out.println(tu);
			tuple = bfr.getNextTuple();
		}
		writer.close();
	}
}
