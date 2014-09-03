package com.missionsky.scp.quartz;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.missionsky.scp.dataanalysis.entity.StandardTask;
import com.missionsky.scp.dataanalysis.facadeinterface.BasicTaskAssemblyLine;

public class SimpleJob implements Job {
	private StandardTask task;

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		System.out.println("Hello world");
		JobDataMap dataMap = context.getTrigger().getJobDataMap();
		task = (StandardTask) dataMap.get("task");
		if(task != null){
			System.out.println("taskName:"+task.getName());
			BasicTaskAssemblyLine basicTaskAssemblyLine = new BasicTaskAssemblyLine();
			basicTaskAssemblyLine.stream(task);
		}
	}

	public StandardTask getTask() {
		return task;
	}

	public void setTask(StandardTask task) {
		this.task = task;
	}
}
