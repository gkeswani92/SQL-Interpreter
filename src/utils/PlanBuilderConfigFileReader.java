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
public class PlanBuilderConfigFileReader {
	
	FileReader configFileReaderObj;
	BufferedReader file;
	Integer joinType, joinBuffer, sortType, sortBuffer, isUseIndex;
	private String tempDir;
	private static PlanBuilderConfigFileReader instance;

	private PlanBuilderConfigFileReader() {
		this.joinBuffer = -1;
		this.joinType = -1;
		this.sortBuffer = -1;
		this.sortType = -1;
		this.isUseIndex = -1;
		tempDir="";
	}
	
	public static synchronized PlanBuilderConfigFileReader getInstance() {
		if(instance == null){
			instance = new PlanBuilderConfigFileReader();
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
			
			// Get the third line from the file and read whether the index should be used
			String indexLine = file.readLine();
			parts = indexLine.split(" ");
			this.isUseIndex = Integer.parseInt(parts[0]);
			
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
		this.tempDir = tempDir;
	}
	
	public String getTempDir(){
		return tempDir;
	}
}
