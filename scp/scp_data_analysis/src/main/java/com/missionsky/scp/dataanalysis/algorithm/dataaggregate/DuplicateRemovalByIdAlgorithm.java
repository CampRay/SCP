package com.missionsky.scp.dataanalysis.algorithm.dataaggregate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.chain.ChainMapper;
import org.apache.hadoop.mapreduce.lib.chain.ChainReducer;
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
 * Duplicate Removal By ID Algorithm
 * @author ellis.xie
 * @version 1.0
 */

@Description("Duplicate Removal By ID Algorithm")
public class DuplicateRemovalByIdAlgorithm extends BasicAlgorithm{
	
	private static Logger logger = LoggerFactory.getLogger(DuplicateRemovalByIdAlgorithm.class);
	
	//Mapper1
	public class Mapper01 extends Mapper<Text, Text, Text, Text>{
		
		@Override
		public void map(Text key, Text value, Context context) throws IOException, InterruptedException{
			context.write(new Text(pattern.split(value.toString())[0]), value);
		}
	};
	
	//Mapper2
	public class Mapper02 extends Mapper<Text, Iterable<Text>, Text, Text>{
		
		private int aggregateType = Constants.DUPL_REMOV_AGGR_TYPE_FIRST;
		
		@Override
		public void setup(Context context){
			aggregateType = context.getConfiguration().getInt("DUPL_REMOV_AGGR_TYPE", Constants.DUPL_REMOV_AGGR_TYPE_FIRST);
		}
		
		
		@Override
		public void map(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
			Integer size = 0;
			Text newKey = null;
			
			for (@SuppressWarnings("unused") Text text : values) {
				size++;
			}
			
			if (size > 1) {
				String[] temp = null;
				ArrayList<String []> matrix = new ArrayList<>();
				for (Text text : values) {
					String[] row = pattern.split(text.toString());
					matrix.add(row);
				}
				
				
				Iterator<String[]> matrixIterator = matrix.iterator();
				
				
				switch (aggregateType) {
				case Constants.DUPL_REMOV_AGGR_TYPE_FIRST:
					
					temp = matrixIterator.next();
					while (matrixIterator.hasNext()) {
						String[] nextData = matrixIterator.next();
						for (int i = 0; i < temp.length; i++) {
							if (StringUtils.isBlank(temp[i]) && StringUtils.isNotBlank(nextData[i])) {
								temp[i] = nextData[i];
							}
						}
					}
					
					break;
					
				case Constants.DUPL_REMOV_AGGR_TYPE_LAST:
					
					temp = matrixIterator.next();
					while (matrixIterator.hasNext()) {
						String[] nextData = matrixIterator.next();
						for (int i = 0; i < temp.length; i++) {
							if (StringUtils.isNotBlank(nextData[i])) {
								temp[i] = nextData[i];
							}
						}
					}
					
					break;
					
				case Constants.DUPL_REMOV_AGGR_TYPE_WEIGHT:
					
					String grid = null;
					Integer val = null;
					Integer matrixWidth = matrix.get(0).length;
					HashMap<String, Integer> weightMap = new HashMap<String, Integer>();
					ArrayList<HashMap<String, Integer>> weightMapList = new ArrayList<HashMap<String, Integer>>();
					
					for (int i = 0; i < matrixWidth; i++) {
						weightMapList.add(new HashMap<String, Integer>());
					}
					
					for (int i = 0; i < matrixWidth; i++) {
						for (String[] row : matrix) {
							grid = row[i];
							if (StringUtils.isNotBlank(grid)) {
								val = weightMapList.get(i).get(grid);
								if (val != null) {
									weightMapList.get(i).put(grid, val + 1);
								} else {
									weightMapList.get(i).put(grid, 1);
								}
							} else {
								val = weightMapList.get(i).get("");
								if (val != null) {
									weightMapList.get(i).put("", val + 1);
								} else {
									weightMapList.get(i).put("", 1);
								}
							}
						}
					}
					
					temp = new String[matrixWidth];
					
					for (int i = 0; i < matrixWidth; i++) {
						weightMap = weightMapList.get(i);
						String maxKey = null;
						for (String weightKey : weightMap.keySet()) {
							if (maxKey == null) {
								maxKey = weightKey;
							} else if (weightMap.get(weightKey) > weightMap.get(maxKey)) {
								maxKey = weightKey;
							}
						}
						temp[i] = maxKey;
					}
					
					break;
					
				default:
					
					break;
					
				}
				
				String result = "";
				for (String re : temp) {
					result = result + re + ",";
				}
				
				newKey = new Text(result.substring(0, result.length()-1));
			} else {
				newKey = values.iterator().next();
			}
			
			context.write(newKey, new Text(""));
		}
	}
	
	//Reducer
	public class Reducer01 extends Reducer<Text, Text, Text, Text>{
		
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
				input = new Path(hdfs + in);
				outputPathSub = new Path(outputPath, "_duplicate_removal_by_id_job_" + i);
				Job job = Job.getInstance(conf, taskName + "_duplicate_removal_by_id_job_" + i++);
				
				jarPath = storeTempJarToHDFS(conf, DuplicateRemovalByIdAlgorithm.class);
				job.addArchiveToClassPath(jarPath);
				job.setJarByClass(DuplicateRemovalByIdAlgorithm.class);
				job.setInputFormatClass(TextInputFormat.class);
				job.setOutputFormatClass(TextOutputFormat.class);
				job.setOutputKeyClass(Text.class);
				job.setOutputValueClass(Text.class);
				
				FileInputFormat.setInputPaths(job, input);
				FileOutputFormat.setOutputPath(job, outputPathSub);
				
				Configuration mapper1Conf = new Configuration(false);
				ChainMapper.addMapper(job, Mapper01.class, Text.class, Text.class, Text.class, Iterable.class, mapper1Conf);
				Configuration mapper2Conf = new Configuration(false);
				ChainMapper.addMapper(job, Mapper02.class, Text.class, Iterable.class, Text.class, Iterable.class, mapper2Conf);
				Configuration reducerConf = new Configuration(false);
				ChainReducer.setReducer(job, Reducer01.class, Text.class, Iterable.class, Text.class, Text.class, reducerConf);
				
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
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = Constants.ALGORITHM_RESULT_FAIL;
		} catch (InterruptedException e) {
			logger.error("the job {} unexpected interrupted",e.getMessage());
			result = Constants.ALGORITHM_RESULT_FAIL;
		}
		
		if (jarPath != null) {
			deleteTempJarFromHDFS(conf, jarPath);
			jarPath = null;
		}
		
		return result;
	}

}
