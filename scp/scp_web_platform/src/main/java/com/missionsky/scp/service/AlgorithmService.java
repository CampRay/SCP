package com.missionsky.scp.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.missionsky.scp.dao.HbaseHelper;
import com.missionsky.scp.dao.ParamDao;
import com.missionsky.scp.entity.Algorithm;
import com.missionsky.scp.entity.Parameter;
import com.missionsky.scp.util.UUIDGenerator;

@Component
public class AlgorithmService {

	private static final Logger logger = LoggerFactory
			.getLogger(AlgorithmService.class);
	private static final String TABLE_NAME = "algorithm";
	private static final String FAMILY = "math";
	private static final String[] QUALIFIERS = { "name", "type", "description", "pathName"};

	@Autowired
	private HbaseHelper helper;

	@Autowired
	private ParamDao paramDao;

	/**
	 * 
	 * @param page
	 * @return
	 * @throws IOException
	 */
	public List<Algorithm> findAllAlgorithms(Integer page) throws IOException {
		List<Algorithm> algorithms = new ArrayList<Algorithm>();
		logger.info("select all task records:default pageSize 20");
		List<Result> list = new ArrayList<Result>();
		if (page == null || page == 1) {
			list = helper.getAllData(TABLE_NAME, 20);
		} else {
			list = helper.getAllData(TABLE_NAME, 20 * page);
			if (list != null && !list.isEmpty()) {
				list.removeAll(helper.getAllData(TABLE_NAME, 20 * (page - 1)));
			}
		}
		if (list != null && !list.isEmpty()) {
			for (Result result : list) {
				Algorithm algorithm = new Algorithm();
				byte[] name = result.getValue(Bytes.toBytes(FAMILY),
						Bytes.toBytes(QUALIFIERS[0]));
				algorithm.setName(Bytes.toString(name));
				List<Parameter> params = paramDao.getParamsByAlgorithmId(Bytes
						.toString(result.getRow()));
				algorithm.setParams(params);
				algorithms.add(algorithm);
			}
		}
		return algorithms;
	}

	public List<Algorithm> findByNameAndPage(String name, Integer page)
			throws IOException {
		List<Algorithm> algorithms = new ArrayList<Algorithm>();
		logger.info("select all task records:default pageSize 20");
		List<Result> list = new ArrayList<Result>();
		Map<String, byte[]> values = new HashMap<String, byte[]>();
		if (name != null && !"".equals(name.trim())) {
			values.put(QUALIFIERS[0], Bytes.toBytes(name));
		}
		if (page == null || page == 1) {
			list = helper.getRowResults(TABLE_NAME, FAMILY, QUALIFIERS, values,
					20);
		} else {
			list = helper.getRowResults(TABLE_NAME, FAMILY, QUALIFIERS, values,
					20 * page);
			if (list != null && !list.isEmpty()) {
				list.removeAll(helper.getRowResults(TABLE_NAME, FAMILY,
						QUALIFIERS, values, 20 * (page - 1)));
			}
		}
		if (list != null && !list.isEmpty()) {
			for (Result result : list) {
				Algorithm algorithm = new Algorithm();
				algorithm.setRowKey(Bytes.toString(result.getRow()));
				byte[] algorithmName = result.getValue(Bytes.toBytes(FAMILY),
						Bytes.toBytes(QUALIFIERS[0]));
				if (algorithmName != null) {
					algorithm.setName(Bytes.toString(algorithmName));
				}
				byte[] type = result.getValue(Bytes.toBytes(FAMILY),
						Bytes.toBytes(QUALIFIERS[1]));
				if (type != null) {
					algorithm.setType(Bytes.toInt(type));
				}
				byte[] description = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[2]));
				if(description != null){
					algorithm.setDescription(Bytes.toString(description));
				}
				List<Parameter> params = paramDao.getParamsByAlgorithmId(Bytes
						.toString(result.getRow()));
				algorithm.setParams(params);
				algorithms.add(algorithm);
			}
		}
		return algorithms;
	}

	public Algorithm findAlgorithm(String rowKey) throws IOException {
		logger.info("find algorithm msg by rowKey");
		Algorithm algorithm = null;
		Result result = helper.selectRowResult(TABLE_NAME, rowKey);
		if (result != null) {
			algorithm = new Algorithm();
			algorithm.setRowKey(rowKey);
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
			if(pathName != null){
				algorithm.setPathName(Bytes.toString(pathName));
			}
			List<Parameter> params = paramDao.getParamsByAlgorithmId(rowKey);
			algorithm.setParams(params);
		}
		return algorithm;
	}

	public Map<String, Object> saveAlgorithm(Algorithm algorithm,
			Map<String, Object> ret) throws IOException {
		logger.info("save algorithm into hbase");
		if (algorithm != null) {
			List<Result> results = helper.getResultsByColumnValue(TABLE_NAME,
					FAMILY, QUALIFIERS[0], Bytes.toBytes(algorithm.getName()));
			String rowkey = algorithm.getRowKey();
			for (Result result : results) {
				byte[] rowKey = result.getRow();
				if (rowkey == null || !rowkey.equals(Bytes.toString(rowKey))) {
					ret.put("msg", "exist");
					return ret;
				}
			}
			if (rowkey == null || "".equals(rowkey.trim())) {
				rowkey = UUIDGenerator.getUUID();
			} else {
				// delete history params
				List<String> rowKeys = paramDao.getrowkeys(rowkey);
				paramDao.deleteParams(rowKeys);
			}
			Map<String, byte[]> map = new HashMap<String, byte[]>();
			map.put(QUALIFIERS[0], Bytes.toBytes(algorithm.getName()));
			map.put(QUALIFIERS[1], Bytes.toBytes(algorithm.getType()));
			map.put(QUALIFIERS[2], Bytes.toBytes(algorithm.getDescription()));
			map.put(QUALIFIERS[3], Bytes.toBytes(algorithm.getPathName()));
			helper.updateRowData(TABLE_NAME, rowkey, FAMILY, QUALIFIERS, map);
			List<Parameter> params = algorithm.getParams();
			if (params != null && !params.isEmpty()) {
				paramDao.saveParams(params, rowkey);
			}
			ret.put("msg", "success");
		} else {
			ret.put("msg", "empty");
		}
		return ret;
	}

	public void deleteTask(String rowKey) throws IOException {
		if (rowKey != null && !"".equals(rowKey.trim())) {
			helper.deleteRowData(TABLE_NAME, rowKey);
			logger.info("delete row which rowKey is" + rowKey + " success!");
		}
	}

	public Algorithm cellToAlgorithm(Cell[] cells) {
		Algorithm algorithm = null;
		if (cells != null && cells.length > 0) {
			algorithm = new Algorithm();
			String rowKey = Bytes.toString(CellUtil.cloneRow(cells[0]));
			algorithm.setRowKey(rowKey);
			for (int i = 0; i < cells.length; i++) {
				switch (i) {
				case 0:
					algorithm.setName(Bytes.toString(CellUtil
							.cloneValue(cells[i])));
					break;
				default:
					break;
				}
			}
		}
		return algorithm;
	}

	public List<Algorithm> findAllAlgorithmsByType(Integer type)
			throws IOException {
		List<Algorithm> algorithms = new ArrayList<Algorithm>();
		Map<String, byte[]> values = new HashMap<String, byte[]>();
		if (type != null) {
			values.put(QUALIFIERS[1], Bytes.toBytes(type));
		}
		List<Result> list = helper.getRowResults(TABLE_NAME, FAMILY,
				QUALIFIERS, values, null);
		if (list != null && !list.isEmpty()) {
			for (Result result : list) {
				Algorithm algorithm = new Algorithm();
				algorithm.setRowKey(Bytes.toString(result.getRow()));
				byte[] name = result.getValue(Bytes.toBytes(FAMILY),
						Bytes.toBytes(QUALIFIERS[0]));
				if (name != null) {
					algorithm.setName(Bytes.toString(name));
				}
				byte[] description = result.getValue(Bytes.toBytes(FAMILY),
						Bytes.toBytes(QUALIFIERS[2]));
				if (description != null) {
					algorithm.setDescription(Bytes.toString(description));
				}
				algorithms.add(algorithm);
			}
		}
		return algorithms;
	}
}
