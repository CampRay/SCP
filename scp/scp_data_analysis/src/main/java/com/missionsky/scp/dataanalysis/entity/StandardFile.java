package com.missionsky.scp.dataanalysis.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.missionsky.scp.dao.StandardFileDao;
import com.missionsky.scp.dataanalysis.utils.PropertiesUtil;
import com.missionsky.scp.dataanalysis.utils.XmlUtil;

/**
 * Standard file entity for parsing standard file. 
 * @author ellis.xie
 * @version 1.0
 */

public class StandardFile {
	
	private Element root;
	
	private String name;
	
	private String standardFileID;
	
	private List<Element> columns;
	
	private static Logger logger = LoggerFactory.getLogger(StandardFile.class);
	
	private String fileDirectoryPath = PropertiesUtil.loadPropertiesFile(StandardFile.class.getResource("/").getPath() + "/config.properties").getProperty("fileDirectoryPath");
	
	@SuppressWarnings("unchecked")
	public StandardFile(String standardFileKey){
		try {
			com.missionsky.scp.entity.StandardFile sf = StandardFileDao.getFileByRowKey(standardFileKey);
			Document document = DocumentHelper.parseText(sf.getContent());
			this.root = document.getRootElement();
			this.name = sf.getName();
			this.standardFileID = root.elementText("id");
			this.columns = root.element("content").elements();
		} catch (DocumentException e) {
			logger.error("Can not convert string into XML Document Object:{}",e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getName(){
		return this.name;
	}

	public ArrayList<String> getColumnNames(){
		ArrayList<String> columnNames = new ArrayList<String>();
		
		for (Element column : columns) {
			columnNames.add(column.elementText("name"));
		}
		
		return columnNames;
	}
	
	public ArrayList<String> getStandardKeys(){
		ArrayList<String> standardKeys = new ArrayList<String>();
		
		for (Element column : columns) {
			if (Boolean.parseBoolean(column.elementText("key").trim())) {
				standardKeys.add(column.elementText("name"));
			}
		}
		
		return standardKeys;
	}
	
	//Load datasource configuration file and get standard field name map
	@SuppressWarnings("unchecked")
	public LinkedHashMap<String, String> LoadDataConfFile(String confName){
		LinkedHashMap<String, String> standardFieldNameMap = new LinkedHashMap<String, String>();
		
		String configFilePath = fileDirectoryPath + (confName.endsWith(".xml")?confName:(confName+".xml"));
		
		try {
			Document doc = XmlUtil.read(configFilePath);
			Element root = doc.getRootElement();
			List<Element> fields = root.element("dataStructures").element("fieldMap").elements();
			ArrayList<String> columnNames = getColumnNames();
			
			for (String fieldName : columnNames) {
				standardFieldNameMap.put(fieldName, "");
			}
			
			Integer order = 0;
			
			for (Element field : fields) {
				List<Element> maps = field.elements();
				for (Element map : maps) {
					if (map.attributeValue("standardId").equals(standardFileID)) {
						standardFieldNameMap.put(map.attributeValue("standardName"), order.toString());
						break;
					}
				}
				order++;
			}
		} catch (DocumentException e) {
			logger.error("The configuration file {} is not found!", configFilePath);
		}
		
		return standardFieldNameMap;
	}
	
	//Get order in datasource of standard keys
	public String[] getStandardKeyOrder(String confName){
		ArrayList<String> standardKeys = getStandardKeys();
		LinkedHashMap<String, String> standardFieldNameMap = LoadDataConfFile(confName);
		
		String[] standardKeyOrder = new String[standardKeys.size()];
		for (int i = 0; i < standardKeys.size(); i++) {
			standardKeyOrder[i] = standardFieldNameMap.get(standardKeys.get(i));
		}
		
		return standardKeyOrder;
	}
	
	//Get order in datasource of standard fields
	public String[] getStandardFieldOrder(String confName){
		ArrayList<String> columnNames = getColumnNames();
		LinkedHashMap<String, String> standardFieldNameMap = LoadDataConfFile(confName);
		
		String[] standardFieldOrder = new String[columnNames.size()];
		int i = 0;
		for (String key : standardFieldNameMap.keySet()) {
			standardFieldOrder[i] = standardFieldNameMap.get(key);
			i++;
		}
		
		return standardFieldOrder;
	}
		
	

}
