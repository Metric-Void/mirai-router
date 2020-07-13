package com.metricv.mirai.router;

import com.metricv.mirai.matcher.MatchOptions;
import com.metricv.mirai.matcher.Matcher;
import net.mamoe.mirai.message.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageSource;
import net.mamoe.mirai.message.data.SingleMessage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class Routing {

    /**
     * The configuration of a matcher, in a chain of matchers from a routing.
     */
    static class MatcherConfig {
        String name;
        Matcher matcher;
        EnumSet<MatchOptions> opts;
    }

    protected ArrayList<MatcherConfig> matcherChain;
    protected boolean matchParallel;
    protected Consumer<RoutingResult> target;

    protected Routing() {
        matcherChain = new ArrayList<>();
    }

    protected Routing(boolean isParallel) {
        this();
        matchParallel = isParallel;
    }

    public static Routing parallelRoute() {
        return new Routing(true);
    }

    public static Routing serialRoute() {
        return new Routing(false);
    }

    /**
     * Put a customized parameter into this routing.
     * Routing will become {@link ParameterizedRouting} . Original Routing is not affected.
     * Parameters will appear in RoutingResult.
     * @param name Name of the parameter
     * @param param Value.
     * @return self, but in the form of a {@link ParameterizedRouting}.
     */
    public ParameterizedRouting putParam(String name, Object param) {
        ParameterizedRouting pr = new ParameterizedRouting(this);
        pr.putParam(name, param);
        return pr;
    }

    /**
     * Accept and process an event.
     * @param event {@link MessageEvent} from mirai.
     */
    public void startRouting(@NotNull MessageEvent event) {
        RoutingResult initialResult = new RoutingResult();
        initialResult.eventSource = event;
        startRouting(event, initialResult);
    }

    protected void startRouting(@NotNull MessageEvent event, RoutingResult initialResult) {
        MessageChain incomingMessage = event.getMessage();
        List<SingleMessage> contentList = new ArrayList<>(incomingMessage);

        RoutingContext context = new RoutingContext(event);

        // Isolate MessageSource.
        if(incomingMessage.first(MessageSource.Key) != null) {
            initialResult.msgSource = incomingMessage.first(MessageSource.Key);
            contentList.remove(initialResult.msgSource);
        }

        // Start matching

        for(int index=0; index < matcherChain.size(); index += 1) {
            Optional<Object> matchResult = Optional.empty();
            MatcherConfig curr = matcherChain.get(index);
            if(curr.opts.contains(MatchOptions.SEEK_NEXT)) {
                while(index < matcherChain.size()) {
                    if (curr.matcher.isMatch(context, contentList.get(index))) {
                        matchResult = curr.matcher.getMatch(context, contentList.get(index));
                        break;
                    }
                    index += 1;
                }
                if(matchResult.isEmpty()) return;
            } else {
                if(curr.matcher.isMatch(context, contentList.get(index))) {
                    matchResult = curr.matcher.getMatch(context, contentList.get(index));
                } else {
                    return;
                }
            }

            if(!curr.opts.contains(MatchOptions.CATCH_NONE)) {
                if(curr.name != null) {
                    initialResult.put(curr.name, matchResult);
                } else {
                    initialResult.insertNonNamed(matchResult);
                }
            }
        }
        // Routing has ended. All route match.
        target.accept(initialResult);
    }

    /**
     * Attach a matcher that matches the next new message.
     * The previous matcher will be forcifully set to "DISPOSE".
     * @param nextMatcher The next matcher. Construct one yourself.
     * @return self
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

    /**
     * Set a functional interface to this message.
     * @param target A {@link Consumer} accepting a {@link RoutingResult}.
     * @return self
     */
    public Routing setTarget(Consumer<RoutingResult> target) {
        this.target = target;
        return this;
    }
}
