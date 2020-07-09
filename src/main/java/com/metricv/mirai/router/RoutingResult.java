package com.metricv.mirai.router;

import net.mamoe.mirai.message.data.MessageSource;

import java.util.HashMap;

public class RoutingResult extends HashMap<Object, Object> {
    protected MessageSource msgSource;

    public RoutingResult() {
        super();
        msgSource = null;
    }

    public MessageSource getMsgSource() {
        return msgSource;
    }

    public void insertNonNamed(Object matchResult) {
        this.put(this.size() + 1, matchResult);
    }
}
