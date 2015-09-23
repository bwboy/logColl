import org.apache.log4j.Logger;

import com.futong.server.ServerManager;

/**
 * 程序启动入口
 * @author went
 *
 */
public class Main {
	private static final Logger log = Logger.getLogger(Main.class);
	public static void main(String[] args) {
		log.info("采集机开始运行");
		//程序启动入口
		//1启动所有服务,如果要添加配置文件的话，在ServerManager中直接读取并缓存(没有实现)
		ServerManager manager = new ServerManager();
		try {
			log.info("====================Starting All the Serve====================");
			manager.startAllServer();
			log.info("====================Collector Started!====================");
		} catch (Exception e) {
			manager.stopAllServer();
			log.error("====================Collector Start faild!Please check the problem!。====================", e);
			System.exit(-1);
		}
		
		
	}
}
