package com.missionsky.scp.dao;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.missionsky.scp.entity.Algorithm;

@Component
public class AlgorithmDao {
	private static final Logger logger = LoggerFactory
			.getLogger(AlgorithmDao.class);
	private static final String TABLE_NAME = "algorithm";
	private static final String FAMILY = "math";
	private static final String[] QUALIFIERS = { "name", "type", "description", "pathName"};

	@Autowired
	private HbaseHelper helper;

	public Algorithm findTaskByRowKey(String rowKey) throws IOException {
		logger.info("find algorithm by rowKey");
		Result result = helper.selectRowResult(TABLE_NAME, rowKey);
		return getAlgorithm(result);
	}

	private Algorithm getAlgorithm(Result result) {
		if (result != null) {
			Algorithm algorithm = new Algorithm();
			algorithm.setRowKey(Bytes.toString(result.getRow()));
			byte[] name = result.getValue(Bytes.toBytes(FAMILY),
					Bytes.toBytes(QUALIFIERS[0]));
			if (name != null) {
				algorithm.setName(Bytes.toString(name));
			}
			byte[] type = result.getValue(Bytes.toBytes(FAMILY),
					Bytes.toBytes(QUALIFIERS[1]));
			if (type != null) {
				algorithm.setType(Bytes.toInt(type));
			}
			byte[] description = result.getValue(Bytes.toBytes(FAMILY),
					Bytes.toBytes(QUALIFIERS[2]));
			if (description != null) {
				algorithm.setDescription(Bytes.toString(description));
			}
			byte[] pathName = result.getValue(Bytes.toBytes(FAMILY),
					Bytes.toBytes(QUALIFIERS[3]));
			if (pathName != null) {
				algorithm.setPathName(Bytes.toString(pathName));
			}
			return algorithm;
		}
		return null;
	}
	
	public String getPathNameByRowKey(String rowKey) throws IOException{
		String pathName = "";
		Result result = helper.selectRowResult(TABLE_NAME, rowKey);
		byte[] obj = result.getValue(Bytes.toBytes(FAMILY),
				Bytes.toBytes(QUALIFIERS[3]));
		if (pathName != null) {
			pathName = Bytes.toString(obj);
		}
		return pathName;
	}
}
