package com.missionsky.scp.dataadapter.entity;

import java.util.HashMap;

/**
 * @author Ellis Xie
 * @version 1.0
 * 数据源的实体类
 */
public class DataSource {
	
	// 数据名称
	private String name;
	// 数据格式
	private Integer dataFormat;
	// 是否第一次取值
	private Boolean isFirst;
	// 取值步长
	private Integer step;
	// 数据总量
	private Integer total;
	// 数据总量的路径
	private String totalPath;
	// 储存路径
	private String storePath;
	// 错误类型路径
	private String errorTypePath;
	// 错误信息路径
	private String errorMessagePath;
	// 偏移量的url参数名称
	private String offsetParameterName;
	// 是否定时执行
	private Boolean taskTimer;
	// 定时执行时间间隔
	private Integer fecthDuration;
	// 数据源链接
	private String link;
	// 数据源描述
	private String description;
	// 数据结构中的数据路径
	private String recordsPath;
	// 数据结构中的成功标识路径
	private String successFlagPath;
	// 成功标识的值的Map
	private HashMap<String, Integer> successValues;
	// 数据源字段和标准文件字段映射的List
	private HashMap<String, HashMap<String, String>> standardFieldNameMaps;
	// 数据字段以及该字段的类型的hashmap
	private HashMap<String, String> fields;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getDataFormat() {
		return dataFormat;
	}
	public void setDataFormat(Integer dataFormat) {
		this.dataFormat = dataFormat;
	}
	public Boolean getIsFirst() {
		return isFirst;
	}
	public void setIsFirst(Boolean isFirst) {
		this.isFirst = isFirst;
	}
	public Integer getStep() {
		return step;
	}
	public void setStep(Integer step) {
		this.step = step;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public String getTotalPath() {
		return totalPath;
	}
	public void setTotalPath(String totalPath) {
		this.totalPath = totalPath;
	}
	public String getStorePath() {
		return storePath;
	}
	public void setStorePath(String storePath) {
		this.storePath = storePath;
	}
	public String getErrorTypePath() {
		return errorTypePath;
	}
	public void setErrorTypePath(String errorTypePath) {
		this.errorTypePath = errorTypePath;
	}
	public String getErrorMessagePath() {
		return errorMessagePath;
	}
	public void setErrorMessagePath(String errorMessagePath) {
		this.errorMessagePath = errorMessagePath;
	}
	public String getOffsetParameterName() {
		return offsetParameterName;
	}
	public void setOffsetParameterName(String offsetParameterName) {
		this.offsetParameterName = offsetParameterName;
	}
	public Boolean getTaskTimer() {
		return taskTimer;
	}
	public void setTaskTimer(Boolean taskTimer) {
		this.taskTimer = taskTimer;
	}
	public Integer getFecthDuration() {
		return fecthDuration;
	}
	public void setFecthDuration(Integer fecthDuration) {
		this.fecthDuration = fecthDuration;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getRecordsPath() {
		return recordsPath;
	}
	public void setRecordsPath(String recordsPath) {
		this.recordsPath = recordsPath;
	}
	public String getSuccessFlagPath() {
		return successFlagPath;
	}
	public void setSuccessFlagPath(String successFlagPath) {
		this.successFlagPath = successFlagPath;
	}
	public HashMap<String, Integer> getSuccessValues() {
		return successValues;
	}
	public void setSuccessValues(HashMap<String, Integer> successValues) {
		this.successValues = successValues;
	}
	public HashMap<String, HashMap<String, String>> getStandardFieldNameMaps() {
		return standardFieldNameMaps;
	}
	public void setStandardFieldNameMaps(
			HashMap<String, HashMap<String, String>> standardFieldNameMaps) {
		this.standardFieldNameMaps = standardFieldNameMaps;
	}
	public HashMap<String, String> getFields() {
		return fields;
	}
	public void setFields(HashMap<String, String> fields) {
		this.fields = fields;
	}
	
}
