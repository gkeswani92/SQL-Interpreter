package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;

public class BinaryFileReader implements TupleReader {

	private FileInputStream fis;
	private FileChannel channel;
	private ByteBuffer bb;
	
	public BinaryFileReader(String filename) throws FileNotFoundException {
		fis = new FileInputStream(new File(filename));
		channel = fis.getChannel();
		bb = ByteBuffer.allocateDirect(4*1024);
		bb.clear();
		getNextTuple();
	}
	
	@Override
	public Tuple getNextTuple() {
		if(bb.remaining() == 0)
			updateBufferWithNextPage();
	}

	
	private void updateBufferWithNextPage() {
		int[] ipArr = new int [(int)channel.size()/4];
		 System.out.println("File size: "+channel.size()/4);
		  long len = 0;
		  int offset = 0;
		  
		  while ((len = channel.read(bb)) != -1){  
		    bb.flip();
		    bb.asIntBuffer().get(ipArr,offset,(int)len/4);
			
			
		    for(Integer i:ipArr)
		    	System.out.println(i);
		    offset += (int)len/4;
		    bb.clear();
		    break;
		  }
	}
}
