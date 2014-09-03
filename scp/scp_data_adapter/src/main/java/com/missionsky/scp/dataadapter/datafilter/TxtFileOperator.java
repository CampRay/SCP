package com.missionsky.scp.dataadapter.datafilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TxtFileOperator {
	private static Logger logger = LoggerFactory.getLogger(TxtFileOperator.class);
	private TxtFileOperator(){
		
	}
	public static void writeTxtFile(Map<String, String> map,String fileName) throws IOException{
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		FileOutputStream fos = null;
		PrintWriter pw = null;
		String temp = "";
		try {
			if(map != null && !map.isEmpty()){
				File file = new File(fileName);	
				fis = new FileInputStream(file);
				isr = new InputStreamReader(fis);
				br = new BufferedReader(isr);
				StringBuffer buf = new StringBuffer();
				for(@SuppressWarnings("unused")
				int j=1;(temp=br.readLine())!=null;j++){
					buf = buf.append(temp);
					buf.append(System.getProperty("line.separator"));
				}
				for(Map.Entry<String, String> entry:map.entrySet()){
					buf.append(entry.getKey()+":"+entry.getValue()+"; ");
				}
				buf.append("\r\n");
				fos = new FileOutputStream(file);
				pw = new PrintWriter(fos);
				pw.write(buf.toString().toCharArray());
				pw.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} finally{
			if(pw != null){
				pw.close();
			}
			if(fos != null){
				fos.close();
			}
			if(br != null){
				br.close();
			}
			if(isr != null){
				isr.close();
			}
			if(fis != null){
				fis.close();
			}
		}
	}
	
	public static void writeTxtFile(List<Map<String, String>> list,String fileName) throws IOException{
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		FileOutputStream fos = null;
		PrintWriter pw = null;
		String temp = "";
		try {
			if(list != null && !list.isEmpty()){
				File file = new File(fileName);	
				fis = new FileInputStream(file);
				isr = new InputStreamReader(fis);
				br = new BufferedReader(isr);
				StringBuffer buf = new StringBuffer();
				for(@SuppressWarnings("unused")
				int j=1;(temp=br.readLine())!=null;j++){
					buf = buf.append(temp);
					buf.append(System.getProperty("line.separator"));
				}
				for(int i=0;i<list.size();i++){
					Map<String, String> map = list.get(i);
					if(map != null && !map.isEmpty()){
						for(Map.Entry<String, String> entry:map.entrySet()){
							buf.append(entry.getKey()+":"+entry.getValue()+"; ");
						}
						buf.append("\r\n");
					}
				}
				fos = new FileOutputStream(file);
				pw = new PrintWriter(fos);
				pw.write(buf.toString().toCharArray());
				pw.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} finally{
			if(pw != null){
				pw.close();
			}
			if(fos != null){
				fos.close();
			}
			if(br != null){
				br.close();
			}
			if(isr != null){
				isr.close();
			}
			if(fis != null){
				fis.close();
			}
		}
	}
	
	public static void main(String[] args) throws IOException{
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		for(int j=0;j<15;j++){
			Map<String, String> map = new HashMap<String, String>();
			for(int i=0;i<20;i++){
				map.put(i+"key"+i,j+"value"+i);
			}
			list.add(map);
		}
		String fileName = "C:/kuka2.txt";
		TxtFileOperator.writeTxtFile(list, fileName);
	}
}
