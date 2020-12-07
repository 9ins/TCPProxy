package org.chaostocosmos.net.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * ProxySession
 *
 * @author 9ins
 * 2020. 11. 18.
 */
class ProxySession implements Runnable {
	
	boolean isDone = false;
	String sessionName;
	Thread thread;
	Config config;
	SessionMapping sessionMapping;
	Logger logger;
	Map<String, InteractiveChannel> sendChannelMap;
	Map<String, InteractiveChannel> receiveChannelMap;
	
	/**
	 * Constructor
	 * @param proxy
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public ProxySession(String sessionName, Config config) throws IOException, InterruptedException {
		this.sessionName = sessionName;
		this.config = config;
		this.sessionMapping = config.getSessionMapping(sessionName);
		this.sendChannelMap = new HashMap<String, InteractiveChannel>();
		this.receiveChannelMap = new HashMap<String, InteractiveChannel>();
		this.logger = Logger.getInstance();
	}
	
	/**
	 * Apply socket configuration
	 * @param socket
	 * @param sm
	 * @return
	 * @throws IOException 
	 * @throws ConfigException 
	 */
	public static List<ProxySocket> createProxySocket(SessionMapping sm) throws IOException, ConfigException {
		List<ProxySocket> socketList = new ArrayList<ProxySocket>();
		for(int i=0; i<sm.getRemoteHosts().size(); i++) {
			String remote = sm.getRemoteHosts().get(i);
			if(remote.indexOf(":") == -1) {
				throw new ConfigException("remoteHosts", "Remote host must be defined like HOST:PORT format!!! Defined format is: "+remote);
			}
			String host = remote.substring(0, remote.lastIndexOf(":"));
			int port = Integer.parseInt(remote.substring(remote.lastIndexOf(":")+1));
			float channelRatio = sm.getSessionMode() == SESSION_MODE.LOAD_BALANCE_SEPARATE_RATIO ? sm.getLoadBalanceRatioMap().get(remote) : 0f;
			
			ProxySocket socket = new ProxySocket(host, port, sm.getSessionMode(), i, channelRatio);
			socket.bind(new InetSocketAddress(sm.getProxyBindAddress(), sm.getProxyPort()));
			socket.setSoTimeout(sm.getSoTimeout());
			socket.setKeepAlive(sm.isKeepAlive());
			int bufferSize = sm.getBufferSize() == 0 ? 1024 : sm.getBufferSize();
			socket.setReceiveBufferSize(bufferSize);
			socket.setSendBufferSize(bufferSize);
			socket.setTcpNoDelay(sm.isTcpNoDelay());
			socketList.add(socket);
		}
		return socketList;
	}
	
	/**
	 * Start
	 */
	public void start() {
		this.thread = new Thread(this);
		this.thread.start();
	}
	
	/**
	 * Close
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public void closeSessions() throws IOException, InterruptedException {
		for(int i=0; i<sendChannelMap.size(); i++) {
			sendChannelMap.get(i).close();
			receiveChannelMap.get(i).close(); 
		}
	}
	
	/**
	 * Close Socket
	 * @param socket
	 * @param msg
	 * @throws IOException
	 */
	public void closeSocket(Socket socket, String msg) throws IOException {
		if(socket != null) {
			OutputStream os = socket.getOutputStream();
			os.write(msg.getBytes());
			os.flush();
			os.close();
			socket.close();
		}
	}
	
	/**
	 * Get session 
	 * @return
	 */
	public String getSessionName() {
		return this.sessionName;
	}
	
	/**
	 * Set session
	 * @param sessionName
	 */
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}
	
	boolean ha_fail_over_flag = false;
	
	@Override
	public void run() {
		logger.info("["+sessionName+"] Proxy server waiting [Port] : "+sessionMapping.getProxyPort()+"   Target: "+sessionMapping.getRemoteHosts().toString());
		ServerSocket proxyServer = null;
		try {
			proxyServer = new ServerSocket(this.sessionMapping.getProxyPort(), 100, InetAddress.getByName(this.config.getProxyHost()));
		} catch (IOException e) {
			logger.throwable(e);
		}
		while(!this.isDone) {
			try {
				Socket client = proxyServer.accept();
				if(sessionMapping.getRemoteHosts().stream().anyMatch(h -> config.isForbiddenHost(h))) {
					String err = "Forbidden remote host request detected. Forbidden List: "+sessionMapping.getRemoteHost()s.toString();
					logger.info(err);
					closeSocket(client, err);
				} else {
					if(sessionMapping.getAllowedHosts().size() == 0 || sessionMapping.getAllowedHosts().stream().anyMatch(h -> h.equals(client.getLocalAddress().getHostAddress()))) {						
						List<Socket> remoteSockets = createProxySocket(sessionMapping);
						Socket remote = null;
						String remoteHost = null;
						String host = null;
						int port = -1;
						switch(sessionMapping.getSessionMode()) {
							case STAND_ALONE :
								if(remoteSockets.size() != 1) {
									throw new ConfigException(sessionMapping.getSessionMode().name(), "["+sessionName+"] Stand-Alone session must have just one remote host. Check the seesion configuration in config yaml!!!");
								}
								remote = remoteSockets.get(0);
								remoteHost = sessionMapping.getRemoteHost().get(0);
								host = remoteHost.substring(0, remoteHost.lastIndexOf(":"));
								port = Integer.parseInt(remoteHost.substring(remoteHost.lastIndexOf(":")+1));
								int retry = sessionMapping.getRemoteRetry();
								for(int i=0; i<retry; i++) {
									try {
										remote.connect(new InetSocketAddress(host, port), sessionMapping.getConnectionTimeout());
										break;
									} catch(Exception e) {
										logger.info("["+sessionName+"] Retry session... Interval: 3000 milliseconds.");
									}
									Thread.sleep(3000);
								}
								break;
							case HA_FAIL_OVER :
								if(remoteSockets.size() != 2) {
									throw new ConfigException(sessionMapping.getSessionMode().name(), "["+sessionName+"] HA Fail Over session must have just two remote host. Check the seesion configuration in config yaml!!!");
								}
								if(!ha_fail_over_flag) {
									remote = remoteSockets.get(0);
									remoteHost = sessionMapping.getRemoteHost().get(0);
									host = remoteHost.substring(0, remoteHost.lastIndexOf(":"));
									port = Integer.parseInt(remoteHost.substring(remoteHost.lastIndexOf(":")+1));
									try {
										remote.connect(new InetSocketAddress(host, port), sessionMapping.getConnectionTimeout());
									} catch(Exception e) {
										ha_fail_over_flag = true;
										logger.info("["+sessionName+"] ");
									}
								} else {
									
								}								
								break;
							case HA_FAIL_BACK :
								break;
							//case LOAD_BALANCE_ROUND_ROBIN :
							//	break;
							//case LOAD_BALANCE_RATIO :
							//	break;
								
							default:
						}
						//sendChannelMap.put(clientSocket.getRemoteSocketAddress().toString(), new InteractiveChannel(sessionName+"-Send", clientSocket, remoteSocket));
						//receiveChannelMap.put(clientSocket.getRemoteSocketAddress().toString(), new InteractiveChannel(sessionName+"-Receive", remoteSocket, clientSocket));
						//logger.info("["+sessionName+"] Session channels(send/receive) is managed. Client: "+clientSocket.getRemoteSocketAddress().toString());
					} else {
						//String err = "Not allowed host connected: "+socket.getLocalAddress().getHostAddress();
						//logger.info(err);
						//closeSocket(socket, err);
					}
				}
			} catch(Exception e) {
				logger.throwable(e);
			}
		}
	}

	/**
	 * InteractiveThread
	 *
	 * @author 9ins
	 * 2020. 11. 19.
	 */
	public class InteractiveChannel extends Thread {
		
		boolean isDone;
		String channelName;
		Socket source, target;
		InputStream is;
		OutputStream os;
		
		public InteractiveChannel(String channelName, Socket source, Socket target) {
			this.channelName = channelName;
			this.source = source;
			this.target = target;
			start();
		}
		
		@Override
		public void run() {			
			try {
				logger.info("["+this.channelName+"] Channel Open. Target: "+this.target.getRemoteSocketAddress());
				is = source.getInputStream();
				os = target.getOutputStream();
				int bufferSize = sessionMapping.getBufferSize() == 0 ? 1024 : sessionMapping.getBufferSize();
				byte[] buffer = new byte[bufferSize];
				int read; 
				long total = 0;
				while((read = is.read(buffer)) > 0) {
					if(total < 1024) {
						System.out.println(new String(buffer));
					}
					os.write(buffer, 0, read);
					total += read;
				}				
				os.flush();
			} catch(SocketException se) {
				logger.debug(se.getMessage());
			} catch (Exception e) {
				logger.throwable(e);
			} finally {
				try {
					close();
				} catch(Exception e) {
					logger.throwable(e);
				}
			}
		}
		
		public void close() throws IOException, InterruptedException {
			this.isDone = true;
			if(is != null) is.close();
			if(is != null) os.close();
			if(source != null) source.close();
			if(target != null) target.close();
			logger.info("["+this.channelName+"] Channel Closed.");
		}
	}
}
