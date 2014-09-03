package com.missionsky.scp.dataanalysis.algorithm.dataaggregate;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.glassfish.gmbal.Description;

import com.missionsky.scp.dataanalysis.algorithm.BasicAlgorithm;

/**
 * Aggregate Algorithm  abstract class
 * @author ellis.xie 
 * @version 1.0
 */

@Description("Aggregate algorithm  abstract class")
public abstract class AggregateAlgorithm extends BasicAlgorithm{
	
	/**
	 * Data Trim Mapper
	 * @author ellis.xie
	 * @version 1.0
	 */
	public class DataTrimMapper extends Mapper<Object, Text, Text, Text>{
		
		IntWritable[] standardKeyOrder = null;
		IntWritable[]  standardFieldOrder = null;
		
		//Initialization
		@Override
		public void setup(Context context){
			String[] tempKeyOrder = context.getConfiguration().getStrings("STANDARD_KEY_ORDER");
			String[] tempFieldOrder = context.getConfiguration().getStrings("STANDARD_FIELD_ORDER");
			standardKeyOrder = new IntWritable[tempKeyOrder.length];
			standardFieldOrder = new IntWritable[tempFieldOrder.length];
			
			for (int i = 0; i < tempKeyOrder.length; i++) {
				standardKeyOrder[i] = new IntWritable(Integer.parseInt(tempKeyOrder[i]));
			}
			
			for (int i = 0; i < tempFieldOrder.length; i++) {
				String key = tempFieldOrder[i];
				if (StringUtils.isNotBlank(key)) {
					standardKeyOrder[i] = new IntWritable(Integer.parseInt(key));
				} else {
					standardKeyOrder[i] = new IntWritable(-1);
				}
			}
		}
		
		@Override
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException{
			String[] val = value.toString().split(",");
			StringBuffer outputKey = new StringBuffer();
			for (IntWritable keyOrder : standardKeyOrder) {
				outputKey.append(val[keyOrder.get()]).append("_");
			}
			
			outputKey.append(",");
			
			for (IntWritable k : standardFieldOrder) {
				int v = k.get();
				outputKey.append((v != -1)?val[v]:"").append(",");
			}
			
			context.write(new Text(outputKey.toString()), new Text(""));
		}
		
		//Round-off work
		@Override
		public void cleanup(Context context){
			standardKeyOrder = null;
			standardFieldOrder = null;
		}
	}
	
	/**
	 * Data Trim Reducer
	 * @author ellis.xie
	 * @version 1.0
	 */
	public class DataTrimReducer extends Reducer<Text, Text, Text, Text>{
		
		@Override
		public void reduce(Text key, Iterable<Text> value, Context context) throws IOException, InterruptedException{
			context.write(key, new Text(""));
		}
	}
	
	
	

}
