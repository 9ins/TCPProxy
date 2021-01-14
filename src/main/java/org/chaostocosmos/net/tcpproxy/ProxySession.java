package org.chaostocosmos.net.tcpproxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.chaostocosmos.net.tcpproxy.config.ConfigException;
import org.chaostocosmos.net.tcpproxy.config.SessionMapping;

/**
 * 
 * ProxySession
 *
 * @author 9ins 2020. 11. 18.
 */
public class ProxySession implements Runnable {

	boolean isDone = false;
	String sessionName;
	Thread thread;
	SessionMapping sessionMapping;
	Logger logger;
	Map<String, InteractiveChannel> sendChannelMap;
	Map<String, InteractiveChannel> receiveChannelMap;
	ServerSocket proxyServer;
	ProxyThreadPool threadPool;
	boolean standAloneFailed = false;
	boolean masterSessionFailed = false;
	boolean slaveSessionFailed = false;
	Map<Integer, Boolean> loadBalancedStatus;
	long totalSuccessCount, totalFailCount;
	long sessionStartMillis, sessionCurrentMillis;	

	/**
	 * Constructor
	 * 
	 * @param proxy
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public ProxySession(String sessionName, SessionMapping sm, ProxyThreadPool proxyThreadPool) {
		this.logger = Logger.getInstance();
		this.sessionName = sessionName;		
		this.sessionMapping = sm;
		this.threadPool = proxyThreadPool;
		this.sendChannelMap = new HashMap<>();
		this.receiveChannelMap = new HashMap<>();
		this.loadBalancedStatus = IntStream.range(0, this.sessionMapping.getRemoteHosts().size()).boxed().map(i -> new AbstractMap.SimpleEntry<Integer, Boolean>(i, true)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	/**
	 * Start
	 */
	public void start() {
		this.thread = new Thread(this);
		this.thread.start();
	}

	/**
	 * Close this session
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void closeSession() throws IOException, InterruptedException {
		closeAllChannels();
		this.proxyServer.close();
	}

	/**
	 * Close
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void closeAllChannels() throws IOException, InterruptedException {
		for (int i = 0; i < sendChannelMap.size(); i++) {
			sendChannelMap.get(i).close();
			receiveChannelMap.get(i).close();
		}
	}

	/**
	 * Close Socket
	 * 
	 * @param socket
	 * @param msg
	 * @throws IOException
	 */
	public void closeSocket(Socket socket, String msg) throws IOException {
		if (socket != null) {
			OutputStream os = socket.getOutputStream();
			os.write(msg.getBytes());
			os.flush();
			os.close();
			socket.close();
		}
	}

	/**
	 * Get session
	 * 
	 * @return
	 */
	public String getSessionName() {
		return this.sessionName;
	}

	/**
	 * Set session
	 * 
	 * @param sessionName
	 */
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	@Override
	public void run() {
		logger.info("[" + sessionName + "] [Session type: "+sessionMapping.getSessionModeEnum().name()+"] Proxy server waiting [Port] : " + sessionMapping.getProxyPort()	+ "   Target: " + sessionMapping.getRemoteHosts().toString());
		try {
			this.proxyServer = new ServerSocket(this.sessionMapping.getProxyPort(), 100,	InetAddress.getByName(sessionMapping.getProxyBindAddress()));
		} catch (IOException e) {
			logger.throwable(e);
			return;
		}
		while (!this.isDone) {
			try {
				this.sessionStartMillis = System.currentTimeMillis();
				Socket client = proxyServer.accept();
				if (sessionMapping.getRemoteHosts().stream().anyMatch(h -> sessionMapping.isForbiddenHost(h))) {
					String err = "Forbidden remote host request detected. Forbidden List: " + sessionMapping.getRemoteHosts().toString();
					logger.info(err);
					closeSocket(client, err);
				} else {
					if (sessionMapping.getAllowedHosts().size() == 0 || sessionMapping.getAllowedHosts().stream().anyMatch(h -> h.equals(client.getLocalAddress().getHostAddress()))) {
						logger.info("[" + sessionName + "][SESSION MODE: " + this.sessionMapping.getSessionModeEnum()+ "] Client channel connected. "+client.getRemoteSocketAddress().toString());
						SessionTask task = new SessionTask(sessionName, sessionMapping, client);
						logger.info("[" + sessionName + "] new client is connected. Clinet: " + client.getRemoteSocketAddress().toString() + " ------------------- connected.");
						this.threadPool.execute(task);
					} else {
						String err = "Not allowed host connected: " + client.getRemoteSocketAddress().toString();
						logger.info(err);
						closeSocket(client, err);
					}
				}
			} catch (Exception e) {
				logger.throwable(e);
			}
		}
	}

	/**
	 * Get remote host index by ratio list
	 * 
	 * @param ratioList
	 * @return
	 */
	public int getRandomRatioIndex(List<Float> ratioList) {
		Random random = new Random();
		int sum = ratioList.stream().reduce((a, b) -> Float.sum(a, b)).get().intValue();
		int start = 0;
		int bound = 0;
		int ran = random.nextInt(sum);
		for (int i = 0; i < ratioList.size(); i++) {
			int ratio = ratioList.get(i).intValue();
			bound += ratio;
			if (ran >= start && ran < bound) {
				// System.out.println("VAl: "+ran+" Start: "+start+" End : "+bound);
				return i;
			}
			start += ratio;
		}
		return 0;
	}

	/**
	 * SessionTask
	 */
	public class SessionTask extends Thread {

		String sessionName;
		SessionMapping sessionMapping;
		Socket client;
		List<String> remotes;
		long transactionStartMillis;

		public SessionTask(String sessionName, SessionMapping sessionMapping, Socket client) {
			this.transactionStartMillis = System.currentTimeMillis();
			this.sessionName = sessionName;
			this.sessionMapping = sessionMapping;
			this.client = client;
			this.remotes = sessionMapping.getRemoteHosts();			
		}

		/**
		 * Get session name
		 * 
		 * @return
		 */
		public String getSessionName() {
			return this.sessionName;
		}

		/**
		 * Start instract between client and remote host.
		 * 
		 * @param sessionName
		 * @param client
		 * @param remote
		 * @throws InterruptedException
		 * @throws IOException
		 */
		private void startInteraction(String sessionName, Socket client, ProxySocket remote) throws Exception {
			try {
				InteractiveChannel sendChannel = new InteractiveChannel(sessionName+"-Send", client.getInputStream(), remote.getOutputStream(), transactionStartMillis);
				InteractiveChannel receiveChannel = new InteractiveChannel(sessionName+"-Receive", remote.getInputStream(), client.getOutputStream(), transactionStartMillis);
				//sendChannel.start();
				//receiveChannel.start();
				threadPool.execute(sendChannel);
				threadPool.execute(receiveChannel);
			} catch(Exception e) {
				logger.throwable(e);
				if(remote != null) remote.close();
				throw e;
			}
		}

		/**
		 * Apply socket configuration
		 * 
		 * @param socket
		 * @param sm
		 * @return
		 * @throws IOException
		 * @throws ConfigException
		 * @throws SocketException
		 */
		public ProxySocket createProxySocket(SessionMapping sm, int sessionIndex) throws ConfigException, SocketException {
			return new ProxySocket(sm, sessionIndex);
		}

		@Override
		public void run() {
			long roundRobinIndex = 0;
			int failCount = 0;
			try {
				switch (sessionMapping.getSessionModeEnum()) {
					case STAND_ALONE:
						if (remotes.size() != 1) {
							throw new ConfigException(this.sessionMapping.getSessionModeEnum().name(), "[" + sessionName+ "][SESSION MODE: " + this.sessionMapping.getSessionModeEnum()	+ "] Stand Alone session must have just one remote channel. Check the seesion configuration in config yaml!!!");
						}
						ProxySocket proxySocket = createProxySocket(sessionMapping, 0);
						int retry = sessionMapping.getStandAloneRetry();
						for (int i = 0; i < retry; i++) {
							try {
								proxySocket.connect();
								startInteraction(sessionName, client, proxySocket);
								logger.info("[" + sessionName + "][SESSION MODE: " + this.sessionMapping.getSessionModeEnum()+ "] Remote channel connected. "+proxySocket.getRemoteAddress().toString());								
								standAloneFailed = false;
								totalSuccessCount++;
								break;
							} catch (Exception e) {		
								standAloneFailed = true;						
								logger.info("[" + sessionName + "][SESSION MODE: " + this.sessionMapping.getSessionModeEnum() + "] Retry channel... Interval: "	+ Constants.RETRY_INTERVAL + " milliseconds.");
							}
							Thread.sleep(Constants.RETRY_INTERVAL);
						}
						if(standAloneFailed) {
							if(client != null) client.close();
							if(proxySocket != null) proxySocket.close();
							totalFailCount++;
						}
						break;
					case HIGI_AVAILABLE_FAIL_BACK:
						masterSessionFailed = false;
						slaveSessionFailed = false;
					case HIGI_AVAILABLE_FAIL_OVER:
						if (remotes.size() != 2) {
							throw new ConfigException(this.sessionMapping.getSessionModeEnum().name(), "[" + sessionName+ "][SESSION MODE: " + this.sessionMapping.getSessionModeEnum()	+ "] HA Fail Over session must have just two remote channel. Check the seesion configuration in config yaml!!!");
						}
						if(masterSessionFailed && slaveSessionFailed) {
							failCount++;
							logger.info("[" + sessionName + "][SESSION MODE: " + this.sessionMapping.getSessionModeEnum() + "] Master/Slave channel disable now. Please reboot or rivival using Admin CLI(Currently not support)."); 
						}
						if(!masterSessionFailed) {
							ProxySocket master = createProxySocket(sessionMapping, 0);
							try {
								master.connect();
								startInteraction(sessionName, client, master);
								logger.info("[" + sessionName + "][SESSION MODE: " + this.sessionMapping.getSessionModeEnum()+ "] Master channel connected. "+master.getRemoteAddress().toString());								
								masterSessionFailed = false;
								totalSuccessCount++;
							} catch(Exception e) {
								if(master != null) master.close();
								masterSessionFailed = true;
								failCount++;
								logger.info("[" + sessionName + "][SESSION MODE: " + this.sessionMapping.getSessionModeEnum()+ "] Master channel failed. Slave channel be used at next connection.");								
								logger.throwable(e);
							}
						}
						if(masterSessionFailed && !slaveSessionFailed) {
							ProxySocket slave = createProxySocket(sessionMapping, 1);
							try {
								slave.connect();
								startInteraction(sessionName, client, slave);
								logger.info("[" + sessionName + "][SESSION MODE: " + this.sessionMapping.getSessionModeEnum()+ "] Slave channel connected. "+slave.getRemoteAddress().toString());
								slaveSessionFailed = false;
								totalSuccessCount++;
							} catch(Exception e) {	
								if(slave != null) slave.close();
								slaveSessionFailed = true;
								failCount++;
								logger.info("[" + sessionName + "][SESSION MODE: " + this.sessionMapping.getSessionModeEnum()+ "] Slave channel failed. Master/Slave failed.");
								logger.throwable(e);
							}
						}
						if(masterSessionFailed && slaveSessionFailed) {
							totalFailCount++;
							client.close();							
						}
						break;

					case LOAD_BALANCE_ROUND_ROBIN:
						while(loadBalancedStatus.values().stream().anyMatch(b -> b == true)) { 
							int channelIndex = (int)(roundRobinIndex % (long)sessionMapping.getRemoteHosts().size());
							if(!loadBalancedStatus.get(channelIndex)) {
								roundRobinIndex++;
								continue;
							}
							ProxySocket channel = createProxySocket(sessionMapping, channelIndex);
							try { 
								channel.connect();
								startInteraction(sessionName, client, channel);
								totalSuccessCount++;
								logger.info("[" + sessionName + "][SESSION MODE: " + this.sessionMapping.getSessionModeEnum()+ "] Load-Balanced Round-Robin channel connected. Channel index: "+channelIndex+"  Host: "+channel.getRemoteAddress().toString());
								break; 
							} catch (Exception e) { 
								logger.info("["+sessionName+"][SESSION MODE: "+sessionMapping.getSessionModeEnum()+"] Load-Balance Round-Robin Channel failed. Round-Robin channel index: "+channelIndex+"  Host: "+channel.getRemoteAddress().toString()); 								
								if(channel != null) channel.close();								
								loadBalancedStatus.put(channelIndex, false);
							} finally {
								roundRobinIndex++;
								if(!loadBalancedStatus.values().stream().anyMatch(b -> b == true)) {
									logger.info("["+sessionName+"][SESSION MODE: "+sessionMapping.getSessionModeEnum()+"] All of Load-Balanced remote channel failed. Please check remote hosts healthy. eg. Network, Server status..."); 						
									if(client != null) client.close();
									if(channel != null) channel.close();
									totalFailCount++;
									break;
								}
							}							
						}
						break;

					case LOAD_BALANCE_SEPARATE_RATIO:
						List<Float> ratioList = sessionMapping.getLoadBalanceRatioList();
						while(loadBalancedStatus.values().stream().anyMatch(b -> b == true)) { 
							int randomIndex = getRandomRatioIndex(ratioList);
							randomIndex = randomIndex >= sessionMapping.getRemoteHosts().size() ? 0 : randomIndex;
							if(!loadBalancedStatus.get(randomIndex)) {
								continue;
							}
							ProxySocket channel = createProxySocket(sessionMapping, randomIndex);
							try {
								channel.connect();
								startInteraction(sessionName, client, channel); 
								totalSuccessCount++;
								logger.info("["+sessionName+"][SESSION MODE: "+this.sessionMapping.getSessionModeEnum()+"] Load-Balance Separate Ratio Distribution Channel connected. Channel index: "+randomIndex+"  Host: "+sessionMapping.getRemoteHosts().get(randomIndex).toString()); 								
								break; 
							} catch(Exception e) { 
								logger.info("["+sessionName+"][SESSION MODE: "+this.sessionMapping.getSessionModeEnum()+"] Load-Balance Separate Ratio Distribution Channel failed. Channel index: "+randomIndex+"  Host: "+sessionMapping.getRemoteHosts().get(randomIndex).toString()); 
								if(channel != null) channel.close();
								loadBalancedStatus.put(randomIndex, false);
								ratioList.set(randomIndex, 0f);
								failCount++;
							} finally {
								if(!loadBalancedStatus.values().stream().anyMatch(b -> b == true)) { 
									logger.info("["+sessionName+"][SESSION MODE: "+this.sessionMapping.getSessionModeEnum()+"] All of Load-Balance Separate Ratio Distribution remote channel failed. Please check remote host healthy. eg. Network, Server status..."); 
									if(client != null) client.close();
									if(channel != null) channel.close();
									totalFailCount++;
									break; 
								} 
							}
						}						 
						break;

					default:
						logger.error("Wrong Session Mode is input: " + this.sessionMapping.getSessionModeEnum().name());
				}
				logger.info("["+sessionName+"][SESSION MODE: "+this.sessionMapping.getSessionModeEnum()+"] Statistics:  Success = "+totalSuccessCount+"  Fail = "+totalFailCount+"");
			} catch (Exception e) {
				logger.throwable(e);
			}
		}
	}

	/**
	 * InteractiveThread
	 *
	 * @author 9ins 2020. 11. 19.
	 */
	public class InteractiveChannel extends Thread {

		boolean isDone = false;
		String channelName;
		InputStream is;
		OutputStream os;
		long startMillis;

		/**
		 * Constructor
		 * 
		 * @param channelName
		 * @param socket
		 * @throws IOException
		 */
		public InteractiveChannel(String channelName, InputStream is, OutputStream os, long startMillis) throws IOException {
			this.channelName = channelName;
			this.is = is;
			this.os = os;
			this.startMillis = startMillis;
		}

		@Override
		public void run() {
			logger.info("[" + this.channelName + "] Channel Open. ");
			final int bufferSize = sessionMapping.getBufferSize() == 0 ? 1024 : sessionMapping.getBufferSize();
			byte[] buffer = new byte[bufferSize];
			try {
				long total = 0;
				int read;
				while ((read = this.is.read(buffer)) > 0) {
					if (total < 1024) {
						 System.out.println(new String(buffer));
					}
					os.write(buffer, 0, read);
					os.flush();
					total += read;
				}
				isDone = true;
			} catch (IOException e) {
				//May socket closed. Afterword, the logic might to be modified.
				e.printStackTrace();
			} finally {
				try {
					close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public void close() throws IOException {
			this.is.close();
			this.os.close();
			logger.info("["+this.channelName+"] Channel Closed.  Channel Elapse Time Millis: "+(System.currentTimeMillis()-startMillis));
		}

		public boolean isDone() {
			return this.isDone;
		}
	}
}
