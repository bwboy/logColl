package com.futong.server;

/**
 * @author Created by Andy Wang(bwboy@163.com)
 *         Create on 9/23/15.
 */

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class ConfigManager {
    private static final Logger log = Logger.getLogger(ConfigManager.class);
    private static final String configFile = System.getProperty("user.dir") + "//server.properties";
    private File m_file = null;
    private long m_lastModifiedTime = 0;
    private Properties m_props = null;
    private static ConfigManager m_instance  = new ConfigManager();
    private ConfigManager(){
        m_file = new File(configFile);
        m_lastModifiedTime = m_file.lastModified();
        if (m_lastModifiedTime == 0){
            log.info(configFile + " file does not exist,Please Check it first!");
        }
        m_props = new Properties();
        try {
            m_props.load(new FileInputStream(configFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    synchronized public static ConfigManager getInstance(){
        return m_instance;
    }
    public final Object getConfigItem(String name,Object defaultVal){
        long newTime = m_file.lastModified();
        if (newTime ==0 ){
            if(m_lastModifiedTime == 0){
                log.info(configFile+" file does not exist!");
            }else {
                log.info(configFile+" file was deleted!");
            }
            return defaultVal;
        }else if (newTime>m_lastModifiedTime){
            m_props.clear();
            try {
                m_props.load(new FileInputStream(configFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        m_lastModifiedTime = newTime;
        Object val = m_props.getProperty(name);
        if (val == null){
            return defaultVal;
        }else{
            return val;
        }
    }
}
