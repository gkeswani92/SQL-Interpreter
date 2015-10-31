package utils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class TestFramework {
	
	private static String externalOutput = "/Users/gaurav/Documents/Eclipse/SQL-Interpreter/samples/output/External/";
	private static String inMemoryOutput = "/Users/gaurav/Documents/Eclipse/SQL-Interpreter/samples/output/In-Memory/";
	
	public static void main(String args[]){
		File inMemoryDir = new File(inMemoryOutput);
		File externalDir = new File(externalOutput);
		
		File[] inMemoryFileList = inMemoryDir.listFiles();
		File[] externalFileList = externalDir.listFiles();
		
		for(int i=0; i<inMemoryFileList.length; i++){
			
			try {
				byte[] f1 = Files.readAllBytes(inMemoryFileList[i].toPath());
				byte[] f2 = Files.readAllBytes(externalFileList[i].toPath());
				if(Arrays.equals(f1,f2))
					System.out.println("Query "+(i+1)+" works");
				else
					System.out.println("Query "+(i+1)+" does not work");
			} 
			catch (IOException e) {
				e.printStackTrace();
			}	
		}
	}
}
