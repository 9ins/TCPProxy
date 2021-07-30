package org.chaostocosmos.net.tcpproxy;

import org.chaostocosmos.net.tcpproxy.config.Config;

/**
 * 
 * AdminCommand
 *
 * @author 9ins 2020. 11. 18.
 */
public class AdminCommand {
	  
	String adminUser;
	String adminPassword;
	boolean restartProxy;
	boolean restartServerSocket;
	boolean updateConfig;
	boolean closeAllSessions;
	Config config;
	boolean executeCommand;
	boolean applyImmediately;
	String commandExpr;
	Object commandValue;
	
	/**
	 * Constructor
	 * @param restartProxy
	 * @param restartServerSocket
	 * @param updateConfig
	 * @param closeAllSessions
	 * @param config
	 */
	public AdminCommand(boolean restartProxy, boolean restartServerSocket, boolean updateConfig,
			boolean closeAllSessions, Config config) {
		super();
		this.restartProxy = restartProxy;
		this.restartServerSocket = restartServerSocket;
		this.updateConfig = updateConfig;
		this.closeAllSessions = closeAllSessions;
		this.config = config;
	}

	public boolean isRestartProxy() {
		return restartProxy;
	}

	public boolean isRestartServerSocket() {
		return restartServerSocket;
	}

	public boolean isUpdateConfig() {
		return updateConfig;
	}

	public boolean isCloseAllSessions() {
		return closeAllSessions;
	}
	
	public Config getConfig() {
		return this.config;
	}
	
	public String getAdminUser() {
		return this.adminUser;
	}
	
	public String getAdminPassword() {
		return this.adminPassword;
	}
	
	public void setAdminUser(String adminUser) {
		this.adminUser = adminUser;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	public void setRestartProxy(boolean restartProxy) {
		this.restartProxy = restartProxy;
	}

	public void setUpdateConfig(boolean updateConfig, Config config) {
		this.updateConfig = updateConfig;
		this.config = config;
	}

	public void setCloseAllSessions(boolean closeAllSessions) {
		this.closeAllSessions = closeAllSessions;
	}
	
	/**
	 * Execute command  
	 * @param commandExpr like e.g.  commandExpr = "sessaionMapping.Oracle.user"  commandValue = "guest"
	 * @param commandValue
	 * @param applyImmediately
	 */
	public void executeCommand(String commandExpr, Object commandValue, boolean applyImmediately) {
		this.executeCommand = true;
		this.commandExpr = commandExpr;
		this.commandValue = commandValue;
		this.applyImmediately = applyImmediately;
	}

	@Override
	public String toString() {
		return "AdminCommand [adminUser=" + adminUser + ", adminPassword=" + adminPassword + ", restartProxy="
				+ restartProxy + ", restartServerSocket=" + restartServerSocket + ", updateConfig=" + updateConfig
				+ ", closeAllSessions=" + closeAllSessions + ", config=" + config + ", commandExpr=" + commandExpr
				+ "]";
	}
}
