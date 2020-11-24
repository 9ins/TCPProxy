package org.chaostocosmos.net.tcp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * ProxySessionHandler
 *
 * @author 9ins
 * 2020. 11. 19.
 */
public class ProxySessionHandler {
	
	boolean isDone = false;
	TCPProxy proxy;
	ConfigHandler configHandler;
	ServerSocket proxyServer;
	Thread thread;
	Map<String, ProxySession> sessionMap;
	Logger logger;
	
	/**
	 * Constructor
	 * @param proxy
	 * @throws FileNotFoundException 
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public ProxySessionHandler(TCPProxy proxy) throws FileNotFoundException {
		this.proxy = proxy;
		this.configHandler = ConfigHandler.getInstance();
		this.sessionMap = new HashMap<>();
		this.logger = Logger.getInstance();
		createProxySessions(this.configHandler.getConfig());
	}
	
	/**
	 * Create proxy sessions
	 * @param config
	 */
	public void createProxySessions(Config config) {
		String proxyHost = config.getProxyHost();
		this.sessionMap = config.getSessionMapping().entrySet().stream().map(e -> {
			ProxySession ps = null;
			try {
				ps = new ProxySession(e.getKey(), config);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return new AbstractMap.SimpleEntry<String, ProxySession>(e.getKey(), ps);
		}).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
	}
	
	/**
	 * Start handler
	 */
	public void start() {
		this.sessionMap.values().forEach(p -> p.start());
	}
	
	/**
	 * Restart handler
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public void restart() throws IOException, InterruptedException {
		close();
		start();
	}
	
	/**
	 * Get interactive session map
	 * @return
	 */
	public Map<String, ProxySession> getProxySessionMap() {
		return this.sessionMap;
	}
	
	/**
	 * Close specified session
	 * @param sessionName
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public void closeSession(String sessionName) throws IOException, InterruptedException {
		this.sessionMap.get(sessionName).close();
	}
	
	/**
	 * Close all sessions
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public void closeAllSessions() throws IOException, InterruptedException {
		for(ProxySession session : this.sessionMap.values()) {
			session.close();
		}
	}
	
	/**
	 * Close session server
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public void close() throws IOException, InterruptedException {
		this.isDone = true;
		this.proxyServer.close();
		this.thread.interrupt();
		this.thread.join();
	}
}
