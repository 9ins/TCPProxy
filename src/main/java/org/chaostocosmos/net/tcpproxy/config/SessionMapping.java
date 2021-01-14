package org.chaostocosmos.net.tcpproxy.config;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * SessionMapping
 *
 * @author 9ins
 * 2020. 11. 30.
 */
public class SessionMapping implements Serializable {
	/**
	 * Gen serial ID.
	 */
	private static final long serialVersionUID = -8952065662406999209L;
	
	private List<String> allowedHosts = new ArrayList<>();
	private List<String> forbiddenHosts = new ArrayList<>();
    private int threadPoolCoreSize;
    private int threadPoolMaxSize;
    private int threadPoolIdleSecond;
    private int threadPoolQueueSize;
	private boolean keepAlive;
	private boolean tcpNoDelay;
	private String proxyBindAddress;
	private int proxyPort;
	private List<String> remoteHosts = new ArrayList<>();
	private String sessionMode;
	private String loadBalanceRatio;
	private int standAloneRetry;
	private int failedCircularRetry;
	private int bufferSize;
	private int connectionTimeout;
	private int soTimeout;	
	
	private SESSION_MODE sessionModeEnum;
	private Map<String, Float> loadBalanceRatioMap;
	
	SessionMapping() {}
	
	public Map<String, Object> getSessionMappingMap() throws IllegalArgumentException, IllegalAccessException {
		Map<String, Object> map = new HashMap<String, Object>();
		Field[] fields = this.getClass().getDeclaredFields();
		for(Field f : fields) {
			map.put(f.getName(), f.get(this));
		}
		return map;
	}

	public List<String> getAllowedHosts() {
		return allowedHosts;
	}

	public void setAllowedHosts(List<String> allowedHosts) {
		this.allowedHosts = allowedHosts;
	}

	public List<String> getForbiddenHosts() {
		return forbiddenHosts;
	}

	public void setForbiddenHosts(List<String> forbiddenHosts) {
		this.forbiddenHosts = forbiddenHosts;
	}

	public int getThreadPoolCoreSize() {
		return this.threadPoolCoreSize;
	}

	public void setThreadPoolCoreSize(int threadPoolCoreSize) {
		this.threadPoolCoreSize = threadPoolCoreSize;
	}

	public int getThreadPoolMaxSize() {
		return this.threadPoolMaxSize;
	}

	public void setThreadPoolMaxSize(int threadPoolMaxSize) {
		this.threadPoolMaxSize = threadPoolMaxSize;
	}

	public int getThreadPoolIdleSecond() {
		return this.threadPoolIdleSecond;
	}

	public void setThreadPoolIdleSecond(int threadPoolIdleSecond) {
		this.threadPoolIdleSecond = threadPoolIdleSecond;
	}

	public int getThreadPoolQueueSize() {
		return this.threadPoolQueueSize;
	}

	public void setThreadPoolQueueSize(int threadPoolQueueSize) {
		this.threadPoolQueueSize = threadPoolQueueSize;
	}

	public boolean isKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}

	public boolean isTcpNoDelay() {
		return tcpNoDelay;
	}

	public void setTcpNoDelay(boolean tcpNoDelay) {
		this.tcpNoDelay = tcpNoDelay;
	}

	public String getProxyBindAddress() {
		return proxyBindAddress;
	}

	public void setProxyBindAddress(String proxyBindAddress) {
		this.proxyBindAddress = proxyBindAddress;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public List<String> getRemoteHosts() {
		return remoteHosts;
	}
	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getSoTimeout() {
		return soTimeout;
	}

	public void setSoTimeout(int soTimeout) {
		this.soTimeout = soTimeout;
	}	

	public int getStandAloneRetry() {
		return standAloneRetry;
	}

	public void setStandAloneRetry(int standAloneRetry) {
		this.standAloneRetry = standAloneRetry;
	}

	public String getSessionMode() {
		return sessionMode;
	}
	
	public String getLoadBalanceRatio() {
		return this.loadBalanceRatio;
	}

	public int getFailedCircularRetry() {
		return this.failedCircularRetry;
	}

	public void setFailedCircularRetry(int failedCircularRetry) {
		this.failedCircularRetry = failedCircularRetry;
	}

	public void setSessionModeEnum(SESSION_MODE sessionModeEnum) {
		this.sessionModeEnum = sessionModeEnum;
	}

	public void setLoadBalanceRatioMap(Map<String, Float> loadBalanceRatioMap) {
		this.loadBalanceRatioMap = loadBalanceRatioMap;
	}

	public void setRemoteHosts(List<String> remoteHosts) {
		this.remoteHosts = remoteHosts;
	}

	public void setSessionMode(String sessionMode) {
		this.sessionMode = sessionMode;
	}

	public void setLoadBalanceRatio(String loadBalanceRatio) {
		this.loadBalanceRatio = loadBalanceRatio;
	}

	public SESSION_MODE getSessionModeEnum() throws ConfigException {
		if(sessionMode.equalsIgnoreCase("SA")) {
			return SESSION_MODE.STAND_ALONE;
		} else if(sessionMode.equalsIgnoreCase("HA_FO")) {
			return SESSION_MODE.HIGI_AVAILABLE_FAIL_OVER;
		} else if(sessionMode.equalsIgnoreCase("HA_FB")) {
			return SESSION_MODE.HIGI_AVAILABLE_FAIL_BACK;
		} else if(sessionMode.equalsIgnoreCase("LB_LR")) {		
			return SESSION_MODE.LOAD_BALANCE_ROUND_ROBIN;
		} else if(sessionMode.equalsIgnoreCase("LB_SR")) {
			return SESSION_MODE.LOAD_BALANCE_SEPARATE_RATIO;
		} else {
			try {
				return SESSION_MODE.valueOf(sessionMode);
			} catch(Exception e) {
				throw new ConfigException("remoteMode", "remoteMOde must be among STAND_ALONE / HIGH_AVAILABLE_FAIL_OVER / HIGH_AVAILABLE_FAIL_BACK / LOAD_BALANCE_ROUND_ROBIN / LOAD_BALANCE_SEPARATE_RATIO.");
			}
		}
	}

	/**
	 * Get load balance ratio list.
	 * @return
	 */
	public List<Float> getLoadBalanceRatioList() {
		if(loadBalanceRatio == null || remoteHosts.size() == 0 || remoteHosts.size() != loadBalanceRatio.split(":").length) {
			throw new ConfigException("loadBalanceRatio", "It must be same count between remote hosts count and Load-Balanced Ratio tokens in LOAD_BALANCE_SEPARATE_RATIO mode.");
		}
		return Arrays.asList(loadBalanceRatio.split(":")).stream().map(f -> Float.parseFloat(f)).collect(Collectors.toList());
	}

	/**
	 * Get loadbalance ratio map on mapping with remote host.
	 * @return
	 */
	public Map<String, Float> getLoadBalanceRatioMap() {
		Map<String, Float> map = new HashMap<>();
		List<Float> ratioList = Arrays.asList(loadBalanceRatio.split(":")).stream().map(f -> Float.parseFloat(f)).collect(Collectors.toList());
		float sum = 0.0f;
		for(Float f : ratioList) {
			sum += f;
			if(sum > 100f) {
				f = 100f - (sum - f);
			}
			if(remoteHosts.size() > 0) {
				String remote = remoteHosts.remove(0);
				map.put(remote, f);
			}
		}
		if(remoteHosts.size() > 0) {
			for(String remote : remoteHosts) {
				map.put(remote, 0f);
			}
		}
		return map;
	}

	public boolean isForbiddenHost(InetSocketAddress socketAddr) {
		return isForbiddenHost(socketAddr.getHostName(), socketAddr.getPort());
	}
	public boolean isForbiddenHost(String hostPort) {
		return isForbiddenHost(hostPort.substring(0, hostPort.lastIndexOf(":")), Integer.parseInt(hostPort.substring(hostPort.lastIndexOf(":")+1)));
	}
	public boolean isForbiddenHost(String host, int port) {
		System.out.println("HOST NAME: "+host+"   "+port);			
		if(this.forbiddenHosts.contains(host+":"+port) || this.forbiddenHosts.contains(host+":*")) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "{" +
			" allowedHosts='" + allowedHosts + "'" +
			", forbiddenHosts='" + forbiddenHosts + "'" +
			", threadPoolCoreSize='" + threadPoolCoreSize + "'" +
			", threadPoolMaxSize='" + threadPoolMaxSize + "'" +
			", threadPoolIdleSecond='" + threadPoolIdleSecond + "'" +
			", threadPoolQueueSize='" + threadPoolQueueSize + "'" +
			", keepAlive='" + keepAlive + "'" +
			", tcpNoDelay='" + tcpNoDelay + "'" +
			", proxyBindAddress='" + proxyBindAddress + "'" +
			", proxyPort='" + proxyPort + "'" +
			", remoteHosts='" + remoteHosts + "'" +
			", sessionMode='" + sessionMode + "'" +
			", loadBalanceRatio='" + loadBalanceRatio + "'" +
			", standAloneRetry='" + standAloneRetry + "'" +
			", failedCircularRetry='" + failedCircularRetry + "'" +
			", bufferSize='" + bufferSize + "'" +
			", connectionTimeout='" + connectionTimeout + "'" +
			", soTimeout='" + soTimeout + "'" +
			", sessionModeEnum='" + sessionModeEnum + "'" +
			", loadBalanceRatioMap='" + loadBalanceRatioMap + "'" +
			"}";
	}

}
