package com.futong.Task;

import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.futong.conn.SSHConn;
import com.futong.dao.BaseDao;
import com.futong.server.SendServer;
import com.futong.utils.ConstantUtils;
import org.apache.log4j.Logger;
import org.graylog2.gelfclient.transport.GelfTransport;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * 日志采集任务
 * @author went
 * 
 * 遗留问题：应该根据日志的logType来执行不同采集策略。
 *
 */
public class LogCollecterTask implements Job {
	private static final Logger log = Logger.getLogger(LogCollecterTask.class);
	private SSHConn conn;
	private BaseDao dao;
	//private LogParser parser; TODO 如果需要采集端进行解析，则配置不同的parser进行解析即可，应该另起线程解析，不要用采集线程。
	private SendServer sender;
	private String logName;
	private String logType;
	private long lastCount;
	private long lastModify;//暂时没用
	private String hostIp;
	
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
		if(logName == null || logType == null || sender == null || conn == null || hostIp == null || dao == null){
			init(context);
		}
		log.info("Collect Job:" + hostIp +"-"+logName +" Started!");
		this.lastCount = dao.getLastCount(hostIp,logName);
		String cmd_count_size = "wc -l -c " + logName;
		
		try {
			long currCount = 0;
			long currSize = 0;
			String result = execRemoteCommand(cmd_count_size,true);
			String[] arr = result.split(" ");
			log.debug("There may be cause a problem , the arr is " + Arrays.toString(arr) + " And the arr lenth is " + arr.length);
			if(arr.length==5){
				 currCount = Long.parseLong(arr[2]);
				 currSize = Long.parseLong(arr[3]);

			}else if(arr.length==6)
			{
				 currCount = Long.parseLong(arr[3]);
				 currSize = Long.parseLong(arr[4]);
			}else{
				log.warn("New condition! The output size is "+arr.length+",and arr content is "+Arrays.toString(arr));
			}
			log.info("The log's count is:" + currCount+". The log's size is: "+ currSize+".");
			dao.updateCurr(logName,hostIp,currCount,currSize,System.currentTimeMillis());
			long diff = currCount > lastCount ? currCount - lastCount : 0;
			
			String command_tail = "head -n " + currCount + " " + logName + " | tail -n " + diff;
			execRemoteCommand(command_tail,false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}


	private void init(JobExecutionContext context) {
		log.info("Initialize the collector!");
		JobDataMap map = context.getJobDetail().getJobDataMap();
		this.logName = map.getString(ConstantUtils.LOGNAME);
		this.logType = map.getString(ConstantUtils.LOGTYPE);
		this.sender = (SendServer) map.get(ConstantUtils.SENDER);
		this.conn = (SSHConn) map.get(ConstantUtils.SSHCONN);
		this.hostIp = conn.getConn().getHostname();
		this.dao = new BaseDao();
		log.info("----------------Collect Done-------------------");
	}
	
	public String execRemoteCommand(String command,boolean isReturn) {
		// 一个链接为一个session
		Session ssh = null;
		String line = null;
		BufferedReader buff = null;
		long begin = System.currentTimeMillis();
		int count = 0;
		try {
				ssh = conn.getConn().openSession();
				ssh.execCommand(command);
				InputStream is = new StreamGobbler(ssh.getStdout());
				buff = new BufferedReader(new InputStreamReader(is));
				if(!isReturn){
					GelfTransport transport = sender.getTransport();
					while ((line = buff.readLine()) != null) {
						//发送到graylog
						transport.send(sender.getMessage(line, hostIp,logType));
						count++;
						if(count%50000 == 0){
							log.info("sending count:" + count);
						}	
					}
				}else{
					while ((line = buff.readLine()) != null) {
						log.info("lines is :"+line);
						return line;
					}
				}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
				if(buff != null){
					buff.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			ssh.close();
			log.info("RUNNING END,Close the connection");
			long end = System.currentTimeMillis();
			log.info("RUNNING Time is :" + (end - begin) + "ms"+",total:" + count + " lines.");
		}
		return line;
	}
}
