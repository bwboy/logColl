package com.futong.Task;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.futong.server.ProcessServer;

/**
 * 定时扫描数据库任务
 * @author went
 *
 */
public class ScanDbTask implements Job {
	private static final Logger log = Logger.getLogger(ScanDbTask.class);
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		Long start = System.currentTimeMillis();
		String tName = Thread.currentThread().getName();
		log.info("ScanDbTask's Thread is  ："+tName);
		ProcessServer processServer = ProcessServer.getInstance();
		try {
			log.info("Sync DB start at ：" + start);
			processServer.start();
			log.info("Sync DB consume ：" + (System.currentTimeMillis() - start) + " ms");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
