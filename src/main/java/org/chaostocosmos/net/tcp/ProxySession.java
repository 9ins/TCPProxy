package org.chaostocosmos.net.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

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
	InteractiveChannel receiveThread, sendThread;
	Logger logger;
	
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
		this.logger = Logger.getInstance();
	}
	
	/**
	 * Create remote socket object with configuration.
	 * @param sm
	 * @return
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public Socket createRemoteSocket(SessionMapping sm) throws UnknownHostException, IOException {
		Socket socket = new Socket(sm.getRemoteHost(), sm.getRemotePort());
		return applySocketConfig(socket, sm);		
	}
	
	/**
	 * Apply socket configuration
	 * @param socket
	 * @param sm
	 * @return
	 * @throws SocketException
	 */
	public Socket applySocketConfig(Socket socket, SessionMapping sm) throws SocketException {
		socket.setSoTimeout(sm.getSoTimeout());
		socket.setKeepAlive(sm.isKeepAlive());
		int bufferSize = sessionMapping.getReceiveBufferSize() == 0 ? 1024 : sessionMapping.getReceiveBufferSize();
		socket.setReceiveBufferSize(bufferSize);
		socket.setSendBufferSize(bufferSize);
		socket.setTcpNoDelay(sm.isTcpNoDelay());
		return socket;
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
	public void close() throws IOException, InterruptedException {
		this.sendThread.close();
		this.receiveThread.close();
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
	
	@Override
	public void run() {
		try {
			logger.info("["+sessionName+"] Proxy server waiting [Port] : "+sessionMapping.getProxyPort()+"   Target: "+sessionMapping.getRemoteHost());
			ServerSocket proxyServer = new ServerSocket(this.sessionMapping.getProxyPort(), 100, InetAddress.getByName(this.config.getProxyHost()));
			while(!this.isDone) {
				Socket socket = proxyServer.accept();
				if(sessionMapping.getAllowedHosts().size() == 0 || sessionMapping.getAllowedHosts().stream().anyMatch(h -> h.equals(socket.getLocalAddress().getHostAddress()))) {
					Socket clientSocket = applySocketConfig(socket, sessionMapping);				
					Socket remoteSocket = createRemoteSocket(sessionMapping);
					receiveThread = new InteractiveChannel(sessionName+"-Receive", clientSocket, remoteSocket);
					sendThread = new InteractiveChannel(sessionName+"-Send", remoteSocket, clientSocket);
				} else {
					String err = "Not allowed host connected: "+socket.getLocalAddress().getHostAddress();
					logger.info(err);
					socket.getOutputStream().write(err.getBytes());
					socket.close();
				}
			}
		} catch(Exception ioe) {
			Logger.getInstance().throwable(ioe);
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
				int bufferSize = sessionMapping.getReceiveBufferSize() == 0 ? 1024 : sessionMapping.getReceiveBufferSize();
				byte[] buffer = new byte[bufferSize];
				int read; 
				//while(!isDone) {
					while((read = is.read(buffer)) > 0) {
						os.write(buffer, 0, read);
					}				
					os.flush();
				//	sleep(1);
				//}
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
