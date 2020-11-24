package org.chaostocosmos.net.tcp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config implements Serializable {
	
	private String adminUser;
	private String adminPassword;
	private String proxyHost;
	private int adminPort;
	private Map<String, SessionMapping> sessionMapping;
	
	Config() {
		
	}
	
	public String getAdminUser() {
		return adminUser;
	}
	public void setAdminUser(String adminUser) {
		this.adminUser = adminUser;
	}
	public String getAdminPassword() {
		return adminPassword;
	}
	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}
	public int getAdminPort() {
		return adminPort;
	}
	public void setAdminPort(int adminPort) {
		this.adminPort = adminPort;
	}
	public String getProxyHost() {
		return this.proxyHost;
	}
	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}
	public  Map<String, SessionMapping> getSessionMapping() {
		return sessionMapping;
	}
	public void setSessionMapping(Map<String, SessionMapping> sessionMapping) {
		this.sessionMapping = sessionMapping;
	}
	public SessionMapping getSessionMapping(String sessionName) {
		return this.sessionMapping.get(sessionName);
	}
	@Override
	public String toString() {
		return "Config [adminUser=" + adminUser + ", adminPassword=" + adminPassword + ", proxyHost=" + proxyHost
				+ ", adminPort=" + adminPort + ", sessionMapping="+ sessionMapping + "]";
	}
}
