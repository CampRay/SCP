package com.missionsky.scp.dataanalysis.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HbaseUtil {
	private static Configuration config = null;
	
	private static Logger logger = LoggerFactory.getLogger(HbaseUtil.class);

	static {
		config = HBaseConfiguration.create();
	}

	/**
	 * update row data
	 * 
	 * @param tableName
	 * @param rowKey
	 * @param family
	 * @param qualifiers
	 * @param values
	 * @throws IOException
	 */
	public static void updateRowData(String tableName, String rowKey,
			String family, String[] qualifiers, Map<String, byte[]> map)
			throws IOException {
		//Create a connection to the cluster
		HConnection connection = HConnectionManager.createConnection(config);
		//use table as needed, the table returned is lightweight
		HTableInterface table = connection.getTable(tableName);
		List<Put> puts = new ArrayList<Put>();
		for (int i = 0; i < qualifiers.length; i++) {
			Put put = new Put(Bytes.toBytes(rowKey));
			long timeStamp = getTimestamp(tableName, rowKey, family,
					qualifiers[i]);
			if (timeStamp != 0) {
				put.add(Bytes.toBytes(family), Bytes.toBytes(qualifiers[i]),
						timeStamp, map.get(qualifiers[i]));
			} else {
				put.add(Bytes.toBytes(family), Bytes.toBytes(qualifiers[i]),
						map.get(qualifiers[i]));
			}
			puts.add(put);
		}
		table.put(puts);
		table.close();
		// use the connection for other access to the cluster
		connection.close();
	}
	
	public static Result selectRowResult(String tableName,String rowKey) throws IOException{
		HConnection connection = HConnectionManager.createConnection(config);
		HTableInterface table = connection.getTable(tableName);
		Get get = new Get(Bytes.toBytes(rowKey));
		Result result = table.get(get);
		table.close();
		connection.close();
		return result;
	}

	/**
	 * get cell timeStamp
	 * 
	 * @param tableName
	 * @param rowKey
	 * @param family
	 * @param qualifier
	 * @return
	 */
	public static long getTimestamp(String tableName, String rowKey, String family,
			String qualifier) {
		long timeStamp = 0;
		try {
			HConnection connection = HConnectionManager.createConnection(config);
			HTableInterface table = connection.getTable(tableName);
			Get get = new Get(Bytes.toBytes(rowKey));
			get.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier));
			Result result = table.get(get);
			table.close();
			connection.close();
			for (Cell cell : result.rawCells()) {
				timeStamp = cell.getTimestamp();
				return timeStamp;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return timeStamp;
	}
	
	/**
	 * delete row
	 * @param tableName
	 * @param rowKey
	 * @throws IOException
	 */
	public static void deleteRowData(String tableName,String rowKey) throws IOException{
		try{
			HConnection connection = HConnectionManager.createConnection(config);
			HTableInterface table = connection.getTable(tableName);
			Delete delete = new Delete(Bytes.toBytes(rowKey));
			table.delete(delete);
			table.close();
			connection.close();
		}catch(IOException e){
			throw e;
		}
	}
	
	// Create HBase Table
	public static void CreateHBaseTable(String tableName, ArrayList<String> columnNames){
		
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
