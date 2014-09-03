package com.missionsky.scp.dataadapter.datafilter;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BinaryFileOperator {
	private Logger logger = LoggerFactory.getLogger(BinaryFileOperator.class);
	public void binaryWrite(Map<String, String> map,String fileName){
		try{
			if(map != null && !map.isEmpty()){
				DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
				for(Map.Entry<String, String> entry:map.entrySet()){
					out.writeBytes(entry.getKey()+":"+entry.getValue()+" ");
				}
				out.writeBytes("\r\n");
				out.close();
			}
		}catch(IOException e){
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}
	
	public void binaryWrite(List<Map<String, String>> list,String fileName){
		try {
			if(list != null && !list.isEmpty()){
				DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
				for(int i=0;i<list.size();i++){
					Map<String, String> map = list.get(i);
					if(map != null && !map.isEmpty()){
						for(Map.Entry<String, String> entry:map.entrySet()){
							out.writeBytes(entry.getKey()+":"+entry.getValue()+" ");
						}
						out.writeBytes("\r\n");
					}
				}
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}
}
