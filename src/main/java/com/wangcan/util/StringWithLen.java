package com.wangcan.util;


/**
 * 答应流中需要写入指定长度的字符串
 * Author: buleCode
 * Date: 2016/10/23
 */
public class StringWithLen {
    private int length;
    private String str;


    public int getLength() {
        return length;
    }

    public StringWithLen(int length, String str) {
        this.length = length;
        this.str = str;
    }

    public StringWithLen setLength(int length) {
        this.length = length;
        return this;
    }

    public String getStr() {
        return str;
    }

    public StringWithLen setStr(String str) {
        this.str = str;
        return this;
    }
}
