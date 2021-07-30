package org.chaostocosmos.net.tcpproxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.chaostocosmos.net.tcpproxy.config.ConfigHandler;
import org.chaostocosmos.net.tcpproxy.credential.CredentialsHandler;

/**
 * TCPProxy : TCP Proxy
 * 
 * Description : TCP Proxy This proxy SW relay TCP Connection between local and
 * remote. Use when you didn't connect remote directly, First, install this
 * proxy at relay host(able to connect to remote) Second, This Proxy must have
 * 'mapping.properties' file.(which program argument) edit 'mapping.properties'
 * file what you want to mapping to remote host. Third, connect relay host port
 * from your host. finally enjoy to benefit. when you might ask for something,
 * Don't hesitate to ask by e-mail.(chaos930@gmail.com)
 * 
 * Modification Information --------- --------- -------------------------------
 * 20180627 9ins First draft 20201118 9ins Modify configuration to operate into
 * yaml style. And improve functionalities.
 * 
 * @author 9ins
 * @since 20180627
 * @version 1.0 *
 * @copyright All right reserved by Author
 * @email chaos930@gmail.com
 */
public class TCPProxy implements AdminCommandListener {

	public static Path configPath;
	public static Path credentialPath;

	ConfigHandler configHandler;
	CredentialsHandler credentialsHandler;
	boolean isDone = false;
	Logger logger = Logger.getInstance();
	ServerSocket proxyServer;
	ServerSocket adminServer;
	List<AdminCommandListener> configChangeListeners;
	Map<String, ProxySession> sessionMap;
	ProxySessionHandler sessionHandler;
	ProxyThreadPool proxyThreadPool;

	/**
	 * Constructor
	 * 
	 * @param mappingFilename
	 * @throws Exception
	 */
	public TCPProxy(String configPath_) throws Exception {
		logger.info(SymbolMark.TCPPROXY_MARK);
		configPath = Paths.get(configPath_);
		this.configHandler = ConfigHandler.getInstance(configPath);
		credentialPath = Paths.get(this.configHandler.getConfig().getCredentialPath());
		this.credentialsHandler = CredentialsHandler.getInstance(credentialPath);
		this.proxyThreadPool = new ProxyThreadPool(this.configHandler.getConfig().getProxyThreadPoolCoreSize(), 
		this.configHandler.getConfig().getProxyThreadPoolMaxSize(), 
		this.configHandler.getConfig().getProxyThreadPoolIdleSecond(), 
		this.configHandler.getConfig().getProxyThreadPoolQueueSize());
		logger.info("[Thread-Pool] Thread Pool initialized. Core size: "+this.configHandler.getConfig().getProxyThreadPoolCoreSize()
														+" Max size: "+this.configHandler.getConfig().getProxyThreadPoolMaxSize()
														+" Idle size: "+this.configHandler.getConfig().getProxyThreadPoolIdleSecond()
														+" Waiting Queue size: "+this.configHandler.getConfig().getProxyThreadPoolQueueSize()
														);
		this.sessionHandler = new ProxySessionHandler(this.configHandler, this.proxyThreadPool);
		this.sessionMap = this.sessionHandler.getProxySessionMap();
	}
	
	/**
	 * Restart proxy
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	private void restartProxy() throws IOException, InterruptedException {
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
	}
	
	/**
	 * Close server socket of TCPProxy
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void closeServer() throws IOException, InterruptedException {
		this.sessionHandler.closeAllSessions();
	}

	/**
	 * Close all session
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public void closeAllSession() throws IOException, InterruptedException {
		this.sessionHandler.closeAllSessions();
	}

	/**
	 * Get configHandler object
	 * @return
	 */
	public ConfigHandler getConfigHandler() {
		return this.configHandler;
	}

	/**
	 * Get credentials object
	 * @return
	 */
	public CredentialsHandler getCredentialsHandler() {
		return this.credentialsHandler;
	}

	/**
	 * Get proxy session handler
	 * @return
	 */
	public ProxySessionHandler getProxySessionHandler() {
		return this.sessionHandler;
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
	 * @param configHandler
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
