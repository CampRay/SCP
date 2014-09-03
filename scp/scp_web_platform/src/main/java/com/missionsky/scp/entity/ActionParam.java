package com.missionsky.scp.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ActionParam implements Serializable {
	//参数名称
	private String name;
	
	//参数值
	private String value;
	
	//参数描述
	private String description;
	
	//参数类型
	private String type;
	
	public ActionParam(){
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
