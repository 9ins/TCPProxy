package org.chaostocosmos.net.tcpproxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.chaostocosmos.net.tcpproxy.config.ConfigException;
import org.chaostocosmos.net.tcpproxy.config.SESSION_MODE;
import org.chaostocosmos.net.tcpproxy.config.SessionMapping;
/**
 * 
 * ProxySocket
 *
 * @author 9ins
 * 2020. 11. 30.
 */
public class ProxySocket extends Socket {		
	int connectionTimeout;
	int sessionIndex;
	InetSocketAddress remoteAddress;
	SessionMapping sm;

	/**
	 * Constructor
	 * @param remote
	 * @param port
	 * @param sessionMode
	 * @param socketIndex
	 * @param channelRatio
	 */
	public ProxySocket(SessionMapping sm, int sessionIndex) {
		String remote = sm.getRemoteHosts().get(sessionIndex);
		if(remote.indexOf(":") == -1) {
			throw new ConfigException("remoteHosts", "Remote host must be defined like HOST:PORT format!!! Defined format is: "+remote);
		}
		String host = remote.substring(0, remote.lastIndexOf(":"));
		int port = Integer.parseInt(remote.substring(remote.lastIndexOf(":")+1));
		this.sm = sm;
		this.sessionIndex = sessionIndex;
		this.remoteAddress = new InetSocketAddress(host, port);
		this.connectionTimeout = this.sm.getConnectionTimeout();
	}
	
	public Socket connect() throws IOException {
		System.out.println(this.remoteAddress.toString());
		this.setSoTimeout(sm.getSoTimeout());
		this.setKeepAlive(sm.isKeepAlive());
		int bufferSize = sm.getBufferSize() == 0 ? 1024 : sm.getBufferSize();
		this.setReceiveBufferSize(bufferSize);
		this.setSendBufferSize(bufferSize);
		this.setTcpNoDelay(sm.isTcpNoDelay());
		this.connect(this.remoteAddress, this.connectionTimeout);
		return this;
	}

	public InetSocketAddress getRemoteAddress() {
		return this.remoteAddress;
	}
	
	public int getSessionIndex() {
		return this.sessionIndex;
	}

	public float getChannelRatio() throws ConfigException {
		if(this.sm.getSessionModeEnum() != SESSION_MODE.LOAD_BALANCE_SEPARATE_RATIO) {
			throw new ConfigException("sessionMode", "This method must use with LOAD_BALANCE_RATIO mode.");
		}
		return this.sm.getLoadBalanceRatioList().get(this.sessionIndex);
	}
}
