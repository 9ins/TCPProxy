package org.chaostocosmos.net.tcpproxy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.chaostocosmos.net.tcpproxy.config.Config;
import org.chaostocosmos.net.tcpproxy.config.ConfigHandler;
import org.chaostocosmos.net.tcpproxy.config.SessionMapping;

/**
 * 
 * ProxySessionHandler
 *
 * @author 9ins
 * 2020. 11. 19.
 */
public class ProxySessionHandler {
	
	boolean isDone = false;
	ConfigHandler configHandler;
	ServerSocket proxyServer;
	ProxyThreadPool proxyThreadPool;
	Map<String, ProxySession> sessionMap;
	Logger logger;
	
	/**
	 * Constructor
	 * @param proxy
	 * @throws FileNotFoundException 
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public ProxySessionHandler(ConfigHandler configHandler, ProxyThreadPool proxyThreadPool) throws FileNotFoundException {
		this.configHandler = configHandler;
		this.proxyThreadPool = proxyThreadPool;
		this.sessionMap = new HashMap<>();
		this.logger = Logger.getInstance();
		initProxySessions(this.configHandler.getConfig());
	}
	
	/**
	 * Create proxy sessions
	 * @param config
	 */
	public void initProxySessions(Config config) {
		for(String sessionName : config.getSessionMapping().keySet()) {
			this.sessionMap.put(sessionName, createProxySession(sessionName, config.getSessionMapping(sessionName)));
		}
	}

	/**
	 * Create proxy session
	 * @param sessionName
	 * @param sessionMapping
	 * @return
	 */
	public ProxySession createProxySession(String sessionName, SessionMapping sessionMapping) {		
		ProxySession ps = new ProxySession(sessionName, sessionMapping, this.proxyThreadPool);
		return ps;
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
		closeAllSessions();
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
		this.sessionMap.get(sessionName).closeSession();
	}
	
	/**
	 * Close all sessions
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public void closeAllSessions() throws IOException, InterruptedException {
		for(ProxySession session : this.sessionMap.values()) {
			session.closeSession();
		}
	}
}
