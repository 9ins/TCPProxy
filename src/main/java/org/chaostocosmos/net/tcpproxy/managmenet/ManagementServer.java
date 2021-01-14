package org.chaostocosmos.net.tcpproxy.managmenet;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.chaostocosmos.net.tcpproxy.Logger;
import org.chaostocosmos.net.tcpproxy.ResourceUtils;
import org.chaostocosmos.net.tcpproxy.TCPProxy;
import org.chaostocosmos.net.tcpproxy.config.ConfigHandler;
import org.chaostocosmos.net.tcpproxy.credential.Credential;
import org.chaostocosmos.net.tcpproxy.credential.Credentials;
import org.chaostocosmos.net.tcpproxy.credential.CredentialsHandler;
import org.chaostocosmos.net.tcpproxy.credential.Session;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.UserStore;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.server.Authentication.User;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.server.session.HouseKeeper;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Password;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

/**
 * ManagementServer
 */
public class ManagementServer {

	TCPProxy tcpProxy;
	Server server;
	ServerConnector connector;
	QueuedThreadPool threadPool;
	BasicSecurityManager securityManager;
	ConfigHandler configHandler;
	CredentialsHandler credentialsHandler;
	DefaultSessionIdManager sessionIdManager;
	HouseKeeper houseKeeper;

	/**
	 * Constructor
	 * 
	 * @param tcpProxy
	 * @param configHandler
	 * @param credentialsHandler
	 * @throws Exception
	 */
	public ManagementServer(TCPProxy tcpProxy, ConfigHandler configHandler, CredentialsHandler credentialsHandler)
			throws Exception {
		this.tcpProxy = tcpProxy;
		this.configHandler = configHandler;
		this.credentialsHandler = credentialsHandler;
		init();
	}

	public void init() throws Exception {
		// Initialize thread pool
		this.threadPool = new QueuedThreadPool(30, 30, 10000);
		this.threadPool.setDaemon(true);

		// Jetty server create and set connector
		this.server = new Server(this.threadPool);
		this.sessionIdManager = new DefaultSessionIdManager(this.server);
		this.sessionIdManager.setWorkerName("TCPProxy");
		this.server.setSessionIdManager(this.sessionIdManager);

		// Setting house keeper
		this.houseKeeper = new HouseKeeper();
		this.houseKeeper.setIntervalSec(10L);
		this.sessionIdManager.setSessionHouseKeeper(this.houseKeeper);

		// Initialize server connector
		this.connector = new ServerConnector(this.server);
		this.connector.setAccepting(true);
		this.connector.setHost(this.configHandler.getConfig().getManagementAddress());
		this.connector.setPort(this.configHandler.getConfig().getManagementPort());
		this.server.setConnectors(new Connector[] { this.connector });

		// initialize security manager
		this.securityManager = new BasicSecurityManager(this.credentialsHandler.getCredentials());

		// Setting ManagementServelt & Handler
		Path resourcePath = Paths.get("D:\\Projects\\TCPProxy\\tcpproxy-app\\build");

		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setResourceBase(resourcePath.toString());		

		SecurityHandler securityHandler = this.securityManager.getSessionSecurityHandler("oracle");

		SessionHandler sessionHandler = new SessionHandler();
		sessionHandler.addEventListener(new HttpSessionListener() {
			@Override
			public void sessionCreated(HttpSessionEvent se) {
				System.out.println("############## Session Created interval : " + se.getSession().getMaxInactiveInterval());				
			}
			@Override
			public void sessionDestroyed(HttpSessionEvent se) {
				System.out.println("############## Session invalidate. id: "+se.getSession().getId());
			}
		});
		sessionHandler.setSessionIdManager(this.sessionIdManager);
		webAppContext.setSessionHandler(sessionHandler);
		
		ManagementServlet mServlet = new ManagementServlet(this.tcpProxy);
		webAppContext.addServlet(new ServletHolder(mServlet), "/*");
		webAppContext.setWelcomeFiles(new String[] { "index.html" });
		webAppContext.setSecurityHandler(securityHandler);

		HandlerList list = new HandlerList();
		list.addHandler(webAppContext);

		this.server.setHandler(webAppContext);
		Arrays.asList(this.server.getChildHandlers()).stream().forEach(h -> System.out.println(h.toString()));

		this.server.start();
		webAppContext.getSessionHandler().setMaxInactiveInterval(5);
	}

	public void stop() throws Exception {
		this.server.stop();
		this.server.join();
	}

	/**
	 * New and get ResourceHandler object. parameter e.g. "static-upload/index.html"
	 * 
	 * @param resourcePath
	 * @return
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 */
	public static Handler newResourceHandler(String resourcePath) throws URISyntaxException, MalformedURLException {
		return newResourceHandler(Paths.get(resourcePath));
	}

	/**
	 * New and get ResourceHandler
	 * 
	 * @param resourcBase
	 * @return
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 */
	public static Handler newResourceHandler(Path resourcBase) throws URISyntaxException, MalformedURLException {
        URI indexUri = ResourceUtils.findClassLoaderResource(resourcBase.toString());
        URI staticUploadBaseUri = indexUri.resolve("./").normalize();
        Resource baseResource = Resource.newResource(staticUploadBaseUri);
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setBaseResource(baseResource);
        return resourceHandler;
	}
}