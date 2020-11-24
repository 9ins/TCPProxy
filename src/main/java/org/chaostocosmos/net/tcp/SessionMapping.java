package org.chaostocosmos.net.tcp;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionMapping implements Serializable {
	public String password;
	public List<String> allowedHosts;
	public int proxyPort;
	public String remoteHost;
	public int remotePort;
	public boolean tcpNoDelay;
	public boolean keepAlive;
	public int soTimeout;
	public int sendBufferSize;
	public int receiveBufferSize;
	
	SessionMapping() {}
	
	public Map<String, Object> getSessionMappingMap() throws IllegalArgumentException, IllegalAccessException {
		Map<String, Object> map = new HashMap<String, Object>();
		Field[] fields = this.getClass().getDeclaredFields();
		for(Field f : fields) {
			map.put(f.getName(), f.get(this));
		}
		return map;
	}
	
	public SessionMapping(List<String> allowedHosts, int proxyPort, String remoteHost, int remotePort, boolean tcpNoDelay, boolean keepAlive, int soTimeout, int sendBufferSize, int receiveBufferSize) {
		super();
		this.allowedHosts = allowedHosts;
		this.proxyPort = proxyPort;
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
		this.tcpNoDelay = tcpNoDelay;
		this.keepAlive = keepAlive;
		this.soTimeout = soTimeout;
		this.sendBufferSize = sendBufferSize;
		this.receiveBufferSize = receiveBufferSize;
	}
	
	public boolean isAllowedHost(String host) {
		return this.allowedHosts.stream().anyMatch(h -> h.equals(host));
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<String> getAllowedHosts() {
		return allowedHosts;
	}

	public void setAllowedHosts(List<String> allowedHosts) {
		this.allowedHosts = allowedHosts;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	public boolean isTcpNoDelay() {
		return tcpNoDelay;
	}

	public void setTcpNoDelay(boolean tcpNoDelay) {
		this.tcpNoDelay = tcpNoDelay;
	}

	public boolean isKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}

	public int getSoTimeout() {
		return soTimeout;
	}

	public void setSoTimeout(int soTimeout) {
		this.soTimeout = soTimeout;
	}

	public int getSendBufferSize() {
		return sendBufferSize;
	}

	public void setSendBufferSize(int sendBufferSize) {
		this.sendBufferSize = sendBufferSize;
	}

	public int getReceiveBufferSize() {
		return receiveBufferSize;
	}

	public void setReceiveBufferSize(int receiveBufferSize) {
		this.receiveBufferSize = receiveBufferSize;
	}

	@Override
	public String toString() {
		return "SessionMapping [password=" + password + ", allowedHosts=" + allowedHosts
				+ ", proxyPort=" + proxyPort + ", remoteHost=" + remoteHost + ", remotePort=" + remotePort
				+ ", tcpNoDelay=" + tcpNoDelay + ", keepAlive=" + keepAlive + ", soTimeout=" + soTimeout
				+ ", sendBufferSize=" + sendBufferSize + ", receiveBufferSize=" + receiveBufferSize + "]";
	}
}
