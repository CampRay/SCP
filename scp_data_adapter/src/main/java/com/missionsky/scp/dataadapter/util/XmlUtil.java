package com.missionsky.scp.dataadapter.util;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class XmlUtil {
	private static final SAXReader reader = new SAXReader();
	private XmlUtil(){
	}
	
	//XML文件解析
	/*
	 * fileName 完整文件路径+名称
	 */
	public static Document read(String fileName) throws DocumentException{
		return reader.read(new File(fileName));
	}
	
	//URL解析
	public static Document read(URL url) throws DocumentException{
		return reader.read(url);
	}
	
	//字符串式解析
	public static Document readFromStr(String fStr) throws DocumentException{
		return DocumentHelper.parseText(fStr);
	}
	
	public static Element getRootElement(Document doc){
		return doc.getRootElement();
	}
	
	//节点遍历
	@SuppressWarnings("rawtypes")
	public void elementIterator(Element root,Map<String, String> map){
		for(Iterator iterator=root.elementIterator();iterator.hasNext();){
			Element element = (Element) iterator.next();
			if(!element.elements().isEmpty()){
				elementIterator(element, map);
			}else{
				map.put(element.getName(), element.getText());
			}
		}
	}
	
	public void iterator(String fileName,Map<String, String> map) throws DocumentException{
		Document doc = read(fileName);
		Element root = getRootElement(doc);
		elementIterator(root, map);
	}
}
