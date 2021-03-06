package com.futong.server;

import com.futong.resource.CollectorResource;
import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.servlet.GrizzlyWebContainerFactory;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class RestServer {
    private static Logger log = Logger.getLogger(RestServer.class);
    ConfigManager conf = ConfigManager.getInstance();
    private int port = Integer.parseInt(conf.getConfigItem("restServerPort", "null").toString());
    private URI BASE_URI;
    private static RestServer restServer;
    private HttpServer server;

    public static RestServer getInstance() {
        if (restServer != null) {
            return restServer;
        }
        return new RestServer();
    }

    private RestServer() {
            String ip = conf.getConfigItem("restIP","127.0.0.1").toString();
            BASE_URI = URI.create("http://" + ip + ":" + port + "/");
            log.info("RestServer's URL is :" + BASE_URI);
    }

    public void start() {
        log.info("RestServer start");

        Map<String, String> initParams = new HashMap<String, String>();
        initParams.put(ServerProperties.PROVIDER_PACKAGES, CollectorResource.class.getPackage().getName());
        try {
            server = GrizzlyWebContainerFactory.create(BASE_URI, ServletContainer.class, initParams);
            log.info("program runing here without problem? server"+server.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void stop() {
        server.shutdownNow();
        log.info("RestServer close");
    }
}
