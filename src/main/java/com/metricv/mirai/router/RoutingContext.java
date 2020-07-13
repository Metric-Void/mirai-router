package com.metricv.mirai.router;

import io.ktor.client.features.Sender;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.MessageEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * The context of a route.
 * Includes Bot QQ, message sender, etc.
 */
public class RoutingContext extends HashMap<String, Object> {
    public Bot getBot() {
        return bot;
    }

    public User getSender() {
        return sender;
    }

    protected Bot bot;
    protected User sender;

    RoutingContext(MessageEvent event) {
        bot = event.getBot();
        sender = event.getSender();
    }
}
