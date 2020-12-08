package org.chaostocosmos.net.tcpproxy;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.chaostocosmos.net.tcpproxy.ProxySession.SessionTask;

/**
 * Proxy Thread Pool Executor
 * 
 * @author Kooin-Shin
 * @date 2020.12.02
 */
public class ProxyThreadPoolExecutor extends ThreadPoolExecutor {

    ProxyThreadPoolExceptionHandler exceptionHandler;

    Logger logger;

    /**
     * Constructor
     * 
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime
     * @param unit
     * @param workQueue
     * @param handler
     * @param exceptionHandler
     */
    public ProxyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
            BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler,
            ProxyThreadPoolExceptionHandler exceptionHandler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
        this.exceptionHandler = exceptionHandler;
        this.logger = Logger.getInstance();
    }

    /**
     * Execute session task object
     * @param task
     */
    public void execute(SessionTask task) {
        super.execute(task);
    }

    /**
     * Override afterExecute
     */
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        /*
        try {            
            ((SessionTask) r).closeTask();
        } catch (IOException e) {
            logger.throwable(e);
        }
        */
        this.exceptionHandler.handleException((SessionTask) r, t);                
    }
}
