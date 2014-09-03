package com.missionsky.scp.dataadapter.datafetcher;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.missionsky.scp.dataadapter.common.SingleInstance;
import com.missionsky.scp.dataadapter.common.SystemConstants;
import com.missionsky.scp.dataadapter.entity.DataSource;

/**
 * @author Ellis.xie
 * @version 1.0
 * DataFetcher转发器
 */
public class DataFetcherDispenser {
	// 数据名称
	private String dataSourceName = null;
	// 数据结构
	private Element dataStructures = null;
	// 成功标识的值的Map
	private HashMap<String, Integer> successValues = null;
	// 数据源字段和标准文件字段映射的List
	private HashMap<String, HashMap<String, String>> standardFieldNameMaps = null;
	
	private HashMap<String, String> fields = null;
	
	private ScheduledThreadPoolExecutor threadPool = null;
	
	DataSource ds = null;
	
	HashMap<String, DataSource> DataSourceMap = null;
	
	private Logger logger = LoggerFactory.getLogger(DataFetcherDispenser.class);

	/**
	 * @author Ellis.xie
	 * @param doc
	 * 获取解析后的数据
	 */
	@SuppressWarnings("unchecked")
	public void gainParsedData(Document doc) {
				
		List<Element> dataSources = doc.getRootElement().elements();
		
		threadPool =  new ScheduledThreadPoolExecutor(dataSources.size());
		
		for (Element dataSource : dataSources) {
			DataSourceMap = SingleInstance.getDataSources();
			ds = new DataSource();
			dataSourceName = dataSource.attributeValue("name");
			ds.setName(dataSourceName);
			ds.setIsFirst(true);
			ds.setTaskTimer(false);
			
			List<Element> childrenNode = dataSource.elements();
			
			for (Element childNode : childrenNode) {
				if (childNode.getName().equals("dataFormat")) {
					ds.setDataFormat(Integer.parseInt(childNode.getTextTrim()));
				} else if (childNode.getName().equals("step")) {
					ds.setStep(Integer.parseInt(childNode.getTextTrim()));
				} else if (childNode.getName().equals("taskTimer")) {
					ds.setTaskTimer(Boolean.parseBoolean(childNode.getTextTrim()));
				} else if (childNode.getName().equals("fecthDuration")) {
					ds.setFecthDuration(Integer.parseInt(childNode.getTextTrim()));
				} else if (childNode.getName().equals("link")) {
					ds.setLink(childNode.getText());
				} else if (childNode.getName().equals("description")) {
					ds.setDescription(childNode.getText());
				} else if (childNode.getName().equals("offsetParamName")) {
					ds.setOffsetParameterName(childNode.getTextTrim());
				} else if (childNode.getName().equals("successFlag")) {
					// 获取成功标识信息
					ds.setSuccessFlagPath(childNode.attributeValue("path"));
					List<Element> successValueList = childNode.elements();
					successValues = new HashMap<String, Integer>();
					for (Element successValue : successValueList) {
						successValues.put(successValue.attributeValue("name"), Integer.parseInt(successValue.getText()));
					}
					ds.setSuccessValues(successValues);
				} else if (childNode.getName().equals("error")) {
					ds.setErrorTypePath(childNode.attributeValue("errorTypePath"));
					ds.setErrorMessagePath(childNode.attributeValue("errorMessagePath"));
				} else if (childNode.getName().equals("total")) {
					ds.setTotalPath(childNode.attributeValue("path"));
				} else if (childNode.getName().equals("storePath")) {
					List<Element> storePaths = childNode.elements();
					TreeMap<Integer, String> pathMap = new TreeMap<Integer, String>();
					StringBuffer path = new StringBuffer(); 
					for (Element subStorePath : storePaths) {
						pathMap.put(Integer.parseInt(subStorePath.attributeValue("index").toString()), subStorePath.attributeValue("value"));
					}
					for (Integer key : pathMap.keySet()) {
						path.append(pathMap.get(key)+"_");
					}
					ds.setStorePath(path.toString());
				}
				
				else if (childNode.getName().equals("dataStructures")) {
					dataStructures = childNode;

					// 获取dataStructures的子节点List
					List<Element> dataStructureChildren = dataStructures.elements();

					for (Element dataStructureChild : dataStructureChildren) {

						// 获取字段映射信息
						if (dataStructureChild.getName().equals("fieldMap")) {
							// 获取数据path
							ds.setRecordsPath(dataStructureChild.attributeValue("path"));

							// 获取fieldMap的子节点List
							List<Element> fieldMaps = dataStructureChild.elements();
							standardFieldNameMaps = new HashMap<String, HashMap<String, String>>();
							fields = new HashMap<String, String>();
							for (Element fieldMap : fieldMaps) {
								fields.put(fieldMap.attributeValue("name"), fieldMap.attributeValue("type"));

								// 获取数据源字段和标准文件字段映射
								List<Element> maps = fieldMap.elements();
								if (!maps.isEmpty()) {
									HashMap<String, String> standardMaps = new HashMap<String, String>();
									for (Element map : maps) {
										standardMaps.put(map.attributeValue("standardId"), map.attributeValue("standardName"));
									}
									standardFieldNameMaps.put(fieldMap.attributeValue("name"), standardMaps);
								}
							}
							ds.setFields(fields);
							ds.setStandardFieldNameMaps(standardFieldNameMaps);
						}
					}
				}
			}

			// 根据dataformat选择fetcher
			DataFetcher dataFetcher = null;

			switch (ds.getDataFormat()) {
			case SystemConstants.DATA_FORMAT_JSON:
				dataFetcher = new JSONDataFetcher();
				break;
			case SystemConstants.DATA_FORMAT_XML:
				dataFetcher = new XMLDataFetcher();
				break;
			default:

				break;
			}
			
			logger.info("Use {} to fetching data of the datasource {}",dataFetcher.getClass(),dataSourceName);
			
			DataSourceMap.put(dataSourceName, ds);
			SingleInstance.setDataSources(DataSourceMap);
			
			logger.info("Create a timer task thread for datasource {} in threadpool.", dataSourceName);
			System.out.println(ds.getFecthDuration()+"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~`");
			threadPool.scheduleAtFixedRate(new ExcuteFecthThread(SingleInstance.getDataSources().get(dataSourceName), dataFetcher), 2000, ds.getFecthDuration(), TimeUnit.MILLISECONDS);
		}
		
	}

}
