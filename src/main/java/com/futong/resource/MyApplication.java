package com.futong.resource;

/**
 * @author Created by Andy Wang(bwboy@163.com)
 *         Create on 9/29/15.
 */

import com.futong.server.RestServer;
import org.apache.log4j.Logger;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/")
public class MyApplication extends Application {
    private static Logger log = Logger.getLogger(RestServer.class);

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<Class<?>>();
        // register root resource
        classes.add(CollectorResource.class);
        classes.add(JacksonFeature.class);
        log.info("MyApplication Class is running!!!");
        return classes;
    }
}