package com.missionsky.scp.dataanalysis.algorithm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.missionsky.scp.dataanalysis.entity.AlgorithmInfoAnnotation;
import com.missionsky.scp.dataanalysis.entity.AlgorithmType;
import com.missionsky.scp.dataanalysis.entity.StandardTask;
import com.missionsky.scp.dataanalysis.utils.PropertiesUtil;
import com.missionsky.scp.dataanalysis.utils.RemoteHadoopUtil;

/**
 * Basic algorithm abstract class
 * @author ellis.xie 
 * @version 1.0
 */

@AlgorithmInfoAnnotation(name="BasicAlgorithm", algorithmType=AlgorithmType.BasicAlgothm, description="Basic algorithm abstract class.")
public abstract class BasicAlgorithm implements Algorithm {
	
	private static Logger logger = LoggerFactory.getLogger(BasicAlgorithm.class);
	
	public Pattern pattern = Pattern.compile(",");
	
	public int run(Configuration conf, String output, String standardFileName, StandardTask task){
		
		ArrayList<String> inputpaths = new ArrayList<String>();
		try {
			FileSystem fs = FileSystem.get(conf);
			Path standardPath = new Path(getHDFSUrl() + "/scp/data-adapter/" + (standardFileName.endsWith(".xml")?standardFileName.replace(".xml", ""):standardFileName));			
			FileStatus[] fileStatus = fs.listStatus(standardPath);
			for (FileStatus fStatus : fileStatus) {
				inputpaths.add(fStatus.getPath().toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return run(conf, inputpaths, output, task, task.getName());
	}
	
	public abstract int run(Configuration conf, List<String> inputPaths, String outputPath, StandardTask task, String taskName);
	
	/**
	 * Create a jar of current running job and store the jar to temp index in hdfs
	 * @author ellis.xie 
	 * @version 1.0
	 */
	public static Path storeTempJarToHDFS(Configuration conf, Class<?> AlgorithmClazz){
		String jarPath = null;
		String hdfs = getHDFSUrl();
		
		try {
			File jarFile = RemoteHadoopUtil.createJar(AlgorithmClazz); 
			FileSystem fs = FileSystem.get(conf);
			Path src = new Path(jarFile.getPath());
			Path dst = new Path(hdfs + "/tmp/");
			fs.copyFromLocalFile(src, dst);
			fs.close();
			jarPath = hdfs + "/tmp/" + jarFile.getName();
			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new Path(jarPath);
	}
	
	public static void deleteTempJarFromHDFS(Configuration conf, Path jarPath){
		try {
			FileSystem fs = FileSystem.get(conf);
			fs.delete(jarPath, true);
		} catch (IOException e) {
			logger.error("Delete temp jar {} fail.", jarPath.toString());
		}
	}
	
	
	public static String getHDFSUrl(){
		return PropertiesUtil.loadPropertiesFile(BasicAlgorithm.class.getResource("/").getPath() + "hadoop.properties").getProperty("hdfs.url");
	} 
	
	
}
