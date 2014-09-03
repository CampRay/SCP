package com.missionsky.scp.dataadapter.datafetcher;

import java.util.List;
import java.util.Map;

import com.missionsky.scp.dataadapter.entity.DataSource;

/**
 * @author Ellis Xie 
 * @version 1.0
 * DataFetcher接口
 */
public interface DataFetcher {
	
	public List<Map<String, String>> renderMergedFetchedData(DataSource dataSource, Integer offset);
	
	public Object getTargetByPath(Object object, String pathStr);
	
	public Integer getTotal(DataSource dataSource);

}
