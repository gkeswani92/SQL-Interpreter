package utils;

import java.io.File;

public class DirectoryCleanUp {

	private static void deleteMergeTempFolder(File mergeTempFolder) {
		File[] files = mergeTempFolder.listFiles();
		if (files != null) { 
			for (File file : files) {				
				if (file.isDirectory()) {
					deleteMergeTempFolder(file);
				} else {
					file.delete();
				}
			}
			mergeTempFolder.delete();
		}
	}

	public static void cleanupTempDir() {

		File mergeTempFolder = new File(ConfigFileReader.getInstance().getTempDir());
		//File mergeTempFolder = new File("C:\\Users\\windows\\Downloads\\externalsort");
		if (mergeTempFolder.exists()) {
			deleteMergeTempFolder(mergeTempFolder);
			mergeTempFolder.mkdirs();
		} else {
			mergeTempFolder.mkdirs();
		}

	}	
}
