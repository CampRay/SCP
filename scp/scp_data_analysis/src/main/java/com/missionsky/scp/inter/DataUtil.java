package com.missionsky.scp.inter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.SchedulerException;

import com.missionsky.scp.dao.ScheduleDao;
import com.missionsky.scp.dataanalysis.Constants;
import com.missionsky.scp.dataanalysis.algorithm.Algorithm;
import com.missionsky.scp.dataanalysis.algorithm.BasicAlgorithm;
import com.missionsky.scp.dataanalysis.algorithm.MinerAlgorithm;
import com.missionsky.scp.dataanalysis.entity.AlgorithmInfoAnnotation;
import com.missionsky.scp.dataanalysis.entity.ParamField;
import com.missionsky.scp.dataanalysis.entity.StandardTask;
import com.missionsky.scp.dataanalysis.utils.ClassUtil;
import com.missionsky.scp.entity.Action;
import com.missionsky.scp.entity.Mining;
import com.missionsky.scp.entity.Parameter;
import com.missionsky.scp.entity.Task;
import com.missionsky.scp.quartz.SimpleQuartz;

@SuppressWarnings("serial")
public class DataUtil extends UnicastRemoteObject implements IDataUtil {
	private static final String GROUP = "schedule_group";

	protected DataUtil() throws RemoteException {
		super();
	}

	@Override
	public Map<String, String> getFiles() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<String> getAlgorithms() throws RemoteException {
		List<String> list = new ArrayList<String>();
		List<Class> classes = ClassUtil
				.getAllClassByFatherClass(BasicAlgorithm.class);
		if (classes != null && !classes.isEmpty()) {
			for (Class clazz : classes) {
				list.add(clazz.getSimpleName());
			}
		}
		classes = ClassUtil.getAllClassByFatherClass(MinerAlgorithm.class);
		if (classes != null && !classes.isEmpty()) {
			for (Class clazz : classes) {
				list.add(clazz.getSimpleName());
			}
		}
		return list;
	}

	@SuppressWarnings("rawtypes")
	public List<String> getAlgorithmByType(Integer type) throws RemoteException {
		List<String> list = new ArrayList<String>();
		List<Class> classes = new ArrayList<Class>();
		if (type == 1) {
			classes = ClassUtil.getAllClassByFatherClass(BasicAlgorithm.class);
		} else if (type == 2) {
			classes = ClassUtil.getAllClassByFatherClass(MinerAlgorithm.class);
		}

		if (classes != null && !classes.isEmpty()) {
			for (Class clazz : classes) {
				list.add(clazz.getSimpleName());
			}
		}
		return list;
	}

	@Override
	public List<Parameter> getAlgorithmParams(String algorithmName)
			throws RemoteException {
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getAlgorithmMsg(String algorithmName)
			throws RemoteException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Class> classes = ClassUtil
				.getAllClassByFatherClass(Algorithm.class);
		if (classes != null && !classes.isEmpty()) {
			for (Class<?> clazz : classes) {
				String className = clazz.getSimpleName();
				if (className.equals(algorithmName)) {
					map.put("pathName", clazz.getName());
					AlgorithmInfoAnnotation annotation = clazz
							.getAnnotation(AlgorithmInfoAnnotation.class);
					if (annotation != null) {
						map.put("type", annotation.algorithmType().toString());
						map.put("description", annotation.description());
					}
					Field[] fields = clazz.getDeclaredFields();
					if (fields != null && fields.length > 0) {
						List<Parameter> parameters = new ArrayList<Parameter>();
						for (Field field : fields) {
							ParamField paramField = field
									.getAnnotation(ParamField.class);
							if (paramField != null) {
								Parameter param = new Parameter();
								param.setName(paramField.name());
								param.setType(paramField.type());
								param.setDescription(paramField.description());
								parameters.add(param);
							}
						}
						if (!parameters.isEmpty()) {
							map.put("params", parameters);
						}
					}
					break;
				} else {
					continue;
				}
			}
		}
		return map;
	}

	@Override
	public String getTaskStatus(String rowkey) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startTask(Task task) throws RemoteException {
		try {
			SimpleQuartz.runDaily(task.getRowKey(), GROUP, task.getStartTime(),
					task.getTime(), null);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<String> getDataSource() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void runMining(Mining mining) throws RemoteException {
		try {
			Integer status = ScheduleDao.getInstance().getRunStatus(
					mining.getRowKey());
			if (status != null) {
				return;
			}
			ScheduleDao.getInstance().updateScheduleRecord(mining.getRowKey(),
					Constants.jobIsReady);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void runTask(Task task) throws RemoteException {
		try {
			Integer status = ScheduleDao.getInstance().getRunStatus(
					task.getRowKey());
			if (status != null) {
				return;
			}
			StandardTask standardTask = new StandardTask();
			standardTask.setName(task.getRowKey());
			standardTask.setStandardFile(task.getFileName());
			standardTask.setOutputPath(task.getStorePath());
			ArrayList<String> aligorithms = new ArrayList<String>();
			for (Action action : task.getActions()) {
				aligorithms.add(action.getPathName());
			}
			standardTask.setAligorithms(aligorithms);
			if (task.getTriggerType() != null) {
				Integer type = task.getTriggerType();
				if (type == 1) {
					try {
						SimpleQuartz.runDaily(task.getRowKey(), GROUP,
								task.getStartTime(), task.getTime(),
								standardTask);
					} catch (SchedulerException e) {
						e.printStackTrace();
					}
				} else if (type == 2) {
					try {
						SimpleQuartz.runWeek(task.getRowKey(), GROUP,
								task.getStartTime(), task.getWeekday(),
								standardTask);
					} catch (SchedulerException e) {
						e.printStackTrace();
					}
				} else if (type == 3) {
					try {
						SimpleQuartz.runOneTime(task.getRowKey(), GROUP,
								task.getStartTime(), standardTask);
					} catch (SchedulerException e) {
						e.printStackTrace();
					}
				}
			}
			ScheduleDao.getInstance().updateScheduleRecord(task.getRowKey(),
					Constants.jobIsReady);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Integer getRunStatus(String rowKey) throws RemoteException {
		try {
			return ScheduleDao.getInstance().getRunStatus(rowKey);
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public void deleteJob(String rowKey) throws RemoteException {
		if (rowKey == null || "".equals(rowKey.trim())) {
			return;
		}
		try {
			SimpleQuartz.removeJob(rowKey, GROUP);
		} catch (SchedulerException e) {
			throw new RemoteException(e.getMessage());
		}
	}

	public void removeRecord(String rowKey) throws RemoteException {
		if (rowKey == null || "".equals(rowKey.trim())) {
			return;
		}
		try {
			SimpleQuartz.removeJob(rowKey, GROUP);
			ScheduleDao.getInstance().deleteRow(rowKey);
		} catch (IOException e) {
			throw new RemoteException(e.getMessage());
		} catch (SchedulerException e) {
			throw new RemoteException(e.getMessage());
		}
	}

	public static void main(String[] args) throws RemoteException {
		DataUtil dataUtil = new DataUtil();
		Map<String, Object> map = new HashMap<String, Object>();
		map = dataUtil.getAlgorithmMsg("DuplicateRemovalSimpleAlgorithm");
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}
	}
}
