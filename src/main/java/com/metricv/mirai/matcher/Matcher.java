package com.metricv.mirai.matcher;

import com.metricv.mirai.router.RoutingContext;
import net.mamoe.mirai.message.data.SingleMessage;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public interface Matcher {

    /**
     * Whether a single message element is a match.
     * Differnent kinds of matchers MAY or MAY NOT care about options.
     * @param msg
     * @return
     */
    public boolean isMatch(RoutingContext context, SingleMessage msg);

    /**
     * Match a single message.
     * @param msg The single message to be matched for.
     * @return Object matched. Optional.Empty if no match.
     */
    public Optional<Object> getMatch(RoutingContext context, final SingleMessage msg);

    /**
     * Get the remainder after a single match.
     * Should only be executed after getMatch().
     * Should not be used outside.
     * @return Remainder packed within a SingleMessage.
     */
    public SingleMessage getMatchRemainder();

    /**
     * Seek the next matching term in a list. The list WILL be modified to reflect the change.
     * Whether this partial message is retained or discarded depends on MatchOptions.
     * @param msgChain List of message to match for. All preceding terms are remvoed!
     * @return Object matched. Optional.Empty if no match.
     */
    @Deprecated
    public Optional<Object> seekMatch(RoutingContext context, List<SingleMessage> msgChain);

    /**
     * Get the default options for this specific matcher.
     * @return An EnumSet of options.
     */
    public EnumSet<MatchOptions> getDefaultOpts();

    /**
     * Get current options for this specific matcher.
     * @return An EnumSet of options.
     */
    public EnumSet<MatchOptions> getCurrentOpts();
}
