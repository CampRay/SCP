package com.missionsky.scp.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class StandardFile implements Serializable {
	private String rowKey;
	
	//file name
	private String name;
	
	// description of file
	private String description;
	
	//file content
	private String content;
	
	private String path;
	
	public StandardFile(){
		
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
