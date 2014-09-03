package com.missionsky.scp.dataadapter.datafetcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.missionsky.scp.dataadapter.common.SystemConstants;
import com.missionsky.scp.dataadapter.entity.DataSource;

/**
 * @author Ellis Xie 
 * @version 1.0
 * XML格式的datafetcher
 */
public class XMLDataFetcher implements DataFetcher {
	
	private static Logger logger = LoggerFactory.getLogger(XMLDataFetcher.class);

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, String>> renderMergedFetchedData(DataSource dataSource, Integer offset) {
		
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		
		String xmlStr = getXMLString(dataSource.getLink(), dataSource.getOffsetParameterName(), offset);
		try {
			Document doc = DocumentHelper.parseText(xmlStr);
			Element root = doc.getRootElement();
			Element successElement = (Element) getTargetByPath(root, dataSource.getSuccessFlagPath());
			Integer successValue = dataSource.getSuccessValues().get(successElement.getTextTrim());
			HashMap<String, String> singleField = null;
			
			if (successValue.equals(SystemConstants.SUCCESS_FLAG_HALT)) {
				logger.error("{}:{}",getTargetByPath(root, dataSource.getErrorTypePath()),getTargetByPath(root, dataSource.getErrorMessagePath()));
				return null;
			} else if (successValue.equals(SystemConstants.SUCCESS_FLAG_GOON)) {
				Element recordsElement = (Element) getTargetByPath(root, dataSource.getRecordsPath());
				List<Element> records = recordsElement.elements();
				for (Element record : records) {
					singleField = new HashMap<String, String>();
					List<Element> fields = record.elements();
					for (Element field : fields) {
						singleField.put(field.getName(), field.getText());
					}
					result.add(singleField);
				}
			}
		} catch (DocumentException e) {
			logger.error("Can not convert string into XML Document Object:{}",e.toString());
		}
		return result;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Object getTargetByPath(Object object, String pathStr) {
		String[] paths = pathStr.split(" ");
		Element element = (Element) object;
		for (String path : paths) {
			List<Element> elements  =  element.elements();
			for (Element ele : elements) {
				if (ele.getName().equals(path)) {
					element = ele;
					break;
				}
			}
		}
		return element;
	}
	
	public static String getXMLString(String link, String offsetParameterName, Integer offset){
		if (offset != null) {
			if (link.contains("?")) {
				link = link + "&" + offsetParameterName + "=" + offset;
			} else {
				link = link + "?" + offsetParameterName + "=" + offset;
			}
			
		}
		StringBuffer sb = new StringBuffer();
		String str = null;
		try {
			// 连接数据源
			URL url = new URL(link);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.connect();
			// 获取XML数据
			InputStream in = connection.getInputStream();
			Reader reader = new InputStreamReader(in, "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(reader);
			while ((str = bufferedReader.readLine()) != null) {
				sb.append(str);
			}
			// 关闭reader，断开连接
			reader.close();
			connection.disconnect();
		} catch (MalformedURLException e) {
			logger.error("The URL {} is invalid and cannot be loaded:{}", link, e.toString());
		} catch (IOException e) {
			logger.error("Can not get the input stream of the URL {}:{}", link, e.toString());
		}

		return sb.toString();
	}

	@Override
	public Integer getTotal(DataSource dataSource) {
		Integer total = null;
		String xmlStr = getXMLString(dataSource.getLink(), dataSource.getOffsetParameterName(), null);
		try {
			Document doc = DocumentHelper.parseText(xmlStr);
			Element element = (Element) getTargetByPath(doc.getRootElement(), dataSource.getTotalPath());
			total = Integer.parseInt(element.getTextTrim());
		} catch (DocumentException e) {
			logger.error("Can not convert string into XML Document Object:{}",e.toString());
		}
		return total;
	}

}
