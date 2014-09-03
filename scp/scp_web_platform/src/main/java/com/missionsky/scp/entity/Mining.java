package com.missionsky.scp.entity;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class Mining implements Serializable {
	private String rowKey;

	private String name;

	private String description;

	private String source;

	private Integer trigger;

	private Date start;

	private Integer time;

	private Integer weekday;

	private Integer status;
	
	private Action action;

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

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Integer getTrigger() {
		return trigger;
	}

	public void setTrigger(Integer trigger) {
		this.trigger = trigger;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	public Integer getWeekday() {
		return weekday;
	}

	public void setWeekday(Integer weekday) {
		this.weekday = weekday;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}
}
