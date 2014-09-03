package com.missionsky.scp.entity;

import java.util.List;

public class Algorithm {
	//hbase row rowkey
	private String rowKey;
	
	//algorithm name
	private String name;
	
	//algorithm type:basic or mining
	private Integer type;
	
	//algorithm description
	private String description;
	
	//algorithm package
	private String pathName;
	
	//algorithm params
	private List<Parameter> params;

	public String getRowKey() {
		return rowKey;
	}

	public void setRowKey(String rowKey) {
		this.rowKey = rowKey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	public List<Parameter> getParams() {
		return params;
	}

	public void setParams(List<Parameter> params) {
		this.params = params;
	}
}
