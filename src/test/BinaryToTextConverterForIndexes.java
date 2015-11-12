package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class BinaryToTextConverterForIndexes {
	
	private static ByteBuffer buffer;
	private static final int BUFFER_SIZE = 4096;
	private static FileChannel fileChannel;
	
	private static void convert(String inputFile, String outputFile) {		
			
		FileInputStream fin;
		try {
			fin = new FileInputStream(inputFile);
			fileChannel = fin.getChannel();				
			FileWriter writer = new FileWriter(outputFile);			
			String t;
		
			// Deal with the header page
			buffer.clear();
			int nBytesRead = fileChannel.read(buffer);
			buffer.flip();
			int rootAddress = buffer.getInt();
			int leaves = buffer.getInt();
			int order = buffer.getInt();
			t = "Header Page info: tree has order " + order + ", a root at address " + rootAddress + " and " + leaves + " leaf nodes \n";
			writer.write(t);			
			
			String indexNode = "\n---------Next layer is index nodes---------\n";
			String leafNode = "---------Next layer is leaf nodes---------\n";
			String rootNode = "\nRoot node is: ";
			
			int totalPages = 0;
			
			// Deal with the leaf Nodes Pages
			int i = 0;
			while( i < leaves) {
				buffer.clear();
				nBytesRead = fileChannel.read(buffer);				
				buffer.flip();
				totalPages++;
				int type = buffer.getInt();
				int dataEntries = buffer.getInt();
				leafNode+="LeafNode[\n";
				while(dataEntries > 0) {
					
					int k = buffer.getInt();
					leafNode+="<["+k+":";
					int records = buffer.getInt();
					while(records > 0) {
						leafNode+="("+buffer.getInt()+","+buffer.getInt()+")";
						records--;
					}
					leafNode+="]>\n";
					dataEntries--;
				}
				leafNode+="]\n\n";
				i++;
			}
			
			while(totalPages < rootAddress-1) {
				buffer.clear();
				nBytesRead = fileChannel.read(buffer);				
				buffer.flip();
				totalPages++;
				int type = buffer.getInt();
				int keys = buffer.getInt();
				indexNode+="IndexNode with keys [";
				int counter=0;
				while(counter < keys ) {
					int key = buffer.getInt();
					if(counter == keys-1){
						indexNode += key;
					} else{
						indexNode += key+", ";
					}
					counter++;
				}
				counter=0;
				indexNode+="] and child addresses [";
				while(counter <= keys ) {
					int key = buffer.getInt();
					if(counter == keys){
						indexNode += key;
					} else{
						indexNode += key+", ";
					}					
					counter++;
				}
				indexNode+="]\n\n";
			}
			
			// Read the Root Page
			buffer.clear();
			nBytesRead = fileChannel.read(buffer);				
			buffer.flip();
			int type = buffer.getInt();
			int keys = buffer.getInt();
			rootNode+="IndexNode with keys [";
			int counter=0;
			while(counter < keys ) {
				int key = buffer.getInt();
				if(counter == keys-1){
					rootNode += key;
				}else{
					rootNode += key+", ";
				}
				counter++;
			}
			counter=0;
			rootNode+="] and child addresses [";
			while(counter <= keys ) {
				int key = buffer.getInt();
				if(counter == keys){
					rootNode += key;
				}else {
					rootNode += key+", ";
				}
				counter++;
			}
			
			rootNode+="]";
			fileChannel.close();
			buffer.clear();
			//writer.write(System.getProperty( "line.separator" ));
			writer.write(rootNode);
			writer.write(System.getProperty( "line.separator" ));
			writer.write(indexNode);
			writer.write(leafNode);
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		String homePath = "D:/Database_Practicals/SQL-Interpreter/samples/expected_indexes/";
		buffer = ByteBuffer.allocate(BUFFER_SIZE);
		String file1 = homePath + "Boats.E";
		String file2 = homePath + "Boats.E_myhuman";
		convert(file1, file2);				
			
		/*file1 = homePath + "Sailors.A";
		file2 = homePath + "Sailors.A_human";
		convert(file1, file2);*/
	}

}
