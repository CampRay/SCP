package com.missionsky.scp.dataanalysis.algorithm.dataaggregate;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import com.missionsky.scp.dataanalysis.entity.StandardTask;

/**
 * Replicated joins aggregate algorithm
 * @author ellis.xie
 * @version 1.0
 */

@Deprecated
public class ReplicatedJoinAggregateAlgorithm extends AggregateAlgorithm {
	
	public class Mapper01 extends Mapper<Text, Text, Text, Text>{
		
		@Override
		public void map(Text key,Text value, Context context) throws IOException, InterruptedException{
			
		}
		
	}
	
	public class Reducer01 extends Reducer<Text, Text, Text, Text>{
		
		@Override
		public void reduce(Text key, Iterable<Text> value, Context context) throws IOException, InterruptedException{
			
		}
		
	}

	@Override
	public int run(Configuration conf, List<String> inputPaths, String outputPath, StandardTask task, String taskName) {
		// TODO Auto-generated method stub
		return 0;
	}
}
