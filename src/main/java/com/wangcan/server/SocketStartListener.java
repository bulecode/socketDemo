package com.wangcan.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 用于在web容器启动的时候开启一个线程启动socket
 *
 * Author: buleCode
 * Date: 2016/10/23
 */
public class SocketStartListener implements ServletContextListener {

    private SocketThread socketThread;

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        if (null != socketThread && !socketThread.isInterrupted()) {
            socketThread.closeSocketServer();
            socketThread.interrupt();
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        if (null == socketThread) {
            //新建线程类
            socketThread = new SocketThread(null);
            //启动线程
            socketThread.start();
        }
    }
}
