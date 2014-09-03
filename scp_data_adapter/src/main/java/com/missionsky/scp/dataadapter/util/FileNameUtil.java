package com.missionsky.scp.dataadapter.util;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import com.missionsky.scp.dataadapter.common.SingleInstance;
import com.missionsky.scp.dataadapter.entity.DataSource;

public class FileNameUtil {
	private FileNameUtil() {
	}
	
	public static String getFileName(String name) throws IOException{
		HashMap<String, DataSource> map = SingleInstance.getDataSources();
		if(!map.isEmpty()){
			DataSource dataSource = map.get(name);
			String fileName = dataSource.getStorePath();
			if(fileName != null && !fileName.trim().equals("")){
//				Properties properties = PropertiesUtil.loadPropertiesFile("");
//				if(properties != null){
//					String path = properties.getProperty("fileDirectoryPath");
//					if(path != null){
//						File file = new File(path + fileName
//								+ new Date().getTime()+".txt");
//						File parent = file.getParentFile();
//						if (parent != null && !parent.exists()) {
//							parent.mkdirs();
//						}
//						file.createNewFile();
//						if (file.exists()) {
//							return file.getAbsolutePath();
//						}
//					}
//				}
				return fileName+new Date().getTime();
			}
		}
		return "";
	}

	/**
	 * 生成文件并返回文件路径
	 * @param key
	 * @return
	 * @throws DocumentException
	 * @throws IOException
	 */
	public static String getFileNames(String key) throws DocumentException,
			IOException {
		String configPath = FileNameUtil.class.getResource("/").getPath();
		Properties properties = PropertiesUtil.loadPropertiesFile(configPath
				+ "/config.properties");
		if (properties != null) {
			String filePath = properties.getProperty("fileNameSetPath");
			Document doc = XmlUtil.read(filePath);
			if (doc != null) {
				Element root = XmlUtil.getRootElement(doc);
				if (root != null) {
					String fileName = iterator(root);
					String path = properties.getProperty("fileDirectoryPath");
					if (path != null && !path.equals("")) {
						File file = new File(path + fileName
								+ new Date().getTime() + key + ".txt");
						File parent = file.getParentFile();
						if (parent != null && !parent.exists()) {
							parent.mkdirs();
						}
						file.createNewFile();
						if (file.exists()) {
							return file.getAbsolutePath();
						}
					}
				}
			}
		}
		return "";
	}

	/**
	 * 遍历XML文件内容
	 * @param root
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static String iterator(Element root) {
		StringBuffer sb = new StringBuffer();
		for (Iterator iterator = root.elementIterator(); iterator.hasNext();) {
			Element element = (Element) iterator.next();
			if (!element.elements().isEmpty()) {
				sb.append(iterator(element));
			} else {
				sb.append(element.getText() + "_");
			}
		}
		return sb.toString();
	}
}
