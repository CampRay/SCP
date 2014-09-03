package com.missionsky.scp.action;

import java.io.IOException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.missionsky.scp.dao.DataClient;
import com.missionsky.scp.entity.Mining;
import com.missionsky.scp.entity.Task;
import com.missionsky.scp.service.AlgorithmService;
import com.missionsky.scp.service.FileService;
import com.missionsky.scp.service.TaskService;
import com.missionsky.scp.util.EncodingTool;
import com.missionsky.scp.util.SysConstants;

@Controller
@RequestMapping("task")
public class TaskAction {
	private Logger logger = LoggerFactory.getLogger(TaskAction.class);
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private AlgorithmService algorithmService;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private DataClient client;
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(
				dateFormat, true));
	}

	/**
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "tasklist", method = RequestMethod.GET)
	public String taskList(Model model, Task task) {
		List<Task> list = new ArrayList<Task>();
		try {
			list = taskService.findByTaskName(task);
			if(list != null && !list.isEmpty()){
				for(Task t:list){
					t.setStatus(client.getRunStatus(t.getRowKey()));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		model.addAttribute("tasks", list);
		model.addAttribute("task", new Task());
		if(task != null){
			model.addAttribute("taskName", task.getTaskName());
			model.addAttribute("fileName", task.getFileName());
		}else{
			model.addAttribute("taskName", "");
			model.addAttribute("fileName", "");
		}
		return "task/taskList";
	}
	
	@RequestMapping(value="executeresult", method = RequestMethod.GET)
	public String executeResult(Model model,Task task){
		List<Task> list = new ArrayList<Task>();
		try {
			list = taskService.findByTaskName(task);
			if(list != null && !list.isEmpty()){
				for(Task t:list){
					t.setStatus(client.getRunStatus(t.getRowKey()));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		model.addAttribute("tasks", list);
		model.addAttribute("task", new Task());
		if(task != null){
			model.addAttribute("taskName", task.getTaskName());
			model.addAttribute("fileName", task.getFileName());
		}else{
			model.addAttribute("taskName", "");
			model.addAttribute("fileName", "");
		}
		return "task/executeResult";
	}
	
	@RequestMapping(value="mininglist",method=RequestMethod.GET)
	public String miningList(Model model,String name){
		if(name != null){
			name = EncodingTool.encodeStr(name);
		}
		try {
			List<Mining> minings = taskService.findMingsByName(name);
			if(minings != null && !minings.isEmpty()){
				for(Mining mining:minings){
					mining.setStatus(client.getRunStatus(mining.getRowKey()));
				}
			}
			model.addAttribute("minings", minings);
		} catch (IOException e) {
			e.printStackTrace();
		}
		model.addAttribute("mining", new Mining());
		model.addAttribute("name", name);
		return "task/miningList";
	}
	
	@RequestMapping(value="miningresult",method=RequestMethod.GET)
	public String miningResult(Model model,String name){
		if(name != null){
			name = EncodingTool.encodeStr(name);
		}
		try {
			List<Mining> minings = taskService.findMingsByName(name);
			if(minings != null && !minings.isEmpty()){
				for(Mining mining:minings){
					mining.setStatus(client.getRunStatus(mining.getRowKey()));
				}
			}
			model.addAttribute("minings", minings);
		} catch (IOException e) {
			e.printStackTrace();
		}
		model.addAttribute("mining", new Mining());
		model.addAttribute("name", name);
		return "task/miningResult";
	}
	
	@RequestMapping(value="searchtasks",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> searchTasks(Task task){
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Task> tasks = taskService.searchTasks(task);
			map.put("msg", "success");
			map.put("list", tasks);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("msg", "failure");
		}
		return map;
	}
	
	@RequestMapping(value="updatetask",method=RequestMethod.GET)
	public String updateTask(Model model,String rowKey){
		Task task = new Task();
		if(rowKey != null && !"".equals(rowKey.trim())){
			try {
				task = taskService.findTask(rowKey);
			} catch (IOException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
		model.addAttribute("task", task);
		try {
			model.addAttribute("files", fileService.getAllFiles());
			model.addAttribute("algorithms", algorithmService.findAllAlgorithmsByType(SysConstants.BASIC));
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "task/updateTask";
	}
	
	@RequestMapping(value="updatemining",method=RequestMethod.GET)
	public String updateMining(Model model,String rowKey){
		Mining mining = new Mining();
		if(rowKey != null){
			try {
				mining = taskService.findMiningByRowKey(rowKey);
			} catch (IOException e) {
				e.printStackTrace();
				mining = new Mining();
			}
		}
		try {
			model.addAttribute("sources", taskService.findByTaskName(null));
			model.addAttribute("algorithms", algorithmService.findAllAlgorithmsByType(SysConstants.MINING));
		} catch (IOException e) {
			e.printStackTrace();
		}
		model.addAttribute("mining", mining);
		/*StringWriter writer = new StringWriter();
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(writer, mining);
			String json = writer.toString();
			model.addAttribute("miningobj", json);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		return "task/updateMining";
	}
	
	@RequestMapping(value="savetask",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> saveTask(Task task){
		Map<String, Object> map = new HashMap<String,Object>();
		try {
			String rowKey = task.getRowKey();
			taskService.saveTask(task);
			if(rowKey != null && !"".equals(rowKey.trim())){
				client.removeRecord(rowKey);
			}
			map.put("msg", "success");
		} catch (IOException e) {
			e.printStackTrace();
			map.put("msg", "failure");
		}
		return map;
	}
	
	@RequestMapping(value="savemining",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> saveMining(Mining mining){
		Map<String, Object> map = new HashMap<String,Object>();
		try {
			String rowKey = mining.getRowKey();
			taskService.saveMining(mining,map);
			if(rowKey != null && !"".equals(rowKey.trim())){
				client.removeRecord(rowKey);
			}
		} catch (IOException e) {
			e.printStackTrace();
			map.put("msg", "failure");
		}
		return map;
	}
	
	@RequestMapping(value="viewtaskresult", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> viewTaskResult(String rowKey){
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			taskService.viewTaskResult(rowKey,map);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("msg", "failure");
		}
		return map;
	}
	
	@RequestMapping(value="viewminingresult", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> viewMiningResult(String rowKey){
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			taskService.viewMiningResult(rowKey, map);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("msg", "failure");
		}
		return map;
	}
	
	@RequestMapping(value="deletetask", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> deleteTask(String rowKey){
		Map<String, Object> map = new HashMap<String,Object>();
		try {
			taskService.deleteTask(rowKey);
			client.deleteJob(rowKey);
			map.put("msg", "success");
		} catch (IOException e) {
			e.printStackTrace();
			map.put("msg", "failure");
		}
		return map;
	}
	
	@RequestMapping(value="deletemining", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> deleteMining(String rowKey){
		Map<String, Object> map = new HashMap<String,Object>();
		try {
			taskService.deleteMining(rowKey);
			map.put("msg", "success");
		} catch (IOException e) {
			e.printStackTrace();
			map.put("msg", "failure");
		}
		return map;
	}
	
	@RequestMapping(value="runtask", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> runTask(String rowKey){
		Map<String, Object> map = new HashMap<String,Object>();
		try {
			Task task = taskService.findTask(rowKey);
			if(task != null){
				client.runTask(task);
			}
			map.put("msg", "success");
		} catch (IOException e) {
			e.printStackTrace();
			map.put("msg", "failure");
		}
		return map;
	}
	
	@RequestMapping(value="runmining", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> runMining(String rowKey){
		Map<String, Object> map = new HashMap<String,Object>();
		try {
			Mining mining = taskService.findMiningByRowKey(rowKey);
			if(mining != null){
				client.runMining(mining);
			}
			map.put("msg", "success");
		} catch (IOException e) {
			e.printStackTrace();
			map.put("msg", "failure");
		}
		return map;
	}
	
	@RequestMapping(value="getalgorithmparam",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getAlgorithmParam(String rowKey){
		System.out.println(rowKey);
		Map<String, Object> map = new HashMap<String,Object>();
			try {
				taskService.getParamsByRowkey(rowKey,map);
				map.put("msg", "success");
			} catch (Exception e) {
				e.printStackTrace();
				map.put("msg", "failure");
			}
		return map;
	}
}
