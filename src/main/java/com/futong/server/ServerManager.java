package com.futong.server;

import org.apache.log4j.Logger;


/**
 * 服务管理者，负责服务的启停。
 * @author went
 *
 */
public class ServerManager {
	private static final Logger log = Logger.getLogger(ServerManager.class);
	private TaskServer taskServer;
	private SendServer sendServer;
	private RestServer restServer; //新增加的RestFul 服务
	private ProcessServer processServer;
	
	public ServerManager() {
		log.info("ServerManager is Starting~!Å");
		taskServer = TaskServer.getInstance();
		sendServer = SendServer.getInstance();
		restServer = RestServer.getInstance();
		processServer = ProcessServer.getInstance();
	}
	/**
	 * 启动所有服务
	 * taskServer
	 * SendServer
	 * restServer
	 */
	public void startAllServer() throws Exception {
		try {
			taskServer.start();
			log.info("====================Task Server Strated!====================");
			sendServer.start();
			log.info("====================Send Server Started!====================");
			restServer.start();
			log.info("====================Rest Server Started!====================");
			processServer.bootstrap();
			log.info("====================process Server has been bootstrap!");
		} catch (Exception e) {
			log.error("Server Start faild!",e);
			throw new Exception("Server is running with error!",e);
		}
		
		
	}
	public void stopAllServer() {
		
		this.taskServer.stop();
		this.sendServer.stop();
		this.restServer.stop();
		
	}

}
