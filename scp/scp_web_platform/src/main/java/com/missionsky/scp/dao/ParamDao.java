package com.missionsky.scp.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.missionsky.scp.entity.Parameter;
import com.missionsky.scp.util.UUIDGenerator;

@Component
public class ParamDao {
	
	private static final String TABLE_NAME = "parameter";
	private static final String FAMILY = "param";
	private static final String[] QUALIFIERS = {"algorithmId","name","type","description"};
	
	@Autowired
	private HbaseHelper helper;
	
	public List<String> getrowkeys(String algorithmId) throws IOException{
		Map<String, byte[]> values = new HashMap<String, byte[]>();
		values.put(QUALIFIERS[0], Bytes.toBytes(algorithmId));
		return helper.getRowKeys(TABLE_NAME, FAMILY, QUALIFIERS, values);
	}

	public void deleteParams(List<String> rowKeys) throws IOException {
		if(rowKeys != null && !rowKeys.isEmpty()){
			for(String rowKey:rowKeys){
				helper.deleteRowData(TABLE_NAME, rowKey);
			}
		}
	}

	public void saveParams(List<Parameter> params, String algorithmId) throws IOException {
		if(params != null && !params.isEmpty()){
			byte[] algorithm = Bytes.toBytes(algorithmId);
			for(Parameter param:params){
				String rowKey = UUIDGenerator.getUUID();
				Map<String, byte[]> values = new HashMap<String, byte[]>();
				values.put(QUALIFIERS[0], algorithm);
				values.put(QUALIFIERS[1], Bytes.toBytes(param.getName()));
				values.put(QUALIFIERS[2], Bytes.toBytes(param.getType()));
				values.put(QUALIFIERS[3], Bytes.toBytes(param.getDescription()));
				helper.addRowData(TABLE_NAME, rowKey, FAMILY, QUALIFIERS, values);
			}
		}
	}
	
	public List<Parameter> getParamsByAlgorithmId(String algorithmId) throws IOException{
		Map<String, byte[]> values = new HashMap<String, byte[]>();
		values.put(QUALIFIERS[0], Bytes.toBytes(algorithmId));
		List<Result> results = helper.getRowResults(TABLE_NAME, FAMILY, QUALIFIERS, values, null);
		if(results != null && !results.isEmpty()){
			List<Parameter> params = new ArrayList<Parameter>();
			for(Result result:results){
				Parameter param = new Parameter();
				byte[] name = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[1]));
				if(name != null){
					param.setName(Bytes.toString(name));
				}
				byte[] type = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[2]));
				if(type != null){
					param.setType(Bytes.toString(type));
				}
				byte[] note = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[3]));
				if(note != null){
					param.setDescription(Bytes.toString(note));
				}
				params.add(param);
			}
			return params;
		}
		return null;
	}
}
