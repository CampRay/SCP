package com.missionsky.scp.dao;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import com.missionsky.scp.dataanalysis.utils.HbaseUtil;
import com.missionsky.scp.entity.StandardFile;

public class StandardFileDao {
	private static final String TABLE_NAME = "standardFile";
	private static final String FAMILY = "file";
	private static final String[] QUALIFIERS = { "fileName","content","description"};
	
	public static StandardFile getFileByRowKey(String rowKey) throws IOException{
		Result result = HbaseUtil.selectRowResult(TABLE_NAME, rowKey);
		if(result != null){
			return transToFile(result);
		}
		return null;
	}
	
	public static StandardFile transToFile(Result result){
		if(result != null){
			StandardFile file = new StandardFile();
			file.setRowKey(Bytes.toString(result.getRow()));
			byte[] name = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[0]));
			if(name != null){
				file.setName(Bytes.toString(name));
			}
			byte[] content = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[1]));
			if(content != null){
				file.setContent(Bytes.toString(content));
			}
			byte[] description = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[2]));
			if(description != null){
				file.setDescription(Bytes.toString(description));
			}
			return file;
		}
		return null;
	}
}
