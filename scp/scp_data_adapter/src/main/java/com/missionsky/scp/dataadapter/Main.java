package com.missionsky.scp.dataadapter;

import java.util.HashMap;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.missionsky.scp.dataadapter.common.ConfigParser;
import com.missionsky.scp.dataadapter.common.SingleInstance;
import com.missionsky.scp.dataadapter.datafetcher.DataFetcherDispenser;
import com.missionsky.scp.dataadapter.util.XmlUtil;

/**
 * @author Ellis Xie
 * @version 1.0
 * scp_data_adapter主程序 
 */
@ComponentScan
@Configuration
public class Main {

	private static Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
//		TODO shell
		
		
		
		
		// 加载配置文件
		ConfigParser.loadConfiguration();
		
		String filePath = SingleInstance.getFilePath();
		HashMap<String, String> configFiles = SingleInstance.getConfigFiles();
		for (String key : configFiles.keySet()) {
			String confFileName = filePath + "/" + configFiles.get(key);
			try {
				logger.info("Begin to parser the datasources configuration file:{}", confFileName);
				
				Document doc = XmlUtil.read(confFileName);
				DataFetcherDispenser dataFetcherDispenser = new DataFetcherDispenser();
				dataFetcherDispenser.gainParsedData(doc);
			} catch (DocumentException e) {
				logger.error("The configuration file {} is not found.", confFileName);
			}
		}
	}
}
