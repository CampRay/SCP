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

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.missionsky.scp.dataadapter.common.SystemConstants;
import com.missionsky.scp.dataadapter.entity.DataSource;

/**
 * @author Ellis Xie 
 * @version 1.0
 * JSON格式的datafetcher
 */
public class JSONDataFetcher implements DataFetcher {

	private static Logger logger = LoggerFactory.getLogger(JSONDataFetcher.class);

	@Override
	public List<Map<String, String>> renderMergedFetchedData(DataSource dataSource, Integer offset) {
		String jsonStr = getJSONString(dataSource.getLink(), dataSource.getOffsetParameterName(), offset);
		JsonNode successNode = (JsonNode) getTargetByPath(jsonStr, dataSource.getSuccessFlagPath());
		Integer successValue = dataSource.getSuccessValues().get(successNode.toString());
		HashMap<String, String> singleField = null;
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();

		// 判断获取数据操作是否成功
		if (successValue.equals(SystemConstants.SUCCESS_FLAG_HALT)) {
			logger.error("{}:{}",getTargetByPath(jsonStr, dataSource.getErrorTypePath()),getTargetByPath(jsonStr, dataSource.getErrorMessagePath()));
			return null;
		} else if (successValue.equals(SystemConstants.SUCCESS_FLAG_GOON)) {
			JsonNode recordsNode = (JsonNode) getTargetByPath(jsonStr, dataSource.getRecordsPath());
			if (!recordsNode.isNull() && recordsNode.isArray()) {
				for (JsonNode record : recordsNode) {
					singleField = new HashMap<String, String>();
					for (String key : dataSource.getFields().keySet()) {
						singleField.put(key, record.get(key).toString());
					}
					result.add(singleField);
				}
			}
		}

		return result;
	}

	/**
	 * @author Ellis Xie 获取JSON字符串
	 */
	protected String getJSONString(String link, String offsetParameterName, Integer offset) {
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
			// 获取JSON数据
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

	/**
	 * @author Ellis Xie 获取指定路径的标签
	 */
	@Override
	public Object getTargetByPath(Object object, String pathStr) {
		String[] paths = pathStr.split(" ");
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = null;
		try {
			node = mapper.readTree(object.toString());
		} catch (JsonProcessingException e) {
			logger.error("Can not convert string into JSONNode:{}",e.toString());
		} catch (IOException e) {
			logger.error("Can not convert string into JSONNode:{}",e.toString());
		}
		for (String path : paths) {
			node = node.get(path);
		}

		return node;
	}

	@Override
	public Integer getTotal(DataSource dataSource) {
		String jsonStr = getJSONString(dataSource.getLink(), dataSource.getOffsetParameterName(), null);
		JsonNode total = (JsonNode) getTargetByPath(jsonStr, dataSource.getTotalPath());
		Integer result = null;
		if (total != null) {
			result = Integer.parseInt(total.toString().trim());
		}
		return result;
	}
	
	
	

}
