package com.wangcan.server;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author: buleCode
 * Date: 2016/10/24
 */
public enum SessionManager {
    INSTANCE;

    private List<ClientSession> sessionList = Collections.synchronizedList(new ArrayList<>());

    public List<ClientSession> getSessionList() {
        return sessionList;
    }

    public void setSessionList(List<ClientSession> sessionList) {
        this.sessionList = sessionList;
    }

    public SessionManager addSession(ClientSession session) {
        this.sessionList.add(session);
        return this;
    }

    public SessionManager removeSession(ClientSession session) {
        this.sessionList.remove(session);
        return this;
    }

}
