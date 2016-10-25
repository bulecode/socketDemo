package com.wangcan.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 创建一个ServerSocket 监听指定的端口
 * Author: buleCode
 * Date: 2016/10/23
 */
public class SocketThread extends Thread {

    private static final int SERVER_PORT = 8188;
    private ServerSocket serverSocket = null;

    public SocketThread(ServerSocket serverScoket) {
        try {
            if (null == serverSocket) {
                this.serverSocket = new ServerSocket(SERVER_PORT);
                System.out.println("ServerSocket start,port is " + SERVER_PORT);
            }
        } catch (Exception e) {
            System.out.println("create ServerSocket error:"+e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (serverSocket == null) {
                    break;
                } else if (serverSocket.isClosed()) {
                    break;
                }
                //阻塞式方法 当有客户端连接的时候才会通过
                Socket socket = serverSocket.accept();
                if (null != socket && !socket.isClosed()) {
                    //自定义通信session类记录并处理此次会话信息
                    ClientSession clientSession = new ClientSession(socket);
                    //记录建立的session 方便管理 例如清理长时间没有心跳的客户端
                    SessionManager.INSTANCE.addSession(clientSession);
                    System.out.println("current socket session size："+SessionManager.INSTANCE.getSessionList().size());
                    //开启新的线程与客户端通信
                    Thread t = new Thread(clientSession);
                    t.start();
                } else {
                    break;
                }
            } catch (Exception e) {
                System.out.println("create ServerSocket error:" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void closeSocketServer() {
        try {
            if (null != serverSocket && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
