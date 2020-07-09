package com.metricv.mirai.test;

import com.metricv.mirai.matcher.MatchOptions;
import com.metricv.mirai.matcher.RegexMatcher;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.EnumSet;
import java.util.Optional;
import java.util.regex.Matcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RegexMatcherTest {

    @Test
    public void testEasy() {
        MessageChainBuilder mcb = new MessageChainBuilder();
        mcb.add("This is a test message...");
        MessageChain mc = mcb.asMessageChain();

        RegexMatcher testee = new RegexMatcher("This (?<what>.+)");
        assertTrue(testee.isMatch(mc.get(0)));
        System.out.println("Single message match OK.");

        Optional<Object> result = testee.getMatch(mc.get(0));
        assertTrue(result.isPresent());
        assertTrue(result.get() instanceof Matcher);

        Matcher rs = (Matcher)result.get();
        assertEquals("is a test message...", rs.group("what"));
        System.out.println("Single message capture OK.");
    }

    @Test
    public void testPartial() {
        MessageChainBuilder mcb = new MessageChainBuilder();
        mcb.add("This is a test message...");
        MessageChain mc = mcb.asMessageChain();

        RegexMatcher testee = new RegexMatcher("This (?<how>[^\\s]+) ", EnumSet.of(
                MatchOptions.MATCH_PART,
                MatchOptions.SEEK_ADJ,
                MatchOptions.DISPOSE,
                MatchOptions.CATCH_MATC
        ));

        assertTrue(testee.isMatch(mc.get(0)));
        System.out.println("Partial existence match OK.");

        Optional<Object> result = testee.getMatch(mc.get(0));
        assertTrue(result.isPresent());
        assertTrue(result.get() instanceof Matcher);

        Matcher rs = (Matcher)result.get();
        assertEquals("is", rs.group("how"));
        System.out.println("Partial capture OK.");

        String remainder = testee.getMatchRemainder().contentToString().trim();
        assertEquals("a test message...", remainder);
        System.out.println("Partial truncate OK.");
    }
}
