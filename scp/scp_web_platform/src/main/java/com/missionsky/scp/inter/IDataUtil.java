package com.missionsky.scp.inter;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import com.missionsky.scp.entity.Mining;
import com.missionsky.scp.entity.Parameter;
import com.missionsky.scp.entity.Task;

public interface IDataUtil extends Remote {
	public Map<String, String> getFiles() throws RemoteException;

	public List<String> getAlgorithms() throws RemoteException;

	public List<Parameter> getAlgorithmParams(String algorithmName)
			throws RemoteException;

	public Map<String, Object> getAlgorithmMsg(String algorithmName) throws RemoteException;
	
	//get task schedule status by rowkey
	public String getTaskStatus(String rowkey) throws RemoteException;
	
	//start a task
	public void startTask(Task task) throws RemoteException;
	
	//return some available data resource for mining
	public List<String> getDataSource() throws RemoteException;
	
	public void runMining(Mining mining) throws RemoteException;

	public void runTask(Task task) throws RemoteException;
	
	public Integer getRunStatus(String rowKey) throws RemoteException;

	public void deleteJob(String rowKey) throws RemoteException;
	
	public void removeRecord(String rowKey) throws RemoteException;

	public List<String> getAlgorithmByType(Integer type) throws RemoteException;
}
