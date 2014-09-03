package com.missionsky.scp.dataanalysis.entity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Standard task entity class
 * @author ellis.xie
 * @version 1.0
 */

public class StandardTask {
	
	// task name
	private String name;
	
	// algorithm list
	private ArrayList<String> aligorithms;
	
	// jobs input path in hdfs
	private ArrayList<String> inputPaths;
	
	// task output path
	private String outputPath;
	
	// standard file name
	private String standardFile;
	
	// configuration file name map(key:intput path,value:configuration file name)
	private HashMap<String, String> confPathMap;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<String> getAligorithms() {
		return aligorithms;
	}

	public void setAligorithms(ArrayList<String> aligorithms) {
		this.aligorithms = aligorithms;
	}

	public ArrayList<String> getInputPaths() {
		return inputPaths;
	}

	public void setInputPaths(ArrayList<String> inputPaths) {
		this.inputPaths = inputPaths;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public String getStandardFile() {
		return standardFile;
	}

	public void setStandardFile(String standardFile) {
		this.standardFile = standardFile;
	}

	public HashMap<String, String> getConfPathMap() {
		return confPathMap;
	}

	public void setConfPathMap(HashMap<String, String> confPathMap) {
		this.confPathMap = confPathMap;
	}
	
}
