package org.chaostocosmos.net.tcpproxy.managmenet;

import java.nio.file.Path;

import org.chaostocosmos.net.tcpproxy.TCPProxy;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class ManagementHandler extends ServletContextHandler {

    ManagementServlet servlet;    
    String contextPath;
    Path resourceBase;
    
    public ManagementHandler(TCPProxy tcpProxy, String contextPath, Path resourceBase) {
        super(ServletContextHandler.SESSIONS);
        this.servlet = new ManagementServlet(tcpProxy);
        this.contextPath = contextPath;
        this.resourceBase = resourceBase;
        ServletHolder holder = new ServletHolder(servlet);
        this.setWelcomeFiles(new String[]{"index.html"});
        this.addServlet(holder, contextPath+"*");
        this.setResourceBase(this.resourceBase.toString());
    }


}
