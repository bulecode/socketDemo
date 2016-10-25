package com.wangcan.server;

import com.wangcan.util.SocketUtil;

import java.io.*;

/**
 * Author: buleCode
 * Date: 2016/10/24
 */
public class ProtocolObj {
    public static final int LOGIN_REQ           = 0x00000001;  //注册连接请求
    public static final int LOGOUT_REQ          = 0x00000002;  //连接注销请求
    public static final int STATUS_REPORT_REQ   = 0x00000003;  //心跳查询请求
    public static final int EXPLORE_REQ         = 0x00000004;  //探测处理请求
    public static final int ANALYSIS_REQ        = 0x00000005;  //分析处理请求

    public static final int LOGIN_RESP           = 0x80000001;  //注册连接应答
    public static final int LOGOUT_RESP          = 0x80000002;  //连接注销应答
    public static final int STATUS_REPORT_RESP   = 0x80000003;  //心跳查询应答
    public static final int EXPLORE_RESP         = 0x80000004;  //探测处理应答
    public static final int ANALYSIS_RESP        = 0x80000005;  //分析处理应答

    public static final int VERSION              = 1;  //版本

    /**
     *
     * @param cmdObj
     * @param os
     */
    public void     dispachCmd(ClientSession session,CommandObj cmdObj, OutputStream os) throws Exception{

        DataOutputStream outputStream = new DataOutputStream(os);
        final DataInputStream bodyInputStream = new DataInputStream(new ByteArrayInputStream(cmdObj.getBodyData()));

        switch (cmdObj.getMsgID()){
            case LOGIN_REQ:
                doLoginReq(session,bodyInputStream,outputStream);
                break;
            case LOGOUT_REQ:
                doLogoutReq(session,bodyInputStream,outputStream);
                break;
            case STATUS_REPORT_REQ:
                doStatusReportReq(session,bodyInputStream,outputStream);
                break;
            case EXPLORE_RESP:
                doExploreResp(session,bodyInputStream,outputStream);
                break;
            case ANALYSIS_RESP:
                doAnalysisResp(session,bodyInputStream,outputStream);
                break;
            default:
                // 不能识别的命令
                break;

        }

    }

    /**
     * 用户登录
     * @param session
     * @param bodyInputStream
     * @param os
     * @throws IOException
     */
    private void doLoginReq(ClientSession session,final DataInputStream bodyInputStream,DataOutputStream os) throws IOException {
        session.setLastUpdateTick(System.currentTimeMillis());

        byte ip[] = new byte[16];
        bodyInputStream.read(ip,0,16);
        String strIP = new String(ip,0,16).trim();
        int moduleId = bodyInputStream.readInt();

        byte username_raw[] = new byte[32];
        byte password_raw[] = new byte[32];
        bodyInputStream.read(username_raw,0,32);
        bodyInputStream.read(password_raw,0,32);

        String strUserName = new String(username_raw,0,32).trim();
        String strPassword = new String(password_raw,0,32).trim();

        //检验登录信息是否
        if ("192.168.1.5".equals(strIP) && "test1".equals(strUserName) && "1111".equals(strPassword)) {
            SocketUtil.writeData(os,1,LOGIN_RESP,session.getSeqNum(),1,1);
            throw new RuntimeException("登录信息不合法");
        }
        session.setUserName(strUserName);
        session.setPassWord(strPassword);
        session.setModuleId(moduleId);
        SocketUtil.writeData(os,VERSION,LOGIN_RESP,session.getSeqNum(),0,0);
    }


    /**
     *分析应答
     */
    private void doAnalysisResp(ClientSession session,final  DataInputStream bodyInputStream,DataOutputStream os) throws Exception {
        session.setLastUpdateTick(System.currentTimeMillis());
       //todo
    }

    /**
     * 探测应答
     */
    private void doExploreResp(ClientSession session, final DataInputStream bodyInputStream, DataOutputStream os) throws Exception{
        session.setLastUpdateTick(System.currentTimeMillis());
        //todo
    }

    /**
     * 心跳
     */
    private void doStatusReportReq(ClientSession session,final DataInputStream bodyInputStream,DataOutputStream os) throws Exception{
        session.setLastUpdateTick(System.currentTimeMillis());
        SocketUtil.writeData(os,VERSION,STATUS_REPORT_RESP,session.getSeqNum());
    }

    /**
     * 注销
     */
    private void doLogoutReq(ClientSession session,final DataInputStream bodyInputStream,DataOutputStream os) throws Exception{
        SocketUtil.writeData(os,VERSION,LOGOUT_RESP,session.getSeqNum(),0);
        os.flush();
        session.close();
        System.out.println(session.getRemoteIP() + "请求注销登录成功");
    }
}
