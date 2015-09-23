package com.futong.conn;

import ch.ethz.ssh2.Connection;
import com.futong.domain.Host;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * 远程主机连接
 *
 * @author went
 */
public class SSHConn {

    private Connection conn;
    private boolean isconn = false;
    private String ip;
    private int port;
    private String username;
    private String password;
    private static final Logger log = Logger.getLogger(SSHConn.class);

    public SSHConn(Host host) {
        ip = host.getIp();
        port = host.getPort();
        username = host.getUsername();
        password = host.getPassword();

        conn = new Connection(ip, port);
        try {
            conn.connect();
            isconn = conn.authenticateWithPassword(username, password);
            if (!isconn) {
                log.warn("SSH connection failed,please check the Hostname/Username/IP of [" + ip + "]");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Connection getConn() {
        if (!isconn) {
            try {
                conn.connect();
                isconn = conn.authenticateWithPassword(username, password);
            } catch (IOException e) {
                log.info("");
                System.out.println("Here is the Problem!");
                e.printStackTrace();
            }
        }
        return conn;
    }


}
