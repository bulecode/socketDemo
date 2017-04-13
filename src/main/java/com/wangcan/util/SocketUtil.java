package com.wangcan.util;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * Author: buleCode
 * Date: 2016/10/23
 */
public class SocketUtil {

    /**
     *
     * 把应答消息写入输出流中
     *
     * @param os
     * @param version
     * @param messageId
     * @param seqNum
     * @param body   只能为int、string或者自定的stringWithLen  其他类型会被忽略
     * @throws IOException
     */
    public synchronized static void writeData(DataOutputStream os, int version, int messageId, int seqNum, Object... body) throws IOException {
        int length = 0;
        //计算回复消息体的长度
        for (Object object : body) {
            if (object instanceof Integer) {
                length += 4;
            } else if (object instanceof String) {
                length += object.toString().length();
            } else if (object instanceof StringWithLen) {
                length += ((StringWithLen) object).getLength();
            }
        }
        //写入消息头
        os.writeInt(version);
        os.writeInt(messageId);
        os.writeInt(length);
        os.writeInt(seqNum);

        //写入消息体
        for (Object object : body) {
            if (object instanceof Integer) {
                os.writeInt((int)object);
            } else if (object instanceof String) {
                os.write(object.toString().getBytes());
            } else if (object instanceof StringWithLen) {
                StringWithLen s = (StringWithLen) object;
                byte[] dest = new byte[s.getLength()];
                byte[] src = s.getStr().getBytes();
                //按照固定长度填充字节数组 这里没有做长度校验 如果string的长度超过定义的length 超出长度的部分会被丢弃
                System.arraycopy(src,0,dest,0,src.length);
                os.write(dest);
            }
        }
    }
}
