package com.metricv.mirai.test;

import com.metricv.mirai.matcher.AtMatcher;
import com.metricv.mirai.matcher.RegexMatcher;
import com.metricv.mirai.router.Routing;
import com.metricv.mirai.router.RoutingResult;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;

import static org.junit.Assert.*;

public class RoutingTest {

    static class FakeMessageEvent extends MessageEvent {
        MessageChain fakeMsgChain;

        FakeMessageEvent(MessageChain msgChain) {
            fakeMsgChain = msgChain;
        }

        @Override
        public Bot getBot() {
            return null;
        }

        @NotNull
        @Override
        public MessageChain getMessage() {
            return fakeMsgChain;
        }

        @Override
        public User getSender() {
            return null;
        }

        @NotNull
        @Override
        public String getSenderName() {
            return "MockSender";
        }

        @Override
        public Contact getSubject() {
            return null;
        }

        @Override
        public int getTime() {
            return 0;
        }
    }

    @Test
    public void simpleRouting() {
        Routing testee = Routing.serialRoute()
                .thenMatch(new RegexMatcher(".*is.*"))
                .setTarget(this::generalIsConsumer);

        MessageChainBuilder mcb = new MessageChainBuilder();
        mcb.add("This is a test message...");
        FakeMessageEvent fme = new FakeMessageEvent(mcb.asMessageChain());

        testee.startRouting(fme);

        MessageChainBuilder mcb2 = new MessageChainBuilder();
        mcb2.add("このメッセージは英語の“イズ”がありません");
        FakeMessageEvent fme2 = new FakeMessageEvent(mcb2.asMessageChain());

        testee.startRouting(fme2);
    }

    public void generalIsConsumer(RoutingResult k) {
        System.out.println("Consumer Called!");
        assertTrue(k.get(1) instanceof Optional);
        @SuppressWarnings("unchecked")  // This is just a test. We don't really care.
        Optional<Object> firstElement = (Optional)k.get(1);
        assertTrue(firstElement.isPresent());
        assertTrue(firstElement.get() instanceof Matcher);
        Matcher resultMatcher = (Matcher)firstElement.get();
        assertEquals("This is a test message...", resultMatcher.group(0));
    }

    @Test
    public void dualMsg() {
        AtomicBoolean passed = new AtomicBoolean(false);

        Routing testee = Routing.serialRoute()
                .thenMatch(new AtMatcher(112233445))
                .thenMatch(new RegexMatcher(".*OK.*"))
                .setTarget((rr) -> {
                    System.out.println("DualMsg Route called.");
                    System.out.println(rr);
                    passed.set(true);
                });

        MessageChainBuilder mcb = new MessageChainBuilder();
        mcb.add(At._lowLevelConstructAtInstance(112233445, "MockUser"));
        mcb.add("This is an OK test message...");

        testee.startRouting(new FakeMessageEvent(mcb.asMessageChain()));

        assertTrue(passed.get());
    }
}
