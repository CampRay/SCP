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

import com.missionsky.scp.entity.Task;
import com.missionsky.scp.util.DateUtil;
import com.missionsky.scp.util.UUIDGenerator;

@Component
public class TaskDao {
	private static final String TABLE_NAME = "scheduleTask";
	private static final String FAMILY = "task";
	private static final String[] QUALIFIERS = { "taskName", "description",
			"fileName", "storePath", "triggerType", "startTime", "time",
			"weekday" };
	@Autowired
	private HbaseHelper helper;

	private Map<String, byte[]> getValues(Task task) {
		Map<String, byte[]> map = new HashMap<String, byte[]>();
		if (task.getTaskName() != null && !"".equals(task.getTaskName().trim())) {
			map.put(QUALIFIERS[0], Bytes.toBytes(task.getTaskName()));
			if (task.getDescription() != null) {
				map.put(QUALIFIERS[1], Bytes.toBytes(task.getDescription()));
			}
			if (task.getFileName() != null) {
				map.put(QUALIFIERS[2], Bytes.toBytes(task.getFileName()));
			}
			if (task.getStorePath() != null) {
				map.put(QUALIFIERS[3], Bytes.toBytes(task.getStorePath()));
			}
			if (task.getTriggerType() != null) {
				map.put(QUALIFIERS[4], Bytes.toBytes(task.getTriggerType()));
				if (task.getTriggerType() == 1) {
					if (task.getTime() != null) {
						map.put(QUALIFIERS[6], Bytes.toBytes(task.getTime()));
					}
				} else if (task.getTriggerType() == 2) {
					if (task.getTime() != null) {
						map.put(QUALIFIERS[6], Bytes.toBytes(task.getTime()));
					}
					if (task.getWeekday() != null) {
						map.put(QUALIFIERS[7], Bytes.toBytes(task.getWeekday()));
					}
				}
			}
			if (task.getStartTime() != null) {
				String date = DateUtil.format(task.getStartTime(), null);
				map.put(QUALIFIERS[5], Bytes.toBytes(date));
			}
		}
		return map;
	}

	public void saveTask(Task task) throws IOException {
		if (task != null) {
			Map<String, byte[]> map = getValues(task);
			if (!map.isEmpty()) {
				String rowKey = task.getRowKey();
				if (rowKey == null || rowKey.trim().equals("")) {
					rowKey = UUIDGenerator.getUUID();
				}
				helper.updateRowData(TABLE_NAME, rowKey, FAMILY, QUALIFIERS,
						map);
			}
		}
	}

	public Task findTaskByRowKey(String rowKey) throws IOException {
		Result result = helper.selectRowResult(TABLE_NAME, rowKey);
		return getTask(result);
	}

	public void deleteTaskByRowKey(String rowkey) throws IOException {
		helper.deleteRowData(TABLE_NAME, rowkey);
	}

	public List<Task> findAllTasks(String taskName) throws IOException {
		Map<String, byte[]> map = new HashMap<String, byte[]>();
		if (taskName != null && !"".equals(taskName.trim())) {
			map.put(QUALIFIERS[0], Bytes.toBytes(taskName));
		}
		List<Result> results = helper.getRowResults(TABLE_NAME, FAMILY,
				QUALIFIERS, map, null);
		List<Task> tasks = new ArrayList<Task>();
		if (results != null && !results.isEmpty()) {
			for (Result result : results) {
				Task task = getTask(result);
				if (task != null) {
					tasks.add(task);
				}
			}
		}
		return tasks;
	}

	private Task getTask(Result result) {
		if (result != null) {
			Task task = new Task();
			task.setRowKey(Bytes.toString(result.getRow()));
			byte[] name = result.getValue(Bytes.toBytes(FAMILY),
					Bytes.toBytes(QUALIFIERS[0]));
			if (name != null) {
				task.setTaskName(Bytes.toString(name));
			}
			byte[] description = result.getValue(Bytes.toBytes(FAMILY),
					Bytes.toBytes(QUALIFIERS[1]));
			if (description != null) {
				task.setDescription(Bytes.toString(description));
			}
			byte[] file = result.getValue(Bytes.toBytes(FAMILY),
					Bytes.toBytes(QUALIFIERS[2]));
			if (file != null) {
				task.setFileName(Bytes.toString(file));
			}
			byte[] storePath = result.getValue(Bytes.toBytes(FAMILY),
					Bytes.toBytes(QUALIFIERS[3]));
			if (storePath != null) {
				task.setStorePath(Bytes.toString(storePath));
			}
			byte[] triggerType = result.getValue(Bytes.toBytes(FAMILY),
					Bytes.toBytes(QUALIFIERS[4]));
			if (triggerType != null && triggerType.length > 0) {
				task.setTriggerType(Bytes.toInt(triggerType));
			}
			byte[] startTime = result.getValue(Bytes.toBytes(FAMILY),
					Bytes.toBytes(QUALIFIERS[5]));
			if (startTime != null) {
				Date date = DateUtil.parse(Bytes.toString(startTime), null);
				task.setStartTime(date);
			}
			byte[] time = result.getValue(Bytes.toBytes(FAMILY),
					Bytes.toBytes(QUALIFIERS[6]));
			if (time != null && time.length > 0) {
				task.setTime(Bytes.toInt(time));
			}
			byte[] weekday = result.getValue(Bytes.toBytes(FAMILY),
					Bytes.toBytes(QUALIFIERS[7]));
			if (weekday != null && weekday.length > 0) {
				task.setWeekday(Bytes.toInt(weekday));
			}
			return task;
		}
		return null;
	}
}
