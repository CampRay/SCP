package com.missionsky.scp.dataadapter.datafilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.DocumentException;
import com.missionsky.scp.dataadapter.common.SingleInstance;
import com.missionsky.scp.dataadapter.entity.DataSource;

public class DataFilter {
	
	private DataFilter(){
		
	}
	
	/**
	 * 
	 * @param mapData
	 * @param keys
	 * @return
	 * @throws DocumentException
	 */
	public static Map<String, String> parseMapData(Map<String, String> mapData,String keys) throws DocumentException {
		HashMap<String, DataSource> singleMap = SingleInstance.getDataSources();
		DataSource dataSource = singleMap.get(keys);
		HashMap<String, HashMap<String, String>> relationMap = dataSource.getStandardFieldNameMaps();
		if (mapData != null && !mapData.isEmpty()) {
			Map<String, String> map = new HashMap<String, String>();
			Set<String> keySet = new HashSet<String>();
			for (Map.Entry<String, String> entry : mapData.entrySet()) {
				if (relationMap != null && relationMap.containsKey(entry.getKey())) {
					Map<String, String> refMap = relationMap.get(entry.getKey());
					if(refMap != null && !refMap.isEmpty()){
						boolean flag = false;
						for(Map.Entry<String, String> entry2:refMap.entrySet()){
							if(entry2.getValue()!=null&& !"".equals(entry2.getValue().trim())){
								map.put(entry2.getValue(), entry.getValue());
								flag = true;
							}
						}
						if(flag){
							keySet.add(entry.getKey());
						}
					}
				}
			}
			if(!keySet.isEmpty()){
				for(String key:keySet){
					mapData.remove(key);
				}
			}
			mapData.putAll(map);
		}
		return mapData;
	}

	/**
	 * 
	 * @param listData
	 * @param key
	 * @return
	 * @throws DocumentException
	 */
	public static void parselistDate(List<Map<String, String>> listData,String key) throws DocumentException {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		if (listData != null && !listData.isEmpty()) {
			Iterator<Map<String, String>> iterator = listData.iterator();
			while (iterator.hasNext()) {
				Map<String, String> listMap = iterator.next();
				listMap = parseMapData(listMap,key);
				list.add(listMap);
			}
		}
		listData.clear();
		listData.addAll(list);
	}
}
