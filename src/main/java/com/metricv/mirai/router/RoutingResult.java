package com.metricv.mirai.router;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageSource;

import java.util.HashMap;

/**
 * The result of a routing.
 */
public class RoutingResult extends HashMap<Object, Object> {
    protected MessageSource msgSource;
    protected Event eventSource;

    public boolean isGroupMsg() {
        return eventSource instanceof GroupMessageEvent;
    }

    public Event getEvent() {
        return eventSource;
    }

    public GroupMessageEvent getGroupEventSource() {
        return (eventSource instanceof GroupMessageEvent)? (GroupMessageEvent) eventSource : null;
    }

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
