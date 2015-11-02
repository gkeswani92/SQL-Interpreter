package utils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestFramework {
	
	private static String expectedOutputDir = "/Users/gaurav/Documents/Eclipse/SQL-Interpreter/samples/output/expected/";
	private static String actualOutputDir = "/Users/gaurav/Documents/Eclipse/SQL-Interpreter/samples/output/actual/";
	
	public static void main(String args[]){
		File expectedFile = new File(expectedOutputDir);
		File actualFile = new File(actualOutputDir);
		
		File[] expectedArray = expectedFile.listFiles();
		File[] actualArray = actualFile.listFiles();
		
		List<File> expected = new ArrayList<File>();
		List<File> actual = new ArrayList<File>();
		
		for(File f: expectedArray){
			if(f.getName().charAt(0) != '.'){
				expected.add(f);
			}
		}
		
		for(File f: actualArray){
			if(f.getName().charAt(0) != '.'){
				actual.add(f);
			}
		}
		
		if(expected.size() == actual.size())
			System.out.println("Exact same length. Things looking good");

		for(int i=0; i<expected.size(); i++){
			try {
				byte[] f1 = Files.readAllBytes(actual.get(i).toPath());
				byte[] f2 = Files.readAllBytes(expected.get(i).toPath());
				if(Arrays.equals(f1,f2))
					System.out.println(actual.get(i).getName()+" works");
				else
					System.out.println(actual.get(i).getName()+ " does not work");
			} 
			catch (IOException e) {
				e.printStackTrace();
			}	
		}
	}
}
