package org.chaostocosmos.net.tcp;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

public class Config implements Serializable {
	
	private String adminUser;
	private String adminPassword;
	private String proxyHost;
	private int adminPort;
	private List<String> forbiddenRemote;
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
	public List<String> getForbiddenRemote() {
		return forbiddenRemote;
	}

	public void setForbiddenRemote(List<String> forbiddenRemote) {
		this.forbiddenRemote = forbiddenRemote;
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
	public boolean isForbiddenHost(InetSocketAddress socketAddr) {
		return isForbiddenHost(socketAddr.getHostName(), socketAddr.getPort());
	}
	public boolean isForbiddenHost(String host, int port) {
		System.out.println("HOST NAME: "+host+"   "+port);			
		if(this.forbiddenRemote.contains(host+":"+port) || this.forbiddenRemote.contains(host+":*")) {
			return true;
		}
		return false;
	}
	@Override
	public String toString() {
		return "Config [adminUser=" + adminUser + ", adminPassword=" + adminPassword + ", proxyHost=" + proxyHost
				+ ", adminPort=" + adminPort + ", forbiddenRemote=" + forbiddenRemote + ", sessionMapping="
				+ sessionMapping + "]";
	}
}
