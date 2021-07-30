package org.chaostocosmos.net.tcpproxy;

import org.junit.Before;
import org.junit.Test;

public class testTCPProxy {

    String configPath;

    @Before
    public void before_test() {
        this.configPath = "D:\\Github\\TCPProxy\\config.yml";
    }
    
    @Test
    public void test_TCPProxy() throws Exception {        
        TCPProxy tcpProxy = new TCPProxy(this.configPath);
        tcpProxy.startProxy();        
    }

    public static void main(String[] args) throws Exception {
        testTCPProxy proxy = new testTCPProxy();
        proxy.before_test();
        proxy.test_TCPProxy();
    }
}
