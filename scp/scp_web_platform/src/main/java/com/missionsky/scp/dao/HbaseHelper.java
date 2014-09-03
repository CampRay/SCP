package com.missionsky.scp.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Component;

@Component
public class HbaseHelper {
	private static Configuration config = null;
	
	static {
		config = HBaseConfiguration.create();
	}
	
	/**
	 * create table
	 * @param tableName
	 * @param familys
	 * @throws IOException
	 */
	public void createTable(String tableName,String[] familys) throws IOException{
		HBaseAdmin admin = new HBaseAdmin(config);
		TableName table = TableName.valueOf(tableName);
		if(admin.tableExists(table)){
			System.out.println("table already exists!");
		}else {
			HTableDescriptor tableDesc = new HTableDescriptor(table);
			for(int i=0;i<familys.length;i++){
				tableDesc.addFamily(new HColumnDescriptor(familys[i]));
			}
			admin.createTable(tableDesc);
			System.out.println("create table " + tableName + " ok.");
		}
		admin.close();
	}
	
	/**
	 * drop table
	 * @param tableName
	 * @throws IOException
	 */
	public void deleteTable(String tableName) throws IOException{
		HBaseAdmin admin;
		try {
			admin = new HBaseAdmin(config);
			TableName table = TableName.valueOf(tableName);
			if(admin.tableExists(table)){
				admin.disableTable(table);
				admin.deleteTable(table);
				System.out.println("delete table " + tableName + " success");
			}else{
				System.out.println("table " + tableName + " not exist!");
			}
			admin.close();
		} catch (Exception e) {
			System.out.println("delete table "+tableName+" failure");
			throw e;
		}
	}
	
	/**
	 * add row data
	 * @param tableName
	 * @param rowKey
	 * @param family
	 * @param qualifiers
	 * @param values
	 * @throws IOException
	 */
	public void addRowData(String tableName, String rowKey, String family,
			String[] qualifiers, Map<String, byte[]> map) throws IOException {
		HTable table = new HTable(config, tableName);
		List<Put> puts = new ArrayList<Put>();
		for (int i = 0; i < qualifiers.length; i++) {
			Put put = new Put(Bytes.toBytes(rowKey));
			put.add(Bytes.toBytes(family), Bytes.toBytes(qualifiers[i]),
					map.get(qualifiers[i]));
			puts.add(put);
		}
		table.put(puts);
		System.out.println("insert recored " + rowKey + " to table "
				+ tableName + "ok.");
		table.close();
	}
	
	/**
	 * delete row
	 * @param tableName
	 * @param rowKey
	 * @throws IOException
	 */
	public void deleteRowData(String tableName,String rowKey) throws IOException{
		try{
			HTable table = new HTable(config, tableName);
			Delete delete = new Delete(Bytes.toBytes(rowKey));
			table.delete(delete);
			System.out.println("delete row " + rowKey + " success!");
			table.close();
		}catch(IOException e){
			System.out.println("delete row " + rowKey + " failure!");
			throw e;
		}
	}
	
	/**
	 * select row data, return cell[] need user parse
	 * @param tableName
	 * @param rowKey
	 * @return
	 */
	public Cell[] selectRowData(String tableName,String rowKey){
		Cell[] cells = null;
		try {
			HTable table = new HTable(config, tableName);
			Get get = new Get(Bytes.toBytes(rowKey));
			Result result = table.get(get);
			cells = result.rawCells();
			table.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cells;
	}
	
	public Result selectRowResult(String tableName,String rowKey) throws IOException{
		HTable table = new HTable(config, tableName);
		Get get = new Get(Bytes.toBytes(rowKey));
		Result result = table.get(get);
		table.close();
		return result;
	}
	
	//get data based on one column name
	public List<Result> getResultsByColumnValue(String tableName,String family,String qualifier,byte[] values) throws IOException{
		List<Result> results = new ArrayList<Result>();
		HTable table = new HTable(config, tableName);
		Scan scan = new Scan();
		Filter filter = new SingleColumnValueFilter(Bytes.toBytes(family), Bytes.toBytes(qualifier), CompareOp.EQUAL, values);
		scan.setFilter(filter);
		ResultScanner ss = table.getScanner(scan);
		for(Result rr=ss.next();rr!=null;rr=ss.next()){
			results.add(rr);
		}
		table.close();
		return results;
	}
	
	/**
	 * select all or pageSize result and it also need to parse by user
	 * @param tableName
	 * @param pageSize
	 * @return
	 * @throws IOException 
	 */
	public List<Result> getAllData(String tableName,Integer pageSize) throws IOException{
		List<Result> list = new ArrayList<Result>();
		HTable table = new HTable(config, tableName);
		Scan scan = new Scan();
		if (pageSize != null && pageSize != 0) {
			PageFilter filter = new PageFilter(pageSize);
			scan.setFilter(filter);
		}
		ResultScanner ss = table.getScanner(scan);
		for (Result rr = ss.next(); rr != null; rr = ss.next()) {
			list.add(rr);
		}
		table.close();
		return list;
	}
	
	public List<Result> getRowResults(String tableName,String family,String[] qualifiers,Map<String, byte[]> values,Integer pageSize) throws IOException{
		List<Result> results = new ArrayList<Result>();
		HTable table = new HTable(config, tableName);
		Scan scan = new Scan();
		List<Filter> filters = new ArrayList<Filter>();
		for(String qualifier:qualifiers){
			if(values != null && values.get(qualifier) != null){
				filters.add(new SingleColumnValueFilter(Bytes.toBytes(family), Bytes.toBytes(qualifier), CompareOp.EQUAL, values.get(qualifier)));
			}
		}
		if(pageSize != null){
			PageFilter pageFilter = new PageFilter(pageSize);
			filters.add(pageFilter);
		}
		if(!filters.isEmpty()){
			FilterList filterList = new FilterList(filters);
			scan.setFilter(filterList);
		}
		ResultScanner ss = table.getScanner(scan);
		for(Result rr=ss.next();rr!=null;rr=ss.next()){
			results.add(rr);
		}
		table.close();
		return results;
	}
	
	public List<Cell[]> getRowDatas(String tableName,String family,String[] qualifiers,Map<String, byte[]> values) throws Exception{
		List<Cell[]> list = new ArrayList<Cell[]>();
		HTable table = new HTable(config, tableName);
		Scan scan = new Scan();
		List<Filter> rowFilters = new ArrayList<Filter>();
		for (String qualifier : qualifiers) {
			if (values.get(qualifier) != null) {
				rowFilters.add(new SingleColumnValueFilter(Bytes
						.toBytes(family), Bytes.toBytes(qualifier),
						CompareOp.EQUAL, values.get(qualifier)));
			}
		}
		FilterList filters = new FilterList(rowFilters);
		PageFilter filter = new PageFilter(20);
		filters.addFilter(filter);
		scan.setFilter(filters);
		ResultScanner ss = table.getScanner(scan);
		/*
		 * for(Result rr = ss.next();rr!=null;rr=ss.next()){
		 * System.out.println(rr.getValue(family, qualifier)); }
		 */
		for (Result result : ss) {
			Cell[] cells = result.rawCells();
			list.add(cells);
		}
		table.close();
		return list;
	}
	
	/**
	 * based on the conditions and search out match row then return the rowkeys
	 * @param tableName
	 * @param family
	 * @param qualifiers
	 * @param values
	 * @return
	 * @throws IOException 
	 * @throws Exception
	 */
	public List<String> getRowKeys(String tableName,String family,String[] qualifiers,Map<String, byte[]> values) throws IOException{
		List<String> list = new ArrayList<String>();
		HTable table = new HTable(config, tableName);
		Scan scan = new Scan();
		List<Filter> rowFilters = new ArrayList<Filter>();
		for (String qualifier : qualifiers) {
			if (values.get(qualifier) != null) {
				rowFilters.add(new SingleColumnValueFilter(Bytes
						.toBytes(family), Bytes.toBytes(qualifier),
						CompareOp.EQUAL, values.get(qualifier)));
			}
		}
		FilterList filters = new FilterList(rowFilters);
		scan.setFilter(filters);
		ResultScanner ss = table.getScanner(scan);
		for (Result rr = ss.next(); rr != null; rr = ss.next()) {
			String rowKey = Bytes.toString(rr.getRow());
			list.add(rowKey);
		}
		
		table.close();
		return list;
	}
	
	/**
	 * update row data
	 * @param tableName
	 * @param rowKey
	 * @param family
	 * @param qualifiers
	 * @param values
	 * @throws IOException 
	 */
	public void updateRowData(String tableName, String rowKey, String family,
			String[] qualifiers, Map<String, byte[]> map) throws IOException {
		HTable table = new HTable(config, tableName);
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
	}
	
	/**
	 * get cell timeStamp
	 * @param tableName
	 * @param rowKey
	 * @param family
	 * @param qualifier
	 * @return
	 */
	public long getTimestamp(String tableName,String rowKey,String family,String qualifier){
		long timeStamp = 0;
		try {
			HTable table = new HTable(config, tableName);
			Get get = new Get(Bytes.toBytes(rowKey));
			get.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier));
			Result result = table.get(get);
			table.close();
			for(Cell cell:result.rawCells()){
				timeStamp = cell.getTimestamp();
				return timeStamp;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return timeStamp;
	}
}
