package com.missionsky.scp.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.missionsky.scp.entity.StandardFile;
import com.missionsky.scp.service.FileService;
import com.missionsky.scp.util.EncodingTool;

@Controller
@RequestMapping("config")
public class ConfigAction {
	private static Logger logger = LoggerFactory.getLogger(ConfigAction.class);
	
	@Autowired
	private FileService fileService;
	
	@RequestMapping(value="configlist",method=RequestMethod.GET)
	public String algorithmList(Model model,String fileName) {
		logger.info("config file list page");
		List<StandardFile> files = new ArrayList<StandardFile>();
		try {
			if(fileName != null && !"".equals(fileName.trim())){
				fileName = EncodingTool.encodeStr(fileName);
			}
			files = fileService.findAllFiles(fileName);
			model.addAttribute("files", files);
			model.addAttribute("size", files.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
		model.addAttribute("fileName", fileName);
		model.addAttribute("file", new StandardFile());
		return "config/fileconfiglist";
	}
	
	@RequestMapping(value="saveuploadfile",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> saveUploadFile(StandardFile file){
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			fileService.saveStandardFile(map, file);
		} catch (IOException e) {
			e.printStackTrace();
			map.put("msg", "failure");
		}
		return map;
	}
	
	@RequestMapping(value="deletefile",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> deleteFile(String rowKey){
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if(rowKey != null && !rowKey.trim().equals("")){
				fileService.deleteFile(rowKey);
				map.put("msg", "success");
			}else{
				map.put("msg", "rowKey is null or ''");
			}
		} catch (IOException e) {
			e.printStackTrace();
			map.put("msg", "failure");
		}
		return map;
	}
}
