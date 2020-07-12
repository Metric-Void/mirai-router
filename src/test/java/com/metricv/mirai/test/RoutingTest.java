package com.metricv.mirai.test;

import com.metricv.mirai.matcher.RegexMatcher;
import com.metricv.mirai.router.Routing;
import com.metricv.mirai.router.RoutingResult;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.junit.Test;

import java.util.Optional;
import java.util.regex.Matcher;

import static org.junit.Assert.*;

public class RoutingTest {
    @Test
    public void simpleRouting() {
        Routing testee = Routing.serialRoute()
                .thenMatch(new RegexMatcher(".*is.*"))
                .setTarget(this::generalIsConsumer);

        MessageChainBuilder mcb = new MessageChainBuilder();
        mcb.add("This is a test message...");

        testee.startRouting(mcb.asMessageChain());

        MessageChainBuilder mcb2 = new MessageChainBuilder();
        mcb2.add("このメッセージは英語の“イズ”がありません");

        testee.startRouting(mcb2.asMessageChain());
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
}
