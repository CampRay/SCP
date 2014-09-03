package com.missionsky.scp.dataadapter.common;

import java.util.HashMap;

import com.missionsky.scp.dataadapter.entity.DataSource;

/**
 * @author Ellis Xie
 * @version 1.0
 * 单例 
 */
public class SingleInstance {
	
	private static String filePath;
	
	private static HashMap<String, DataSource> dataSources;
	
	private static HashMap<String, String> standardFiles;
	
	private static HashMap<String, String> configFiles;

	public static String getFilePath() {
		if (filePath == null) {
			filePath = "";
		}
		return filePath;
	}

	public static void setFilePath(String filePath) {
		SingleInstance.filePath = filePath;
	}

	public static HashMap<String, DataSource> getDataSources() {
		if (dataSources == null) {
			dataSources = new HashMap<String, DataSource>();
		}
		return dataSources;
	}

	public static void setDataSources(HashMap<String, DataSource> dataSources) {
		SingleInstance.dataSources = dataSources;
	}

	public static HashMap<String, String> getStandardFiles() {
		if (standardFiles == null) {
			standardFiles = new HashMap<String, String>();
		}
		return standardFiles;
	}

	public static void setStandardFiles(HashMap<String, String> standardFiles) {
		SingleInstance.standardFiles = standardFiles;
	}

	public static HashMap<String, String> getConfigFiles() {
		if (configFiles == null) {
			configFiles = new HashMap<String, String>();
		}
		return configFiles;
	}

	public static void setConfigFiles(HashMap<String, String> configFiles) {
		SingleInstance.configFiles = configFiles;
	}
	
	

}
