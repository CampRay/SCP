package com.missionsky.scp.quartz;

import java.io.IOException;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import com.missionsky.scp.dao.ScheduleDao;
import com.missionsky.scp.dataanalysis.Constants;

public class MyJobListener implements JobListener {

	@Override
	public String getName() {
		return "job_listener";
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		System.out.println("jobToBeExecuted"+context.getJobDetail().getKey().getName());
		try {
			ScheduleDao.getInstance().updateScheduleRecord(context.getJobDetail().getKey().getName(), Constants.jobToBeExecuted);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		System.out.println("jobExecutionVetoed"+context.getJobDetail().getKey().getName());
		try {
			ScheduleDao.getInstance().updateScheduleRecord(context.getJobDetail().getKey().getName(), Constants.jobExecutionVetoed);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context,
			JobExecutionException jobException) {
		System.out.println("jobWasExecuted"+context.getJobDetail().getKey().getName());
		try {
			ScheduleDao.getInstance().updateScheduleRecord(context.getJobDetail().getKey().getName(), Constants.jobWasExecuted);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
