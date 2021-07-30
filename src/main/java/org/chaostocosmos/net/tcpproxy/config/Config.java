package org.chaostocosmos.net.tcpproxy.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config implements Serializable {
	
	private String credentialPath;
	private int proxyThreadPoolCoreSize;
	private int proxyThreadPoolMaxSize;
	private int proxyThreadPoolIdleSecond;
	private int proxyThreadPoolQueueSize;
			
	private List<String> forbiddenRemote = new ArrayList<>();
	private Map<String, SessionMapping> sessionMapping = new HashMap<>();
	
	Config() {}

	public String getCredentialPath() {
		return this.credentialPath;
	}
	public void setCredentialPath(String credentialPath) {
		this.credentialPath = credentialPath;
	}
	public int getProxyThreadPoolCoreSize() {
		return this.proxyThreadPoolCoreSize;
	}
	public void setProxyThreadPoolCoreSize(int proxyThreadPoolCoreSize) {
		this.proxyThreadPoolCoreSize = proxyThreadPoolCoreSize;
	}
	public int getProxyThreadPoolMaxSize() {
		return this.proxyThreadPoolMaxSize;
	}
	public void setProxyThreadPoolMaxSize(int proxyThreadPoolMaxSize) {
		this.proxyThreadPoolMaxSize = proxyThreadPoolMaxSize;
	}
	public int getProxyThreadPoolIdleSecond() {
		return this.proxyThreadPoolIdleSecond;
	}
	public void setProxyThreadPoolIdleSecond(int proxyThreadPoolIdleSecond) {
		this.proxyThreadPoolIdleSecond = proxyThreadPoolIdleSecond;
	}
	public int getProxyThreadPoolQueueSize() {
		return this.proxyThreadPoolQueueSize;
	}
	public void setProxyThreadPoolQueueSize(int proxyThreadPoolQueueSize) {
		this.proxyThreadPoolQueueSize = proxyThreadPoolQueueSize;
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

	@Override
	public String toString() {
		return "{" +
			", credentialPath='" + credentialPath + "'" +
			", proxyThreadPoolCoreSize='" + proxyThreadPoolCoreSize + "'" +
			", proxyThreadPoolMaxSize='" + proxyThreadPoolMaxSize + "'" +
			", proxyThreadPoolIdleSecond='" + proxyThreadPoolIdleSecond + "'" +
			", proxyThreadPoolQueueSize='" + proxyThreadPoolQueueSize + "'" +
			", forbiddenRemote='" + forbiddenRemote + "'" +
			", sessionMapping='" + sessionMapping + "'" +
			"}";
	}
}
