package com.missionsky.scp.quartz;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.CronScheduleBuilder.weeklyOnDayAndHourAndMinute;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

import com.missionsky.scp.dataanalysis.entity.StandardTask;

public class SimpleQuartz {

	private static SimpleDateFormat sdf = new SimpleDateFormat("mm HH");

	public static void runOneTime(String name, String group, Date date,
			StandardTask task) throws SchedulerException {
		SchedulerFactory schedFact = new StdSchedulerFactory();
		Scheduler sched = schedFact.getScheduler();
		sched.start();
		JobDetail job = newJob(SimpleJob.class).withIdentity(name, group)
				.build();
		JobDataMap dataMap = new JobDataMap();
		dataMap.put("task", task);
		SimpleTrigger trigger = (SimpleTrigger) newTrigger()
				.withIdentity("trigger", "group").startAt(date).forJob(job)
				.usingJobData(dataMap).build();
		sched.scheduleJob(job, trigger);
	}

	public static void runDaily(String name, String group, Date date,
			Integer separate, StandardTask task) throws SchedulerException {
		if (date == null) {
			return;
		}
		SchedulerFactory schedFact = new StdSchedulerFactory();
		Scheduler sched = schedFact.getScheduler();
		sched.start();
		JobDetail job = newJob(SimpleJob.class).withIdentity(name, group)
				.build();
		String cron = "";
		if (separate == null || separate == 0 || separate == 1) {
			cron = "0 " + sdf.format(date) + " * * ?";
		} else {
			cron = "0 " + sdf.format(date) + " 0/" + separate + " * ?";
		}
		JobDataMap dataMap = new JobDataMap();
		dataMap.put("task", task);
		Trigger trigger = newTrigger().withIdentity("trigger", "group")
				.withSchedule(cronSchedule(cron)).forJob(job)
				.usingJobData(dataMap).build();
		sched.scheduleJob(job, trigger);
	}

	public static void runWeek(String name, String group, Date date,
			Integer weekDay, StandardTask task) throws SchedulerException {
		if (date == null || weekDay == null || weekDay > 7 || weekDay < 0) {
			return;
		}
		SchedulerFactory schedFact = new StdSchedulerFactory();
		Scheduler sched = schedFact.getScheduler();
		sched.start();
		String str = sdf.format(date);
		String[] strs = str.split(" ");
		JobDetail job = newJob(SimpleJob.class).withIdentity(name, group)
				.build();
		JobDataMap dataMap = new JobDataMap();
		dataMap.put("task", task);
		Trigger trigger = newTrigger()
				.withIdentity("trigger", "group")
				.withSchedule(
						weeklyOnDayAndHourAndMinute(weekDay,
								Integer.valueOf(strs[1]),
								Integer.valueOf(strs[0])))
				.usingJobData(dataMap).forJob(job).build();
		sched.scheduleJob(job, trigger);
	}

	/**
	 * delete a job
	 * 
	 * @param name
	 * @param group
	 * @throws SchedulerException
	 */
	public static void removeJob(String name, String group)
			throws SchedulerException {
		SchedulerFactory schedFact = new StdSchedulerFactory();
		Scheduler sched = schedFact.getScheduler();
		TriggerKey triggerKey = new TriggerKey("trigger", "group");
		sched.pauseTrigger(triggerKey);
		sched.unscheduleJob(triggerKey);
		JobKey jobKey = new JobKey(name, group);
		sched.deleteJob(jobKey);
	}
}
