package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import indexing.Index;

public class IndexBinaryFileReader {

	private String filePath;
	private FileInputStream fis;
	private FileChannel channel;
	private ByteBuffer bb;
	private Index index;
	
	public IndexBinaryFileReader(Index index){
		filePath = DatabaseCatalog.getInstance().getInputDir() + "/db/indexes/" + index.getTableName() + "." + index.getAttribute();
		try {
			fis = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		channel = fis.getChannel();
		bb = ByteBuffer.allocate(1024*4);
		this.index = index;
	}
	
	public void navigateToLeafNode(Integer lowKey) {
		if (lowKey == null) {
			try {
				channel.position(4096);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				bb = ByteBuffer.allocate(8);
				channel.read(bb);
				bb.position(0);
				Integer rootAddr = bb.getInt();
				channel.position(rootAddr*4096);
				
				bb = ByteBuffer.allocate(8);
				channel.read(bb);
				bb.position(0);
				System.out.println(bb.getInt());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
