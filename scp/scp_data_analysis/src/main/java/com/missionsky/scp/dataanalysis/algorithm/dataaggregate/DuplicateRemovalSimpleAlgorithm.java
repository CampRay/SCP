package com.missionsky.scp.dataanalysis.algorithm.dataaggregate;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.glassfish.gmbal.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.missionsky.scp.dataanalysis.Constants;
import com.missionsky.scp.dataanalysis.algorithm.BasicAlgorithm;
import com.missionsky.scp.dataanalysis.entity.StandardTask;

/**
 * Simple Duplicate Removal Algorithm
 * @author ellis.xie 
 * @version 1.0
 */

@Description("Simple Duplicate Removal Algorithm")
public class DuplicateRemovalSimpleAlgorithm extends BasicAlgorithm {
	
	private Logger logger = LoggerFactory.getLogger(DuplicateRemovalSimpleAlgorithm.class);
	
	//Simple Duplicate Removal Mapper
	public static class Map extends Mapper<Object, Text, Text, Text>{
		
		@Override
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException{
			context.write(value, new Text(""));
		}
	};
	
	//Simple Duplicate Removal Reducer
	public static class Reduce extends Reducer<Text, Text, Text, Text>{
		
		@Override
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
			context.write(key, new Text(""));
		}
	}

	@Override
	public int run(Configuration conf, List<String> inputPaths, String output, StandardTask task, String taskName) {
		int result = Constants.ALGORITHM_RESULT_SUCCESS;
		Path jarPath =null;
		
		try {
			String hdfs = getHDFSUrl();
			Path outputPath = new Path(hdfs + output);
			Path input = null; 
			Path outputPathSub = null;
			int i = 0;
			
			for (String in : inputPaths) {
				input = new Path(in);
				outputPathSub = new Path(outputPath, "simple_duplicate_removal_" + i);
				Job job = Job.getInstance(conf, taskName + "_duplicate_removal_simple_job_" + i++);
				
				jarPath = storeTempJarToHDFS(conf, DuplicateRemovalSimpleAlgorithm.class);
				job.addArchiveToClassPath(jarPath);
				job.setJarByClass(DuplicateRemovalSimpleAlgorithm.class);
				job.setMapperClass(Map.class);
				job.setCombinerClass(Reduce.class);
				job.setReducerClass(Reduce.class);
				
				job.setInputFormatClass(TextInputFormat.class);
				job.setOutputFormatClass(TextOutputFormat.class);
				job.setOutputKeyClass(Text.class);
				job.setOutputValueClass(Text.class);
				
				FileInputFormat.addInputPath(job, input);
				FileOutputFormat.setOutputPath(job, outputPathSub);
				
				if (!job.waitForCompletion(true)) {
					throw new InterruptedException(job.getJobName());
				} else {
					if (jarPath != null) {
						deleteTempJarFromHDFS(conf, jarPath);
						jarPath = null;
					}
				}
			}
		} catch (IOException e) {
			//TODO log info
			e.printStackTrace();
			result = Constants.ALGORITHM_RESULT_FAIL;
		} catch (InterruptedException e) {
			logger.error("the job {} unexpected interrupted",e.getMessage());
			result = Constants.ALGORITHM_RESULT_FAIL;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			result = Constants.ALGORITHM_RESULT_FAIL;
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (jarPath != null) {
			deleteTempJarFromHDFS(conf, jarPath);
		}
		
		return result;
	}

}
