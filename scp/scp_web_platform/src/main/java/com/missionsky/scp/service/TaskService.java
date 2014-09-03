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

import com.missionsky.scp.dao.ActionDao;
import com.missionsky.scp.dao.AlgorithmDao;
import com.missionsky.scp.dao.BasicDao;
import com.missionsky.scp.dao.HbaseHelper;
import com.missionsky.scp.dao.MiningDao;
import com.missionsky.scp.dao.ParamDao;
import com.missionsky.scp.dao.TaskDao;
import com.missionsky.scp.entity.Action;
import com.missionsky.scp.entity.ActionParam;
import com.missionsky.scp.entity.Algorithm;
import com.missionsky.scp.entity.Mining;
import com.missionsky.scp.entity.Parameter;
import com.missionsky.scp.entity.StandardFile;
import com.missionsky.scp.entity.Task;
import com.missionsky.scp.util.UUIDGenerator;

@Component
public class TaskService {
	private Logger logger = LoggerFactory.getLogger(TaskService.class);
	private static final String TABLE_NAME = "scheduleTask";
	private static final String FAMILY = "task";
	private static final String[] QUALIFIERS = {"taskName","description","fileName","storePath","triggerType","startTime","time","weekday"}; 

	@Autowired
	private HbaseHelper helper;
	
	@Autowired
	private ParamDao paramDao;
	
	@Autowired
	private ActionDao actionDao;
	
	@Autowired
	private MiningDao miningDao;
	
	@Autowired
	private TaskDao taskDao;
	
	@Autowired
	private AlgorithmDao algorithmDao;
	
	@Autowired
	private BasicDao basicDao;
	
	@Autowired
	private FileService fileService;
	
	public List<Task> getAllTasks(Integer page) throws IOException{
		logger.info("select all task records:default pageSize 20");
		List<Result> list = new ArrayList<Result>();
		if(page == null || page == 1){
			list = helper.getAllData(TABLE_NAME, 20);
		}else{
			list = helper.getAllData(TABLE_NAME, 20*page);
			if(list != null && !list.isEmpty()){
				list.removeAll(helper.getAllData(TABLE_NAME, 20*(page-1)));
			}
		}
		List<Task> tasks = new ArrayList<Task>();
		if(list != null && !list.isEmpty()){
			for(Result result:list){
				Task task = new Task();
				byte[] taskName = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[0]));
				task.setTaskName(Bytes.toString(taskName));
				byte[] fileName = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[1]));
				task.setFileName(Bytes.toString(fileName));
				byte[] scheduleType = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[2]));
				task.setTriggerType(Bytes.toInt(scheduleType));
//				byte[] updateTime = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[3]));
//				Date date = DateUtil.parse(Bytes.toString(updateTime), "yyyy-MM-dd HH:mm:ss");
//				task.setUpdateTime(date);
//				byte[] createtime = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[4]));
//				date = DateUtil.parse(Bytes.toString(createtime), "yyyy-MM-dd HH:mm:ss");
//				task.setCreateTime(date);
				tasks.add(task);
			}
		}
		return tasks;
	}
	
	public Task findTask(String rowKey) throws IOException{
		Task task = taskDao.findTaskByRowKey(rowKey);
		if(task != null){
			List<Action> actions = actionDao.findActionsByTaskId(task.getRowKey());
			if(actions != null){
				task.setActions(actions);
			}
		}
		return task;
	}
	
	public void saveTask(Task task) throws IOException{
		logger.info("save task into hbase");
		String rowKey = task.getRowKey();
		if(rowKey == null || "".equals(rowKey.trim())){
			rowKey = UUIDGenerator.getUUID();
			task.setRowKey(rowKey);
		}else{
			List<String> rowKeys = actionDao.getrowkeys(rowKey);
			if(rowKeys != null && !rowKeys.isEmpty()){
				actionDao.deleteActions(rowKeys);
			}
		}
		taskDao.saveTask(task);
		List<Action> actions = task.getActions();
		if(actions != null && !actions.isEmpty()){
			for(Action action:actions){
				action.setPathName(algorithmDao.getPathNameByRowKey(action.getRowId()));
			}
		}
		if(actions != null && !actions.isEmpty()){
			actionDao.saveActions(actions, rowKey);
		}
	}
	
	public void saveMining(Mining mining,Map<String, Object> map) throws IOException{
		if(mining != null){
			String rowKey = mining.getRowKey();
			if(rowKey == null || "".equals(rowKey.trim())){
				rowKey = UUIDGenerator.getUUID();
				mining.setRowKey(rowKey);
			}else{
				List<String> rowkeys = actionDao.getrowkeys(rowKey);
				if(rowkeys != null && !rowkeys.isEmpty()){
					actionDao.deleteActions(rowkeys);
				}
			}
			miningDao.saveMining(mining);
			Action action = mining.getAction();
			if(action != null){
				List<Action> actions = new ArrayList<Action>();
				actions.add(action);
				actionDao.saveActions(actions, rowKey);
			}
			map.put("msg", "success");
		}else{
			map.put("msg", "empty");
		}
	}
	
	public void deleteTask(String rowKey) throws IOException{
		if(rowKey != null && !"".equals(rowKey.trim())){
			actionDao.deleteActions(actionDao.getrowkeys(rowKey)); //delete relate action msg
			taskDao.deleteTaskByRowKey(rowKey);
			logger.info("delete row which rowKey is " + rowKey + " success!");
		}
	}
	
	public void deleteMining(String rowKey) throws IOException{
		miningDao.deleteMiningByRowKey(rowKey);
	}
	
	public List<Task> searchTasks(Task task) throws Exception{
		List<Task> tasks = new ArrayList<Task>();
		Map<String, byte[]> values = new HashMap<String, byte[]>();
		if(task != null){
			if(task.getTaskName() != null && !"".equals(task.getTaskName().trim())){
				values.put(QUALIFIERS[0], Bytes.toBytes(task.getTaskName().trim()));
			}
			if(task.getFileName() != null && !"".equals(task.getFileName().trim())){
				values.put(QUALIFIERS[1], Bytes.toBytes(task.getFileName().trim()));
			}
		}
		List<Cell[]> cells = helper.getRowDatas(TABLE_NAME, TABLE_NAME, QUALIFIERS, values);
		
		for(Cell[] cell:cells){
			tasks.add(cellToTask(cell));
		}
		return tasks;
	}
	
	public Task cellToTask(Cell[] cells){
		Task task = null;
		if(cells != null && cells.length > 0){
			task = new Task();
			byte[] bytes = CellUtil.cloneRow(cells[0]);
			String rowKey = Bytes.toString(bytes);
			task.setRowKey(rowKey);
			for(int i=0;i<cells.length;i++){
				switch (i) {
				case 0:
					task.setTaskName(Bytes.toString(CellUtil.cloneValue(cells[i])));
					break;
				case 1:
					task.setFileName(Bytes.toString(CellUtil.cloneValue(cells[i])));
					break;
				case 2:
					task.setTriggerType(Bytes.toInt(CellUtil.cloneValue(cells[i])));
					break;
				case 3:
					task.setStatus(Bytes.toInt(CellUtil.cloneValue(cells[i])));
					break;
//				case 4:
//					String updateDate = Bytes.toString(CellUtil.cloneValue(cells[i]));
//					task.setUpdateTime(DateUtil.parse(updateDate, null));
//					break;
//				case 5:
//					String createDate = Bytes.toString(CellUtil.cloneValue(cells[i]));
//					task.setCreateTime(DateUtil.parse(createDate, null));
//					break;
				default:
					break;
				}
			}
		}
		return task;
	}
	
	public Mining findMiningByRowKey(String rowKey) throws IOException{
		Mining mining = miningDao.findMiningByRowKey(rowKey);
		if(mining != null){
			List<Action> actions = actionDao.findActionsByTaskId(mining.getRowKey());
			if(actions != null && !actions.isEmpty()){
				mining.setAction(actions.get(0));
			}
		}
		return mining;
	}
	
	public List<Mining> findMingsByName(String name) throws IOException{
		List<Mining> minings = miningDao.findAllMinings(name);
		if(minings != null && !minings.isEmpty()){
			for(Mining mining:minings){
				String source = mining.getSource();
				if(source != null && !"".equals(source.trim())){
					Result result = helper.selectRowResult(TABLE_NAME, source);
					if(result != null){
						byte[] taskName = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[0]));
						if(taskName != null && taskName.length > 0){
							mining.setSource(Bytes.toString(taskName));
						}else{
							mining.setSource("");
						}
					}
				}
			}
		}
		return minings;
	}

	public List<Task> findByTaskName(Task searchtask) throws IOException {
		logger.info("select all task records:default pageSize 20");
		List<Task> tasks = new ArrayList<Task>();
		if(searchtask != null){
			tasks = taskDao.findAllTasks(searchtask.getTaskName());
		}else{
			tasks = taskDao.findAllTasks("");
		}
		if(tasks != null && !tasks.isEmpty()){
			for(Task task:tasks){
				String fileId = task.getFileName();
				if(fileId != null && !"".equals(fileId.trim())){
					StandardFile file = fileService.getFileByRowKey(fileId);
					if(file != null){
						task.setFileName(file.getName());
					}else{
						task.setFileName("");
					}
				}else{
					task.setFileName("");
				}
				List<Action> actions = actionDao.findActionsByTaskId(task.getRowKey());
				if(actions != null){
					task.setActions(actions);
				}
			}
		}
		return tasks;
	}
	
	public void getParamsByRowkey(String rowKey, Map<String, Object> map) throws IOException{
		List<ActionParam> list = new ArrayList<ActionParam>();
		String description = "";
		String pathName = "";
		List<Parameter> params = paramDao.getParamsByAlgorithmId(rowKey);
		if(params != null && !params.isEmpty()){
			for(Parameter param:params){
				ActionParam actionParam = new ActionParam();
				actionParam.setName(param.getName());
				list.add(actionParam);
			}
		}
		Algorithm algorithm = algorithmDao.findTaskByRowKey(rowKey);
		if(algorithm != null){
			description = algorithm.getDescription();
			pathName = algorithm.getPathName();
		}
		map.put("description", description);
		map.put("pathName", pathName);
		map.put("list", list);
	}
	
	public void viewMiningResult(String rowKey, Map<String, Object> map) throws IOException{
		if(rowKey == null || "".equals(rowKey.trim())){
			map.put("msg", "empty");
			return;
		}
		Result result = basicDao.findMiningResult("miningResult", rowKey);
		if(result == null){
			map.put("msg", "empty");
			return;
		}
		Map<String, String> retMap = new HashMap<String,String>();
		for(Cell cell:result.rawCells()){
			if(cell != null){
				byte[] key = CellUtil.cloneQualifier(cell);
				byte[] value = CellUtil.cloneValue(cell);
				if(key != null && value != null){
					retMap.put(Bytes.toString(key), Bytes.toString(value));
				}
			}
		}
		map.put("msg", "success");
		map.put("retMap", retMap);
	}

	public void viewTaskResult(String rowKey, Map<String, Object> map) throws IOException {
		if(rowKey == null || "".equals(rowKey.trim())){
			map.put("msg", "empty");
			return;
		}
		
		List<String> columns = basicDao.findColumnByRowkey(rowKey);
		if(columns.isEmpty()){
			map.put("msg", "empty");
			return;
		}
		List<Result> results = basicDao.findDataByTableAndSize("task"+rowKey, 50);
		if(results == null || results.isEmpty()){
			map.put("msg", "empty");
			return;
		}
		List<List<String>> list = new ArrayList<List<String>>();
		for(Result result:results){
			List<String> strs = new ArrayList<String>();
			for(int i=0;i<columns.size();i++){
				byte[] obj = result.getValue(Bytes.toBytes("task"), Bytes.toBytes(columns.get(i)));
				if(obj != null){
					strs.add(Bytes.toString(obj));
				}else {
					strs.add("");
				}
			}
		}
		map.put("msg", "success");
		map.put("list", list);
		map.put("columns", columns);
	}
	
	public List<String> downLoadData(String rowKey) throws IOException {
		List<String> list = new ArrayList<String>();
		if(rowKey == null || "".equals(rowKey.trim())){
			return list;
		}
		
		List<String> columns = basicDao.findColumnByRowkey(rowKey);
		if(columns.isEmpty()){
			return list;
		}
		List<Result> results = basicDao.findDataByTableAndSize("task"+rowKey, null);
		if(results == null || results.isEmpty()){
			return list;
		}
		for(Result result:results){
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<columns.size();i++){
				byte[] obj = result.getValue(Bytes.toBytes("task"), Bytes.toBytes(columns.get(i)));
				if(obj != null){
					sb.append(Bytes.toString(obj)+";");
				}else {
					sb.append(" ;");
				}
			}
			if(sb.length() > 0){
				list.add(sb.toString());
			}
		}
		return list;
	}
}
