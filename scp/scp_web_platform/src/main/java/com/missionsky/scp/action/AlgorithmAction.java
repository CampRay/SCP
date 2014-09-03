package com.missionsky.scp.action;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.missionsky.scp.dao.DataClient;
import com.missionsky.scp.entity.Algorithm;
import com.missionsky.scp.service.AlgorithmService;
import com.missionsky.scp.util.EncodingTool;

@Controller
@RequestMapping("algorithm")
public class AlgorithmAction {
	private static Logger logger = LoggerFactory
			.getLogger(AlgorithmAction.class);

	@Autowired
	private AlgorithmService algorithmService;

	@Autowired
	private DataClient client;

	@RequestMapping(value = "algorithmlist", method = RequestMethod.GET)
	public String algorithmList(Model model, String name, Integer page) {
		List<Algorithm> algorithms = new ArrayList<Algorithm>();
		try {
			if (name != null && !"".equals(name.trim())) {
				name = EncodingTool.encodeStr(name);
			}
			algorithms = algorithmService.findByNameAndPage(name, page);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		model.addAttribute("algorithms", algorithms);
		model.addAttribute("algorithm", new Algorithm());
		model.addAttribute("algorithmName", name);
		return "algorithm/algorithmList";
	}

	@RequestMapping(value = "updatealgorithm", method = RequestMethod.GET)
	public String updateAlgorithm(Model model, HttpServletRequest request,
			HttpServletResponse response, String rowkey) {
		Algorithm algorithm = new Algorithm();
		if (rowkey != null && !"".equals(rowkey.trim())) {
			try {
				rowkey = EncodingTool.encodeStr(rowkey);
				algorithm = algorithmService.findAlgorithm(rowkey);
			} catch (IOException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
		model.addAttribute("algorithm", algorithm);
		try {
			if(algorithm.getType() != null){
				model.addAttribute("algorithms", client.getAlgorithmByType(algorithm.getType()));
			}
		} catch (RemoteException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return "algorithm/updateAlgorithm";
	}

	@RequestMapping(value = "savealgorithm", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> saveAlgorithm(Algorithm algorithm) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			algorithmService.saveAlgorithm(algorithm, map);
		} catch (IOException e) {
			e.printStackTrace();
			map.put("msg", "failure");
		}
		return map;
	}

	@RequestMapping(value = "deletealgorithm", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> deleteAlgorithm(String rowKey) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			algorithmService.deleteTask(rowKey);
			map.put("msg", "success");
		} catch (IOException e) {
			e.printStackTrace();
			map.put("msg", "failure");
		}
		return map;
	}

	@RequestMapping(value = "getalgorithmparam", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getAlgorithmParam(String algorithmName) {
		Map<String, Object> ret = new HashMap<String, Object>();
		try {
			ret.putAll(client.getAlgorithmMsg(algorithmName));
			ret.put("msg", "success");
		} catch (Exception e) {
			e.printStackTrace();
			ret.put("msg", "failure");
		}
		return ret;
	}

	@RequestMapping(value = "getalgorithmbytype", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getAlgorithmByType(Integer type) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map.put("algorithms", client.getAlgorithmByType(type));
			map.put("msg", "success");
		} catch (RemoteException e) {
			e.printStackTrace();
			map.put("msg", "failure");
		}
		return map;
	}
}
