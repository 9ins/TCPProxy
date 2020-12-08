package org.chaostocosmos.net.tcpproxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
/**  
 * TCPProxy : TCP Proxy 
 * 
 * Description :
 * TCP Proxy 
 * This proxy SW relay TCP Connection between local and remote.
 * Use when you didn't connect remote directly,
 * First, install this proxy at relay host(able to connect to remote)
 * Second, This Proxy must have 'mapping.properties' file.(which program argument) 
 * edit 'mapping.properties' file what you want to mapping to remote host.
 * Third, connect relay host port from your host.
 * finally enjoy to benefit. 
 * when you might ask for something, Don't hesitate to ask by e-mail.(chaos930@gmail.com)
 *  
 * Modification Information  
 *  ---------   ---------   -------------------------------
 *  20180627	9ins		First draft
 *  20201118	9ins		Modify configuration to operate into yaml style. And improve functionalities.
 *  
 * @author 9ins
 * @since 20180627
 * @version 1.0 * 
 * @copyright All right reserved by Author
 * @email chaos930@gmail.com
 */
public class TCPProxy implements AdminCommandListener {	

	public static Path configPath;
	boolean isDone = false;
	Logger logger = Logger.getInstance();
	ServerSocket proxyServer;
	ServerSocket adminServer;
	List<AdminCommandListener> configChangeListeners;
	ProxySessionHandler sessionHandler;
	AdminServerHandler adminHandler;
	Map<String, ProxySession> sessionMap;
	
	/**
	 * Constructor
	 * @param mappingFilename
	 * @throws IOException
	 */
	public TCPProxy(String configPath) throws IOException {
		logger.info(SymbolMark.TCPPROXY_MARK);
		this.configPath = Paths.get(configPath);
		this.sessionHandler = new ProxySessionHandler(this);
		this.adminHandler = new AdminServerHandler(this);
		this.sessionMap = this.sessionHandler.getProxySessionMap();
	}
	
	/**
	 * Restart proxy
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	private void restartProxy() throws IOException, InterruptedException {
		closeAllSession();
		closeServer();
		startProxy();
	}
	
	/**
	 * Start TCPProxy
	 * @throws UnknownHostException 
	 */
	public void startProxy() throws UnknownHostException {
		Logger.getInstance().info("Start TCP Proxy... Local Host : "+InetAddress.getLocalHost());
		this.sessionHandler.start();
		this.adminHandler.start();
	}
	
	/**
	 * Close server socket of TCPProxy
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void closeServer() throws IOException, InterruptedException {
		this.sessionHandler.close();
		this.adminHandler.close();
	}

	/**
	 * Close all session
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public void closeAllSession() throws IOException, InterruptedException {
		for(ProxySession session : this.sessionMap.values()) {
			session.closeAllSessions();
		}
	}
	
	/**
	 * Convert byte array to Hex string
	 * @param a
	 * @return
	 */
	public static String byteArrayToHex(byte[] a) {
	   StringBuilder sb = new StringBuilder(a.length * 2);
	   for(byte b: a) {
		   sb.append(String.format("%02x", b));
	   }
	   return sb.toString();
	}
	
	/**
	 * Add config change listener
	 * @param listener
	 */
	public void addConfigChangeListener(AdminCommandListener listener) {
		if(!this.configChangeListeners.contains(listener)) {
			this.configChangeListeners.add(listener);
		}
	}
	
	/**
	 * Remove config change listener
	 * @param listener
	 */
	public void removeConfigChangeListener(AdminCommandListener listener) {
		this.configChangeListeners.remove(listener);
	}
	
	/**
	 * Dispatch config change event
	 * @param config
	 */
	public void dispachConfigChangeEvent(AdminCommand cmd) {
		this.configChangeListeners.stream().forEach(l -> l.receiveConfigChangeEvnet(new AdminCommandEvent(this, cmd)));
	}
	
	@Override
	public void receiveConfigChangeEvnet(AdminCommandEvent ace) {
		try {
			if(ace.getAdminCommand().isRestartProxy()) {
				restartProxy();
			} else if(ace.getAdminCommand().isRestartServerSocket()) {
				this.sessionHandler.restart();
				this.adminHandler.restart();
			}
		} catch(Exception e) {
			Logger.getInstance().throwable(e);
		}
	}

	/**
	 * Main
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {
		if(args.length == 1) {			
			TCPProxy proxy = new TCPProxy(args[0]);
			proxy.startProxy();
		} else {
			System.out.println("Useage : TCP Proxy must have one program parameter which name is 'config.yml'");
		}
	}
}
