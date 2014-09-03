package com.missionsky.scp.dao;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.missionsky.scp.entity.Mining;
import com.missionsky.scp.entity.Parameter;
import com.missionsky.scp.entity.Task;
import com.missionsky.scp.inter.IDataUtil;

@Component
public class DataClient {
	private static IDataUtil dataUtil = null;
	
	static{
		try {
			dataUtil = (IDataUtil) Naming.lookup("rmi://localhost:8888/datautil");
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}
	
	public Map<String, String> getFiles() throws RemoteException{
		return dataUtil.getFiles();
	}
	
	public List<String> getAlgorithms() throws RemoteException{
		return dataUtil.getAlgorithms();
	}
	
	public List<String> getAlgorithmByType(Integer type) throws RemoteException{
		return dataUtil.getAlgorithmByType(type);
	}
	
	public List<Parameter> getAlgorithmParams(String algorithmName) throws RemoteException{
		return dataUtil.getAlgorithmParams(algorithmName);
	}
	
	public Map<String, Object> getAlgorithmMsg(String algorithmName) throws RemoteException{
		return dataUtil.getAlgorithmMsg(algorithmName);
	}
	
	public void runMining(Mining mining) throws RemoteException{
		dataUtil.runMining(mining);
	}

	public void runTask(Task task) throws RemoteException {
		dataUtil.runTask(task);
	}
	
	public Integer getRunStatus(String rowKey) throws RemoteException {
		return dataUtil.getRunStatus(rowKey);
	}

	public void deleteJob(String rowKey) throws RemoteException {
		dataUtil.deleteJob(rowKey);
	}

	public void removeRecord(String rowKey) throws RemoteException {
		dataUtil.removeRecord(rowKey);
	}
}
