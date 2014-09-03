package com.missionsky.scp.dataadapter.common;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.missionsky.scp.dataadapter.util.PropertiesUtil;
import com.missionsky.scp.dataadapter.util.XmlUtil;

/**
 * @author Ellis Xie
 * @version 1.0
 * 配置文件解析器
 */
public class ConfigParser {
	
	private static Logger logger = LoggerFactory.getLogger(ConfigParser.class);
	
	@SuppressWarnings("unchecked")
	public static void loadConfiguration(){
		Properties property = PropertiesUtil.loadPropertiesFile("");
		String filePath = SingleInstance.getFilePath();
		HashMap<String, String> standardFileMap = SingleInstance.getStandardFiles();
		HashMap<String, String> configFileMap = SingleInstance.getConfigFiles();
		
		logger.info("Begin to load system configuration:{}", property.getProperty("config_path"));
		
		try {
			Document doc = XmlUtil.read(property.getProperty("config_path"));
			Element root = doc.getRootElement();
			List<Element> elements = root.elements();
			for (Element element : elements) {
				if (element.getName().equals("filePath")) {
					filePath = element.getText();
				} else if (element.getName().equals("standardFiles")) {
					List<Element> standardFiles = element.elements();
					for (Element standardFile : standardFiles) {
						standardFileMap.put(standardFile.attributeValue("id"), standardFile.attributeValue("fileName"));
					}
				} else if (element.getName().equals("configFiles")) {
					List<Element> configFiles = element.elements();
					for (Element configFile : configFiles) {
						configFileMap.put(configFile.attributeValue("id"), configFile.attributeValue("fileName"));
					}
				}
			}
		} catch (DocumentException e) {
			logger.error("failed to load configuration:{}",e.toString());
		}
		
		SingleInstance.setFilePath(filePath);
		SingleInstance.setStandardFiles(standardFileMap);
		SingleInstance.setConfigFiles(configFileMap);
	}
}
