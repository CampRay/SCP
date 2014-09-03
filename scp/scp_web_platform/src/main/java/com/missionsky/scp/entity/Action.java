package com.missionsky.scp.entity;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class Action implements Serializable {
	private String rowKey;
	
	//任务ID
	private String taskId;
	
	//算法主键ID
	private String rowId;
	
	//算法名称
	private String name;
	
	//算法路径
	private String pathName;
	
	//算法描述
	private String description;
	
	//算法参数
	private List<ActionParam> params;

	public String getRowKey() {
		return rowKey;
	}

	public void setRowKey(String rowKey) {
		this.rowKey = rowKey;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getRowId() {
		return rowId;
	}

	public void setRowId(String rowId) {
		this.rowId = rowId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ActionParam> getParams() {
		return params;
	}

	public void setParams(List<ActionParam> params) {
		this.params = params;
	}
}
