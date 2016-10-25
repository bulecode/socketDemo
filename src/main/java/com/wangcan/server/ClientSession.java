package com.wangcan.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 记录并处理此次会话信息 成员字段 根据自身的义务需求定义
 * Author: buleCode
 * Date: 2016/10/23
 */
public class ClientSession implements Runnable {
    public  static final int MAX_BUFFER_LEN = 2048; // 读取数据最大的缓冲区长度

    private Socket socket;
    private final ProtocolObj protocolObj = new ProtocolObj();
    private CommandObj cmdObj = new CommandObj();

    private     String  userName;   // 用户名
    private     String  passWord;   // 密码
    private     String  remoteIP;   // 远端ip
    private     int     moduleId;   // 模块id
    private     int     seqNum  = 0;     // 消息序列号
    private     Long    lastUpdateTick; // 最后活动时间,用于清除超时的连接


    public ClientSession(Socket socket) throws IOException {
        this.socket = socket;
        this.remoteIP = socket.getInetAddress().getHostAddress();
        System.out.println("connect create by" + socket.getInetAddress());
    }

    @Override
    public void run() {

        byte buffer_raw[] = new byte[MAX_BUFFER_LEN];
        Integer curLen = 0; // 当前缓冲区里面的数据长度

        try {
            InputStream in = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            // 采用循环不断从Socket中读取客户端发送过来的数据
            while (true) {

                //1.尝试从输入里面读取数据,必须有一个header的长度
                // Math.min(512,2048-curLen) 保证了不会读取超过缓冲区长度的数据(防止缓冲区攻击)
                int retLen = in.read(buffer_raw, curLen, Math.min(512,MAX_BUFFER_LEN-curLen));

                if( retLen == -1 ){
                    // 文件已经结束,关闭回话
                    System.out.println(this.remoteIP + " 已断开连接");
                    close();
                    break;
                }

                if( retLen >0 ) {
                    curLen += retLen;
                }

                // 2.如果不够header的长度,继续读取
                if( curLen < CommandObj.HEADER_LEN )
                    continue;

                // 3.已经有至少16个字节了,尝试解析命令,若成功,就处理命令
                while(true) {
                    // 需要连续处理多个命令
                    if( cmdObj.parseCommand(buffer_raw,curLen) ){

                        // 3.1 命令解析成功,处理命令
                        this.protocolObj.dispachCmd(this,cmdObj,os);
                        os.flush();

                        int handledMsgLen = CommandObj.HEADER_LEN + cmdObj.getBodyLen();

                        // 3.2处理完毕,移动buffer和校正指针
                        System.arraycopy(buffer_raw,handledMsgLen,buffer_raw,0,curLen-handledMsgLen);
                        curLen -= handledMsgLen;

                        this.lastUpdateTick = System.currentTimeMillis();

                    }else{
                        break;
                    }
                }
            }
        } catch (Exception e) {
            close(e);
        }finally {
            //每次回话结束 都需要把此次回话的对象从sessionManager里移除
            SessionManager.INSTANCE.removeSession(this);
            System.out.println("current socket session size："+SessionManager.INSTANCE.getSessionList().size());
        }
    }

    private void close(Exception e) {
        System.out.println(this.remoteIP + "exception info : " + e.getMessage());
        e.printStackTrace();
        close();
    }

    //关闭此次会话
    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public int getModuleId() {
        return moduleId;
    }

    public void setModuleId(int moduleId) {
        this.moduleId = moduleId;
    }

    public int getSeqNum() {
        return seqNum++;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getLastUpdateTick() {
        return lastUpdateTick;
    }

    public void setLastUpdateTick(Long lastUpdateTick) {
        this.lastUpdateTick = lastUpdateTick;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getRemoteIP() {
        return remoteIP;
    }

}
