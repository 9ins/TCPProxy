package org.chaostocosmos.net.tcpproxy.managmenet;

import java.io.IOException;

import org.chaostocosmos.net.tcpproxy.TCPProxy;
import org.eclipse.jetty.server.session.Session;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet {

    TCPProxy tcpProxy;

    public LoginServlet(TCPProxy tcpProxy) {
        this.tcpProxy = tcpProxy;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Context Path: "+request.getContextPath()+"   session id: "+request.getRequestedSessionId());
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("{ \"status\": \"ok\"}");
    }

}
