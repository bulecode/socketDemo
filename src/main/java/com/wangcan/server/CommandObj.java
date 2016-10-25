package com.wangcan.server;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

/**
 * 包头解析工具
 * Author: buleCode
 * Date: 2016/10/24
 */
public class CommandObj {
    public static final int HEADER_LEN = 16;

    private     int version = 0;    // 协议版本号
    private     int msgID   = 0;      // 消息id
    private     int bodyLen = 0;    // 消息体长度 

    private     byte bodyData[] = new byte[1];// 消息体的数据,初始化1个字节是为了防止转换为流的时候出现异常

    /**
     * 尝试从输入的字节流里面解析一个命令,若能成功解析出header和body,并且数据长度符合要求,就返回成功
     * @param bytes
     * @param dataLen  已经有的数据长度
     * @return true :false
     */
    public boolean parseCommand(byte[] bytes,int dataLen) throws Exception {
        if( dataLen < HEADER_LEN )
            return false;
        ByteArrayInputStream inputBuffer = new ByteArrayInputStream(bytes);

        // 读取header
        DataInputStream reader = new DataInputStream(inputBuffer);
        this.version = reader.readInt();
        this.msgID = reader.readInt();
        this.bodyLen = reader.readInt();
//        this.seqNum = reader.readInt();
        System.out.println(version + "|" + msgID + "|" + bodyLen+"|"+reader.readInt());
        if( this.bodyLen > 2048 ){
            throw new RuntimeException("数据长度异常,来自命令");
        }

        if( this.bodyLen == 0 ) // 有些消息没有消息体,也返回解析成功
            return true;

        // 判断整个body是否已经在缓冲区内,是就组装成一个完整的command对象
        if( dataLen >= (HEADER_LEN + this.bodyLen) ){
            this.bodyData = new byte[this.bodyLen];
            System.arraycopy(bytes,HEADER_LEN,this.bodyData,0,this.bodyLen);
            return true;
        }

        return  false;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getMsgID() {
        return msgID;
    }

    public void setMsgID(int msgID) {
        this.msgID = msgID;
    }

    public int getBodyLen() {
        return bodyLen;
    }

    public void setBodyLen(int bodyLen) {
        this.bodyLen = bodyLen;
    }

    public byte[] getBodyData(){
        return this.bodyData;
    }
}
