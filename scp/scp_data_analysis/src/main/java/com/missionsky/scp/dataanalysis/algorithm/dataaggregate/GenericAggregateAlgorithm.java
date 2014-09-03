package com.missionsky.scp.dataanalysis.algorithm.dataaggregate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.chain.ChainMapper;
import org.apache.hadoop.mapreduce.lib.chain.ChainReducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.missionsky.scp.dataanalysis.Constants;
import com.missionsky.scp.dataanalysis.entity.AlgorithmInfoAnnotation;
import com.missionsky.scp.dataanalysis.entity.AlgorithmType;
import com.missionsky.scp.dataanalysis.entity.StandardFile;
import com.missionsky.scp.dataanalysis.entity.StandardTask;

/**
 * Generic aggregate algorithm
 * @author ellis.xie 
 * @version 1.0
 */

@AlgorithmInfoAnnotation(name="GenericAggregateAlgorithm", algorithmType=AlgorithmType.BasicAlgothm, description="Generic aggregate algorithm")
public class GenericAggregateAlgorithm extends AggregateAlgorithm {
	
	private static Logger logger = LoggerFactory.getLogger(GenericAggregateAlgorithm.class);
	
	public class Mapper01 extends Mapper<Text, Text, Text, Text>{
		
		@Override
		public void map(Text key,Text value, Context context) throws IOException, InterruptedException{
			context.write(new Text(pattern.split(value.toString())[0]), value);
		}
	}
	
	public class Mapper02 extends Mapper<Text, Iterable<Text>, Text, Text>{
		
		private int aggregateType = Constants.GEN_AGGR_TYPE_FIRST;
		
		@Override
		public void setup(Context context){
			aggregateType = context.getConfiguration().getInt("GEN_AGGR_TYPE", Constants.GEN_AGGR_TYPE_FIRST);
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
				case Constants.GEN_AGGR_TYPE_FIRST:
					
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
					
				case Constants.GEN_AGGR_TYPE_LAST:
					
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
					
				case Constants.GEN_AGGR_TYPE_WEIGHT:
					
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
				for (int i = 1; i < temp.length; i++) {
					result = result + temp[i] + ",";
				}
				
				newKey = new Text(result.substring(0, result.length()-1));
			} else {
				newKey = values.iterator().next();
			}
			
			context.write(newKey, new Text(""));
		}
	}
	
	public class Reducer01 extends TableReducer<Text, Text, NullWritable>{
		
		private byte[] tableNameBytes = null;
		private String[] columnNames = null;
		
		@Override
		public void setup(Context context){
			tableNameBytes = Bytes.toBytes(context.getConfiguration().get("TABLE_NAME"));
			columnNames = context.getConfiguration().getStrings("COLUMN_NAMES");
		}
		
		@Override
		public void reduce(Text key, Iterable<Text> value, Context context) throws IOException, InterruptedException{
			
			Put put = new Put(Bytes.toBytes(UUID.randomUUID().toString()));
			
			String[] values = pattern.split(key.toString());
			
			for (int i = 0; i < columnNames.length; i++) {
				put.add(tableNameBytes, Bytes.toBytes(columnNames[i]), Bytes.toBytes(values[i]));
			}
			
			context.write(NullWritable.get(), put);
		}
	}

	@Override
	public int run(Configuration conf, List<String> inputPaths, String outputPath, StandardTask task, String taskName) {
		int result = Constants.ALGORITHM_RESULT_SUCCESS;
		Path jarPath = null;
		String standardFileName = task.getStandardFile();
		
		if (StringUtils.isNotBlank(standardFileName)) {
			try {
				StandardFile standardFile = new StandardFile(standardFileName);
				
				//Creante HBase table.
				CreateHBaseTable(taskName, standardFile.getColumnNames());
				
				//Data trimer
				Path dataTrimJobIn = null;
				Path dataTrimJobOut = new Path(task.getOutputPath() + "datatrim");
				Path dataTrimJobOutSub = null;
				
				Job dataTrimJob = null;
				
				int i = 1;
				
				for (String path : inputPaths) {
					String confName = task.getConfPathMap().get(path);
					conf.setStrings("STANDARD_KEY_ORDER", standardFile.getStandardKeyOrder(confName));
					conf.setStrings("STANDARD_FIELD_ORDER", standardFile.getStandardFieldOrder(confName));
					
					dataTrimJobIn = new Path(path);
					dataTrimJobOutSub = new Path(dataTrimJobOut, "trim_data_" + i);
					
					dataTrimJob = Job.getInstance(conf, taskName + "_generic_aggregate_data_trim_job_" + i++);
					jarPath = storeTempJarToHDFS(conf, GenericAggregateAlgorithm.class);
					dataTrimJob.addArchiveToClassPath(jarPath);
					dataTrimJob.setJarByClass(AggregateAlgorithm.class);
					dataTrimJob.setMapperClass(DataTrimMapper.class);
					dataTrimJob.setCombinerClass(DataTrimReducer.class);
					dataTrimJob.setReducerClass(DataTrimReducer.class);
					
					dataTrimJob.setInputFormatClass(TextInputFormat.class);
					dataTrimJob.setOutputFormatClass(TextOutputFormat.class);
					dataTrimJob.setMapOutputKeyClass(Text.class);
					dataTrimJob.setMapOutputValueClass(Text.class);
					
					FileInputFormat.setInputPaths(dataTrimJob, dataTrimJobIn);
					FileOutputFormat.setOutputPath(dataTrimJob, dataTrimJobOutSub);
					
					if (!dataTrimJob.waitForCompletion(true)) {
						throw new InterruptedException(dataTrimJob.getJobName());
					} else {
						if (jarPath != null) {
							deleteTempJarFromHDFS(conf, jarPath);
							jarPath = null;
						}
					}
				}
				
				Path dataAggrIn = dataTrimJobOut;
				//TODO data output
				Path dataAggrOut = new Path(task.getOutputPath());
				
				Job aggrJob = Job.getInstance(conf, taskName + "_generic_aggregate_aggregation_job_1");
				jarPath = storeTempJarToHDFS(conf, GenericAggregateAlgorithm.class);
				aggrJob.addArchiveToClassPath(jarPath);
				aggrJob.setJarByClass(GenericAggregateAlgorithm.class);
				aggrJob.setInputFormatClass(TextInputFormat.class);
				aggrJob.setOutputFormatClass(TextOutputFormat.class);
				aggrJob.setOutputKeyClass(Text.class);
				aggrJob.setOutputValueClass(Text.class);
				
				FileInputFormat.setInputPaths(aggrJob, dataAggrIn);
				FileOutputFormat.setOutputPath(aggrJob, dataAggrOut);
				
				Configuration aggrMapper1Conf = new Configuration(false);
				ChainMapper.addMapper(aggrJob, Mapper01.class, Text.class, Text.class, Text.class, Iterable.class, aggrMapper1Conf);
				Configuration aggrMapper2Conf = new Configuration(false);
				ChainMapper.addMapper(aggrJob, Mapper02.class, Text.class, Iterable.class, Text.class, Iterable.class, aggrMapper2Conf);
				Configuration aggrReducer1Conf = new Configuration(false);
				ChainReducer.setReducer(aggrJob, Reducer01.class, Text.class, Iterable.class, NullWritable.class, TableOutputFormat.class, aggrReducer1Conf);
				
				if (!dataTrimJob.waitForCompletion(true)) {
					throw new InterruptedException(aggrJob.getJobName());
				} else {
					if (jarPath != null) {
						deleteTempJarFromHDFS(conf, jarPath);
						jarPath = null;
					}
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
		} else {
			try {
				throw new InterruptedException("Standard file " + task.getStandardFile() + " is not exist.");
			} catch (InterruptedException e) {
				result = Constants.ALGORITHM_RESULT_FAIL;
				logger.error(e.getMessage());
			}
		}
		
		if (jarPath != null) {
			deleteTempJarFromHDFS(conf, jarPath);
		}
		
		return result;
	}
	
	// Create HBase Table
	public void CreateHBaseTable(String tableName, ArrayList<String> columnNames){
		
		try {
			HTableDescriptor table = new HTableDescriptor(TableName.valueOf(tableName));
			
			HColumnDescriptor column = null;
			
			for (String columnName : columnNames) {
				column = new HColumnDescriptor(columnName);
				table.addFamily(column);
			}
			
			Configuration conf = HBaseConfiguration.create();
			HBaseAdmin admin = new HBaseAdmin(conf);
			
			if (admin.tableExists(tableName)) {
				logger.error("Table {} is exist.", tableName);
			} else {
				admin.createTable(table);
			}
			admin.close();
		} catch (MasterNotRunningException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ZooKeeperConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
}
