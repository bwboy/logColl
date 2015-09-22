package com.futong.server;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Created by Andy Wang(bwboy@163.com)
 *         Create on 9/18/15.
 */
public class MyProperties {
    private static Properties props = new Properties();

    public MyProperties() {
        String propsFile = System.getProperty("user.dir") + "//server.properties";
        System.out.println("propsFile=" + propsFile);
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(propsFile));
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMyPropertyValue(String propsName) {
        return props.getProperty(propsName);
    }
}

