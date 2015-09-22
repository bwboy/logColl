package com.futong.server;

import org.apache.log4j.Logger;
import org.graylog2.gelfclient.GelfConfiguration;
import org.graylog2.gelfclient.GelfMessage;
import org.graylog2.gelfclient.GelfMessageBuilder;
import org.graylog2.gelfclient.GelfTransports;
import org.graylog2.gelfclient.transport.GelfTransport;

import java.net.InetSocketAddress;

/**
 * 将日志发送给graylog2
 * @author went
 *
 */
public class SendServer {
	private static final Logger log = Logger.getLogger(SendServer.class);
	private static SendServer sendServer;
	private MyProperties props = new MyProperties();
	private String targetIp = props.getMyPropertyValue("sendServerIP");
	private Integer targetPort = Integer.parseInt(props.getMyPropertyValue("sendServerPort"));
	
	public static SendServer getInstance(){
		if(sendServer == null){
			sendServer = new SendServer();
		}
		return sendServer;
	}
	
	private  GelfTransport transport;
	
	public synchronized GelfTransport getTransport(){
		return transport;
	}
	
	
	public void start() {
		log.info("启动graylog2 发送器");
		GelfConfiguration config = new GelfConfiguration(new InetSocketAddress(targetIp, targetPort))
           .transport(GelfTransports.TCP)
           .queueSize(512)
           .connectTimeout(5000)
           .reconnectDelay(1000)
           .tcpNoDelay(true)
           .sendBufferSize(32768);

		transport = GelfTransports.create(config);
	}
	
	public GelfMessage getMessage(String msg,String hostIp,String logType){
		GelfMessage message = new GelfMessageBuilder(msg, hostIp).additionalField("logType", logType).build();
		return message;
	}


	public void stop() {
		// TODO Auto-generated method stub
		transport.stop();
		
	}

}