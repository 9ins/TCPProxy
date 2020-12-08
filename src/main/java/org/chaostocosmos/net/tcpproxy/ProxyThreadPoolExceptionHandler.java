package org.chaostocosmos.net.tcpproxy;

import org.chaostocosmos.net.tcpproxy.ProxySession.SessionTask;

/**
 * Proxy Thread Pool Exception Handler
 */
public interface ProxyThreadPoolExceptionHandler {

    /**
     * Handing exception;
     * @param task
     * @param t
     */
    public void handleException(SessionTask task, Throwable t);
}
