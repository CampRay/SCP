package com.missionsky.scp.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.missionsky.scp.entity.Mining;
import com.missionsky.scp.util.DateUtil;
import com.missionsky.scp.util.UUIDGenerator;

@Component
public class MiningDao {
	private static final String TABLE_NAME = "mining";
	private static final String FAMILY = "task";
	private static final String[] QUALIFIERS = { "name", "description",
			"source", "trigger", "start", "time", "weekday", "algorithm",
			"params" };

	@Autowired
	private HbaseHelper helper;

	private Map<String, byte[]> getValues(Mining mining) {
		Map<String, byte[]> values = new HashMap<String, byte[]>();
		if (mining.getName() != null && !"".equals(mining.getName().trim())) {
			values.put(QUALIFIERS[0], Bytes.toBytes(mining.getName()));
			if (mining.getDescription() != null
					&& !"".equals(mining.getDescription().trim())) {
				values.put(QUALIFIERS[1],
						Bytes.toBytes(mining.getDescription()));
			}
			if (mining.getSource() != null && !"".equals(mining.getSource())) {
				values.put(QUALIFIERS[2], Bytes.toBytes(mining.getSource()));
			}
			if (mining.getTrigger() != null) {
				values.put(QUALIFIERS[3], Bytes.toBytes(mining.getTrigger()));
				if(mining.getTrigger() == 1){
					if (mining.getTime() != null) {
						values.put(QUALIFIERS[5], Bytes.toBytes(mining.getTime()));
					}
				}else if(mining.getTrigger() == 2){
					if (mining.getTime() != null) {
						values.put(QUALIFIERS[5], Bytes.toBytes(mining.getTime()));
					}
					if (mining.getWeekday() != null) {
						values.put(QUALIFIERS[6], Bytes.toBytes(mining.getWeekday()));
					}
				}
			}
			if (mining.getStart() != null) {
				String startTime = DateUtil.format(mining.getStart(), "");
				values.put(QUALIFIERS[4], Bytes.toBytes(startTime));
			}
		}
		return values;
	}

	public void saveMining(Mining mining) throws IOException {
		if (mining != null) {
			Map<String, byte[]> map = getValues(mining);
			if(!map.isEmpty()){
				String rowKey = mining.getRowKey();
				if(rowKey==null || rowKey.trim().equals("")){
					rowKey = UUIDGenerator.getUUID();
				}
				helper.updateRowData(TABLE_NAME, rowKey, FAMILY, QUALIFIERS, map);
			}
		}
	}
	
	public Mining findMiningByRowKey(String rowKey) throws IOException{
		Result result = helper.selectRowResult(TABLE_NAME, rowKey);
		return getMining(result);
	}
	
	public void deleteMiningByRowKey(String rowkey) throws IOException{
		helper.deleteRowData(TABLE_NAME, rowkey);
	}
	
	public List<Mining> findAllMinings(String name) throws IOException{
		Map<String, byte[]> map = new HashMap<String, byte[]>();
		if(name != null && !"".equals(name.trim())){
			map.put(QUALIFIERS[0], Bytes.toBytes(name));
		}
		List<Result> results = helper.getRowResults(TABLE_NAME, FAMILY, QUALIFIERS, map, null);
		List<Mining> minings = new ArrayList<Mining>();
		if(results != null && !results.isEmpty()){
			for(Result result:results){
				Mining mining = getMining(result);
				if(mining != null){
					minings.add(mining);
				}
			}
		}
		return minings;
	}
	
	private Mining getMining(Result result){
		if(result != null){
			Mining mining = new Mining();
			mining.setRowKey(Bytes.toString(result.getRow()));
			byte[] name = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[0]));
			if(name != null && name.length > 0){
				mining.setName(Bytes.toString(name));
			}
			byte[] description = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[1]));
			if(description != null && description.length > 0){
				mining.setDescription(Bytes.toString(description));
			}
			byte[] source = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[2]));
			if(source != null && source.length > 0){
				mining.setSource(Bytes.toString(source));
			}
			byte[] trigger = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[3]));
			if(trigger != null && trigger.length > 0){
				mining.setTrigger(Bytes.toInt(trigger));
			}
			byte[] startTime = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[4]));
			if(startTime != null && startTime.length > 0){
				Date start = DateUtil.parse(Bytes.toString(startTime), null);
				mining.setStart(start);
			}
			byte[] time = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[5]));
			if(time != null && time.length > 0){
				mining.setTime(Bytes.toInt(time));
			}
			byte[] weekday = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[6]));
			if(weekday != null && weekday.length > 0){
				mining.setWeekday(Bytes.toInt(weekday));
			}
//			byte[] algorithm = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[7]));
//			if(algorithm != null && algorithm.length > 0){
//				mining.setAlgorithm(Bytes.toString(algorithm));
//			}
//			byte[] param = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[8]));
//			if(param != null && param.length > 0){
//				String str = Bytes.toString(param);
//				String[] strs = str.split(":");
//				if(strs.length > 0){
//					List<String> params = new ArrayList<String>();
//					for(String string:strs){
//						params.add(string);
//					}
//					mining.setParams(params);
//				}
//			}
			return mining;
		}
		return null;
	}
}
