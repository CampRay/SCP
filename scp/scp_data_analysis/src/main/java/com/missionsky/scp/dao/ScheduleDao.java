package com.missionsky.scp.dao;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import com.missionsky.scp.dataanalysis.utils.HbaseUtil;

public class ScheduleDao {
	private final static String TABLE_NAME = "schedule";

	private final static String FAMILY = "job";

	private final static String[] QUALIFIER = { "status", "update_time" };

	private final static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private static ScheduleDao dao = new ScheduleDao();

	private ScheduleDao() {
	}

	public static ScheduleDao getInstance() {
		if (dao == null) {
			dao = new ScheduleDao();
		}
		return dao;
	}

	public void updateScheduleRecord(String rowKey, Integer status)
			throws IOException {
		if (rowKey != null && !"".equals(rowKey.trim())) {
			if (status != null) {
				Map<String, byte[]> map = new HashMap<String, byte[]>();
				map.put(QUALIFIER[0], Bytes.toBytes(status));
				String update_time = sdf.format(new Date());
				map.put(QUALIFIER[1], Bytes.toBytes(update_time));
				HbaseUtil.updateRowData(TABLE_NAME, rowKey, FAMILY, QUALIFIER,
						map);
			}
		}
	}
	
	public Integer getRunStatus(String rowKey) throws IOException{
		Integer status = null;
		if(rowKey == null || "".equals(rowKey.trim())){
			return status;
		}
		Result result = HbaseUtil.selectRowResult(TABLE_NAME, rowKey);
		if(result != null){
			byte[] value = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIER[0]));
			if(value != null){
				status = Bytes.toInt(value);
			}
		}
		return status;
	}
	
	public void deleteRow(String rowKey) throws IOException{
		if(rowKey == null || "".equals(rowKey.trim())){
			return;
		}
		HbaseUtil.deleteRowData(TABLE_NAME, rowKey);
	}
	
	public static void main(String[] args) throws IOException{
		HbaseUtil.deleteRowData(TABLE_NAME, "42e2cf53-258e-4efb-8f0b-49b08e81253f");
	}
}
