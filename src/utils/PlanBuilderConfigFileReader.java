package utils;

import java.io.BufferedReader;
import java.io.FileReader;

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
	Integer joinBuffer, sortType, sortBuffer;
	private String tempDir;
	private static PlanBuilderConfigFileReader instance;

	private PlanBuilderConfigFileReader() {
		this.joinBuffer = 5;
		this.sortBuffer = 3;
		this.sortType = 1;
		tempDir="";
	}
	
	public static synchronized PlanBuilderConfigFileReader getInstance() {
		if(instance == null){
			instance = new PlanBuilderConfigFileReader();
		}
        return instance;
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
