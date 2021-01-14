package org.chaostocosmos.net.tcpproxy.managmenet;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.chaostocosmos.net.tcpproxy.TCPProxy;
import org.eclipse.jetty.server.Authentication.ResponseSent;
import org.eclipse.jetty.server.session.SessionHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ManagementServlet extends HttpServlet {

    TCPProxy tcpProxy;
    String sessionId = "";

    Map<String, Long> sessionIdMap = new HashMap<>();

    public ManagementServlet(TCPProxy tcpProxy) {
        this.tcpProxy = tcpProxy;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        printRqeust(request);

        System.out.println("Session id: "+request.getSession().getId());
        System.out.println("Request id: "+request.getRequestedSessionId());
        System.out.println("isNew: "+request.getSession().isNew());

        String requestId = request.getRequestedSessionId();
        Long lastTimeMillis = sessionIdMap.get(requestId);
        if(lastTimeMillis != null && lastTimeMillis+5000 < System.currentTimeMillis()) {
            request.logout();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }        
        sessionIdMap.put(requestId, System.currentTimeMillis());
        Cookie cookie = new Cookie("JSESSIONID", request.getSession().getId());
        cookie.setMaxAge(10);
        response.addCookie(cookie);
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("{ \"status\": \"ok\"}");
    }

    protected void printRqeust(HttpServletRequest request) {
        this.sessionIdMap.keySet().stream().forEach(k -> System.out.println("User : "+k+"   Id : "+sessionIdMap.get(k)));
        Enumeration<String> enu = request.getHeaderNames();
        while(enu.hasMoreElements()) {
            String key = enu.nextElement();
            System.out.println("Name : "+key+"  Value: "+request.getHeader(key));
        }
        enu = request.getAttributeNames();
        while(enu.hasMoreElements()) {
            String key = enu.nextElement();
            System.out.println("Attr name : "+key+"  Attr value: "+request.getAttribute(key));
        }
        System.out.println("Context Path: "+request.getContextPath()+"   Request Session Id: "+request.getRequestedSessionId());
        System.out.println(
            "isRequestedSessionIdValid: "+request.isRequestedSessionIdValid()+"   \n"+
            "isRequestedSessionIdFromCookie: "+request.isRequestedSessionIdFromCookie()+"   \n"+
            "isRequestedSessionIdFromURL: "+request.isRequestedSessionIdFromURL()+"   \n"+            
            "getMaxInactiveInterval: "+request.getSession().getMaxInactiveInterval()+"   \n"+
            //"getMaxInactiveInterval: "+request.getSession().getAttribute("state").toString()+"   \n"+
            "isUserInRole: "+request.isUserInRole("ADMIN") +"   \n"+
            "user: "+request.getParameter("username") +"   \n"
            );
    }

}
