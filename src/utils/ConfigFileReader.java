package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Util singleton class to read the config file and store the join type, sort type
 * and buffer size.
 * The properties are used to create a suitable physical plan for the query.
 * @author tanvimehta
 *
 */
public class ConfigFileReader {
	
	FileReader configFileReaderObj;
	BufferedReader file;
	Integer joinType, joinBuffer, sortType, sortBuffer;
	private String mergeTempDir;
	private static ConfigFileReader instance;

	private ConfigFileReader() {
		this.joinBuffer = -1;
		this.joinType = -1;
		this.sortBuffer = -1;
		this.sortType = -1;
		mergeTempDir="";
	}
	
	public static synchronized ConfigFileReader getInstance() {
		if(instance == null){
			instance = new ConfigFileReader();
		}
        return instance;
	}	
	
	public void readConfigFile(String inputsrcDir) {
		try {
			configFileReaderObj = new FileReader(inputsrcDir + "/plan_builder_config.txt");
			file = new BufferedReader(configFileReaderObj);
			
			// Get the first line from the file and read the join config params
			String joinLine = file.readLine();
			String[] parts = joinLine.split(" ");
			if (parts.length > 1) {
				this.joinBuffer = Integer.parseInt(parts[1]);
			}
			this.joinType = Integer.parseInt(parts[0]);
			
			// Get the second line from the file and read the sort config params
			String sortLine = file.readLine();
			parts = sortLine.split(" ");
			if (parts.length > 1) {
				this.sortBuffer = Integer.parseInt(parts[1]);
			}
			this.sortType = Integer.parseInt(parts[0]);
			
		} catch (IOException ie) {
			ie.printStackTrace();
		}

	}

	public Integer getJoinType() {
		return joinType;
	}

	public Integer getJoinBuffer() {
		return joinBuffer;
	}

	public Integer getSortType() {
		return sortType;
	}

	public Integer getSortBuffer() {
		return sortBuffer;
	}
	
	public void setTempDir(String tempDir){
		this.mergeTempDir = tempDir;
	}
	
	public String getTempDir(){
		return mergeTempDir;
	}
}
