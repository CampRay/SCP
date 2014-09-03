package com.missionsky.scp.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BasicDao {

	@Autowired
	private HbaseHelper helper;
	
	public List<String> findColumnByRowkey(String rowKey) throws IOException {
		List<String> columns = new ArrayList<String>();
		Result result = helper.selectRowResult("meta", rowKey);
		if(result != null){
			for(Cell cell:result.rawCells()){
				byte[] value = CellUtil.cloneValue(cell);
				if(value != null){
					columns.add(Bytes.toString(value));
				}
				//byte[] qualifier = CellUtil.cloneQualifier(cell);
			}
		}
		return columns;
	}
	
	public List<Result> findDataByTableAndSize(String tableName,Integer pageSize) throws IOException{
		return helper.getAllData(tableName, pageSize);
	}
	
	public Result findMiningResult(String tableName,String rowKey) throws IOException{
		return helper.selectRowResult(tableName, rowKey);
	}
}
