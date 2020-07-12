package com.metricv.mirai.router;

import com.metricv.mirai.matcher.MatchOptions;
import com.metricv.mirai.matcher.Matcher;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageSource;
import net.mamoe.mirai.message.data.SingleMessage;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class Routing {

    static class MatcherConfig {
        String name;
        Matcher matcher;
        EnumSet<MatchOptions> opts;
    }

    private ArrayList<MatcherConfig> matcherChain;
    private boolean matchParallel;
    private Consumer<RoutingResult> target;

    private Routing() {
        matcherChain = new ArrayList<>();
    }

    private Routing(boolean isParallel) {
        this();
        matchParallel = isParallel;
    }

    public static Routing parallelRoute() {
        return new Routing(true);
    }

    public static Routing serialRoute() {
        return new Routing(false);
    }

    public void startRouting(MessageChain incomingMessage) {
        List<SingleMessage> contentList = incomingMessage;
        RoutingResult result = new RoutingResult();

        // Isolate MessageSource.
        if(incomingMessage.first(MessageSource.Key) != null) {
            result.msgSource = incomingMessage.first(MessageSource.Key);
            contentList.remove(result.msgSource);
        }

        // Start matching

        for(int index=0; index < matcherChain.size(); index += 1) {
            Optional<Object> matchResult = Optional.empty();
            MatcherConfig curr = matcherChain.get(index);
            if(curr.opts.contains(MatchOptions.SEEK_NEXT)) {
                while(index < matcherChain.size()) {
                    if (curr.matcher.isMatch(contentList.get(index))) {
                        matchResult = curr.matcher.getMatch(contentList.get(index));
                        break;
                    }
                    index += 1;
                }
                if(matchResult.isEmpty()) return;
            } else {
                if(curr.matcher.isMatch(contentList.get(index))) {
                    matchResult = curr.matcher.getMatch(contentList.get(index));
                } else {
                    return;
                }
            }

            if(!curr.opts.contains(MatchOptions.CATCH_NONE)) {
                if(curr.name != null) {
                    result.put(curr.name, matchResult);
                } else {
                    result.insertNonNamed(matchResult);
                }
            }
        }
        // Routing has ended. All route match.
        target.accept(result);
    }

    /**
     * Attach a matcher that matches the next new message.
     * The previous matcher will be forcifully set to "DISPOSE".
     * @param nextMatcher The next matcher. Construct one yourself.
     * @return
     */
    public Routing thenMatch(Matcher nextMatcher) {
        MatcherConfig mc = new MatcherConfig();
        mc.matcher = nextMatcher;
        mc.name = null;
        mc.opts = nextMatcher.getCurrentOpts();

        if(matcherChain.size() != 0) {
            matcherChain.get(matcherChain.size()-1).opts.remove(MatchOptions.RETAIN);
            matcherChain.get(matcherChain.size()-1).opts.add(MatchOptions.DISPOSE);
        }
        matcherChain.add(mc);
        return this;
    }

    public Routing setTarget(Consumer<RoutingResult> target) {
        this.target = target;
        return this;
    }
}
