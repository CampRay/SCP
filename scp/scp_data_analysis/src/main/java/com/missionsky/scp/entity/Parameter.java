package com.missionsky.scp.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Parameter implements Serializable{
	//param name
	private String name;
	
	//param type
	private String type;
	
	//param description
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
