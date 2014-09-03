package com.missionsky.scp.dataanalysis;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Properties;

import com.missionsky.scp.dataanalysis.utils.PropertiesUtil;

public class Main {
	
	public static void main(String [] args) throws ClassNotFoundException{
		
		//TODO RMI Server
		Properties properties = PropertiesUtil.loadPropertiesFile(Main.class.getResource("/").getPath() + "/config.properties");
		String rmiUrl = properties.getProperty("rmi.url");
		String rmiPort = properties.getProperty("rmi.port");
		StringBuffer rmiAddr = new StringBuffer();
		rmiAddr.append(rmiUrl).append(":").append(rmiPort).append("/");
		
		try {
			LocateRegistry.getRegistry(rmiPort);
			
			
			
			
			
			
			
			
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
	}
}
