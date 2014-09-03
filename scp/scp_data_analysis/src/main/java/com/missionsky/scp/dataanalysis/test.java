package com.missionsky.scp.dataanalysis;

import java.util.ArrayList;

import com.missionsky.scp.dataanalysis.algorithm.dataaggregate.DuplicateRemovalSimpleAlgorithm;
import com.missionsky.scp.dataanalysis.entity.StandardTask;
import com.missionsky.scp.dataanalysis.facadeinterface.BasicTaskAssemblyLine;

public class test {
	
	public static void main(String [] args){
		
		StandardTask task = new StandardTask();
		ArrayList<String> algorithms = new ArrayList<String>();
		algorithms.add(DuplicateRemovalSimpleAlgorithm.class.getName());
		
		task.setAligorithms(algorithms);
		task.setStandardFile("standard01.xml");
		
		task.setName("test_X");
		
		BasicTaskAssemblyLine basicTaskAssemblyLine = new BasicTaskAssemblyLine();
		basicTaskAssemblyLine.stream(task);
	}
}
