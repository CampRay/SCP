package com.missionsky.scp.dataanalysis.facadeinterface;

import java.util.ArrayList;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.missionsky.scp.dataanalysis.Constants;
import com.missionsky.scp.dataanalysis.algorithm.BasicAlgorithm;
import com.missionsky.scp.dataanalysis.algorithm.dataaggregate.StoreToHBaseAlgorithm;
import com.missionsky.scp.dataanalysis.entity.StandardFile;
import com.missionsky.scp.dataanalysis.entity.StandardTask;
import com.missionsky.scp.dataanalysis.utils.HbaseUtil;
import com.missionsky.scp.dataanalysis.utils.PropertiesUtil;
import com.missionsky.scp.dataanalysis.utils.RemoteHadoopUtil;

/**
 * Task Assembly Line
 * @author ellis.xie 
 * @version 1.0
 */

public class BasicTaskAssemblyLine {
	
	private Logger logger = LoggerFactory.getLogger(BasicTaskAssemblyLine.class);
	
	public void stream(StandardTask task){
		
		RemoteHadoopUtil.setConf(BasicTaskAssemblyLine.class, Thread.currentThread(), "/hadoop");
		
		Boolean firstStep = true;
		Configuration conf = new Configuration();
		ArrayList<String> tempIntput = new ArrayList<>();
		BasicAlgorithm algorithm = null;
		String algorithmTaskOutput = null;
		int algorithmResult = Constants.ALGORITHM_RESULT_SUCCESS;
		String taskName = task.getName();
		
		try {
			StandardFile standardFile = new StandardFile(task.getStandardFile());
			
			for (String algorithmName : task.getAligorithms()) {
				algorithm = (BasicAlgorithm) Class.forName(algorithmName).newInstance();
				algorithmTaskOutput = "scp/data-analysis/basic/" + taskName + "/" + algorithm.getClass().getSimpleName();
				if (firstStep) {
					conf.set("mapreduce.jobtracker.address", getJobTracker());
					algorithmResult = algorithm.run(conf,algorithmTaskOutput, standardFile.getName(), task);
					
					if (algorithmResult == Constants.ALGORITHM_RESULT_SUCCESS) {
						tempIntput.add(algorithmTaskOutput);
					} else {
						throw new InterruptedException("the algorithm "+ algorithmName +" in the task " + taskName + " fail");
					}
				} else {
					conf.clear();
					conf.set("mapreduce.jobtracker.address", getJobTracker());
					algorithmResult = algorithm.run(conf, tempIntput, algorithmTaskOutput, task, taskName);
					
					if (algorithmResult == Constants.ALGORITHM_RESULT_SUCCESS) {
						tempIntput.clear();
						tempIntput.add(algorithmTaskOutput);
					} else {
						throw new InterruptedException("the algorithm "+ algorithmName +" in the task " + taskName + " fail.");
					}
				}
			}
		
			// store parsed data to HBase.
			HbaseUtil.CreateHBaseTable(taskName, standardFile.getColumnNames());
			algorithm = new StoreToHBaseAlgorithm();
			conf = HBaseConfiguration.create();
			conf.set("mapreduce.jobtracker.address", getJobTracker());
			algorithmResult = algorithm.run(conf, tempIntput, null, task, taskName);
			if (algorithmResult == Constants.ALGORITHM_RESULT_FAIL) {
				throw new InterruptedException("the algorithm StoreToHBaseAlgorithm in the task " + taskName + " fail.");
			}
			
		} catch (ClassNotFoundException e) {
			logger.error("Unexpected interrupted:{}",e.getMessage());
		} catch (InstantiationException e) {
			logger.error("Unexpected interrupted:{}",e.getMessage());
		} catch (IllegalAccessException e) {
			logger.error("Unexpected interrupted:{}",e.getMessage());
		} catch (InterruptedException e) {
			logger.error("Unexpected interrupted:{}",e.getMessage());
		}
	}
	
	public String getJobTracker(){
		Properties properties = PropertiesUtil.loadPropertiesFile(BasicTaskAssemblyLine.class.getResource("/").getPath() + "/hadoop.properties");
		return (String) properties.get("mapreduce.jobtracker.address");
	}

}
