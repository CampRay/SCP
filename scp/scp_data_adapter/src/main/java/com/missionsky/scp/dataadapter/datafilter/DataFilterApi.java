package com.missionsky.scp.dataadapter.datafilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.missionsky.scp.dataadapter.common.SystemConstants;
import com.missionsky.scp.dataadapter.persistence.HadoopFileUtil;
import com.missionsky.scp.dataadapter.util.FileNameUtil;

public class DataFilterApi {
	private static final Logger logger = LoggerFactory.getLogger(DataFilterApi.class);
	
	private DataFilterApi(){}
	
	/**
	 * 数据分类转换存储
	 * @param data 数据 格式为：Map<String, String>和List<Map<String, String>>
	 * @param name 关键字或者名称 configuration中的唯一值
	 * @param type 数据操作方式 1:增加数据 2：更新数据 3：删除数据
	 */
	public static void filterClassifiedStorage(Object data,String name,Integer type){
		if(type != null && name != null && !name.trim().equals("")){//name 和 type 不能为空
			if(type.equals(SystemConstants.DATA_FLITER_TYPE_INSERT) && data == null){//当type为1时新增数据的数据data不能为空
				return;
			}
			if(type.equals(SystemConstants.DATA_FLITER_TYPE_INSERT)){
				saveDataToFile(data, name);
			}else if(type.equals(SystemConstants.DATA_FLITER_TYPE_UPDATE)){
				deleteFile(name);
				saveDataToFile(data, name);
			}else if(type.equals(SystemConstants.DATA_FLITER_TYPE_DELETE)){
				deleteFile(name);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void saveDataToFile(Object data,String name){
		String fileName = "";
		logger.info("instanceof object types:Map or List,recommend to use List");
		if(data instanceof Map){
			Map<String, String> map = (Map<String, String>) data;
			try {
				if(map != null && !map.isEmpty()){
					logger.info("filter data start");
					DataFilter.parseMapData(map, name);//转换数据
					StringBuffer sb = new StringBuffer();
					for(Entry<String, String> entry:map.entrySet()){
						sb.append(entry.getKey()+":"+entry.getValue()+" ");
					}
					if(sb.length() >  0){
						logger.info("transfrom data to byte[]");
						byte[] bytes = sb.substring(0, sb.length()-1).getBytes();
						logger.info("get file name from xml configuration");
						fileName = FileNameUtil.getFileName(name);
						if(fileName != null && !fileName.trim().equals("")){
							uploadFile(name, fileName, bytes);
						}else{
							logger.error("get File Name error!!!");
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}else if(data instanceof List){
			List<Map<String, String>> list = (List<Map<String, String>>) data;
			try {
				if(list != null && !list.isEmpty()){
					DataFilter.parselistDate(list, name);//转换数据
					StringBuffer sb = new StringBuffer();
					for(int i=0;i<list.size();i++){
						Map<String, String> map = list.get(i);
						for(Entry<String, String> entry:map.entrySet()){
							sb.append(entry.getKey()+":"+entry.getValue()+" ");
						}
						sb.append("\r\n");
					}
					if(sb.length() >  0){
						logger.info("transfrom data to byte[]");
						byte[] bytes = sb.substring(0, sb.length()-1).getBytes();
						logger.info("get file name from xml configuration");
						fileName = FileNameUtil.getFileName(name);
						if(fileName != null && !fileName.trim().equals("")){
							uploadFile(name, fileName, bytes);
						}else{
							logger.error("get File Name error!!!");
						}
					}
				}
			} catch (DocumentException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
	}
	
	private static void uploadFile(String name,String fileName,byte[] bytes){
		//检测HDFS中是否存在该目录
		if(!HadoopFileUtil.checkFile(name)){
			logger.info(name + " directory created.");
			HadoopFileUtil.createDir(name);//创建目录
		}else{
			logger.info(name + " directory exist.");
		}
		//获取该目录下的所有文件并返回最小文件的路径和大小
		Map<String, String> hdfsPath = HadoopFileUtil.listAllFile(name);
		//获取配置文件中上传服务器的uri
		
		if(hdfsPath == null || Long.parseLong(hdfsPath.get("size"))>128*1024*1024){
			logger.info("create file {} and write data",fileName);
			HadoopFileUtil.createFile("/"+name+"/"+fileName, bytes);
		}else{
			String path = hdfsPath.get("path");
			String[] paths = path.split(":9000");
			path = paths[paths.length-1];
			logger.info("append data to exist file {}",path);
			HadoopFileUtil.appendFile(bytes, path);
		}
	}
	
	private static void deleteFile(String name){
		if(HadoopFileUtil.checkFile(name)){
			HadoopFileUtil.deleteFile(name);
		}
	}
}
