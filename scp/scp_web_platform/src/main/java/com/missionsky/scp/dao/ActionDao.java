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

import com.missionsky.scp.entity.Action;
import com.missionsky.scp.entity.ActionParam;
import com.missionsky.scp.util.UUIDGenerator;

@Component
public class ActionDao {
	private static final String TABLE_NAME = "actions";
	private static final String FAMILY = "action";
	private static final String[] QUALIFIERS = {"taskId","rowId","name","params","description", "pathName"};
	
	@Autowired
	private HbaseHelper helper;
	
	public List<String> getrowkeys(String taskId) throws IOException{
		Map<String, byte[]> values = new HashMap<String, byte[]>();
		values.put(QUALIFIERS[0], Bytes.toBytes(taskId));
		return helper.getRowKeys(TABLE_NAME, FAMILY, QUALIFIERS, values);
	}

	public void deleteActions(List<String> rowKeys) throws IOException {
		if(rowKeys != null && !rowKeys.isEmpty()){
			for(String rowKey:rowKeys){
				helper.deleteRowData(TABLE_NAME, rowKey);
			}
		}
	}
	
	public void saveActions(List<Action> actions, String taskId) throws IOException {
		if(actions != null && !actions.isEmpty()){
			byte[] task = Bytes.toBytes(taskId);
			for(Action action:actions){
				if(action != null && action.getRowId() != null && !"".equals(action.getRowId().trim())){
					String rowKey = UUIDGenerator.getUUID();
					Map<String, byte[]> values = new HashMap<String, byte[]>();
					values.put(QUALIFIERS[0], task);
					if(action.getRowId() != null){
						values.put(QUALIFIERS[1], Bytes.toBytes(action.getRowId()));
					}
					if(action.getName() != null){
						values.put(QUALIFIERS[2], Bytes.toBytes(action.getName()));
					}
					if(action.getDescription() != null){
						values.put(QUALIFIERS[4], Bytes.toBytes(action.getDescription()));
					}
					if(action.getParams() != null && !action.getParams().isEmpty()){
						StringBuffer sb = new StringBuffer();
						for(ActionParam param:action.getParams()){
							sb.append(param.getName()+":"+param.getValue()+";");
						}
						if(sb.length() > 0){
							values.put(QUALIFIERS[3], Bytes.toBytes(sb.substring(0, sb.length()-1)));
						}
					}
					if(action.getPathName() != null){
						values.put(QUALIFIERS[5], Bytes.toBytes(action.getPathName()));
					}
					helper.updateRowData(TABLE_NAME, rowKey, FAMILY, QUALIFIERS, values);
				}
			}
		}
	}

	public List<Action> findActionsByTaskId(String rowKey) throws IOException {
		Map<String, byte[]> values = new HashMap<String, byte[]>();
		values.put(QUALIFIERS[0], Bytes.toBytes(rowKey));
		List<Result> results = helper.getRowResults(TABLE_NAME, FAMILY, QUALIFIERS, values, null);
		if(results != null && !results.isEmpty()){
			List<Action> actions = new ArrayList<Action>();
			for(Result result:results){
				Action action = new Action();
				byte[] rowId = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[1]));
				if(rowId != null){
					action.setRowId(Bytes.toString(rowId));
				}
				byte[] name = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[2]));
				if(name != null){
					action.setName(Bytes.toString(name));
				}
				byte[] params = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[3]));
				if(params != null){
					String param = Bytes.toString(params);
					String[] parameters = param.split(";");
					List<ActionParam> actionParams = new ArrayList<ActionParam>();
					for(String str:parameters){
						String[] strs = str.split(":");
						if(strs.length == 2){
							ActionParam actionParam = new ActionParam();
							actionParam.setName(strs[0]);
							actionParam.setValue(strs[1]);
							actionParams.add(actionParam);
						}
					}
					action.setParams(actionParams);
				}
				byte[] description = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[4]));
				if(description != null){
					action.setDescription(Bytes.toString(description));
				}
				byte[] pathName = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[5]));
				if(pathName != null){
					action.setPathName(Bytes.toString(pathName));
				}
				actions.add(action);
			}
			return actions;
		}
		return null;
	}
}
