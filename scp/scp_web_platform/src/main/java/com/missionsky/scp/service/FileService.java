package com.missionsky.scp.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.missionsky.scp.dao.HbaseHelper;
import com.missionsky.scp.entity.StandardFile;
import com.missionsky.scp.util.UUIDGenerator;

@Component
public class FileService {
	private Logger logger = LoggerFactory.getLogger(FileService.class);
	private static final String TABLE_NAME = "standardFile";
	private static final String FAMILY = "file";
	private static final String[] QUALIFIERS = { "fileName","content","description"};
	
	@Autowired
	private HbaseHelper helper;
	
	public Map<String, String> getAllFiles() throws IOException{
		List<Result> list = helper.getRowResults(TABLE_NAME, FAMILY, QUALIFIERS, null, null);
		Map<String, String> map = new HashMap<String,String>();
		if(list != null && !list.isEmpty()){
			for(Result result:list){
				byte[] rowKey = result.getRow();
				byte[] fileName = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[0]));
				if(rowKey != null && fileName != null){
					map.put(Bytes.toString(rowKey), Bytes.toString(fileName));
				}
			}
		}
		return map;
	}
	
	public StandardFile getFileByRowKey(String rowKey) throws IOException{
		Result result = helper.selectRowResult(TABLE_NAME, rowKey);
		if(result != null){
			StandardFile file = new StandardFile();
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
			file.setRowKey(Bytes.toString(result.getRow()));
			return file;
		}
		return null;
	}
	
	public List<StandardFile> findAllFiles(String fileName) throws IOException{
		List<StandardFile> files = new ArrayList<StandardFile>();
		List<Result> list = new ArrayList<Result>();
		Map<String, byte[]> values = new HashMap<String, byte[]>();
		if(fileName != null && !"".equals(fileName.trim())){
			values.put(QUALIFIERS[0], Bytes.toBytes(fileName));
		}
		list = helper.getRowResults(TABLE_NAME, FAMILY, QUALIFIERS, values, null);
		if(list != null && !list.isEmpty()){
			for(Result result:list){
				StandardFile file = new StandardFile();
				byte[] name = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[0]));
				if(name != null){
					file.setName(Bytes.toString(name));
				}
				byte[] description = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[2]));
				if(description != null){
					file.setDescription(Bytes.toString(description));
				}
				file.setRowKey(Bytes.toString(result.getRow()));
				files.add(file);
			}
		}
		return files;
	}
	
	public Map<String, Object> saveStandardFile(Map<String, Object> map,StandardFile file) throws IOException{
		if(file != null){
			Map<String, byte[]> map2 = new HashMap<String, byte[]>();
			if(file.getName() != null && !"".equals(file.getName())){
				map2.put(QUALIFIERS[0], Bytes.toBytes(file.getName()));
			}
			if(file.getContent() != null && !"".equals(file.getContent())){
				map2.put(QUALIFIERS[1], Bytes.toBytes(file.getContent()));
			}
			if(file.getDescription() != null && !"".equals(file.getDescription())){
				map2.put(QUALIFIERS[2], Bytes.toBytes(file.getDescription()));
			}
			if(!map2.isEmpty()){
				helper.updateRowData(TABLE_NAME, UUIDGenerator.getUUID(), FAMILY, QUALIFIERS, map2);
				map.put("msg", "success");
			}else{
				map.put("msg", "empty");
			}
		}else{
			map.put("msg", "empty");
		}
		return map;
	}

	public void deleteFile(String rowKey) throws IOException {
		helper.deleteRowData(TABLE_NAME, rowKey);
		logger.info("delete row which rowKey is" + rowKey + " success!");
	}
}
