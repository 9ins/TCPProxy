package org.chaostocosmos.net.tcpproxy;

public class TCPProxyTest {
    
    public static void main(String[] args) throws Exception {
        String configPath = "D:\\Github\\TCPProxy\\config.yml";
        TCPProxy tcpProxy = new TCPProxy(configPath);
        tcpProxy.startProxy();        
    }
}
