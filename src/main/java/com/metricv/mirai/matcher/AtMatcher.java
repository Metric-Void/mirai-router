package com.metricv.mirai.matcher;

import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.AtAll;
import net.mamoe.mirai.message.data.SingleMessage;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

/**
 * Matches an @.
 * This matcher does not care about MatchOptions.
 * It will always match an At or AtAll element, and return qqid as match.
 * qqid=0 means @all.
 */
public class AtMatcher implements Matcher{
    public long qqid;

    /**
     * Create a AtMatcher.
     * qqid=-1 means anyone.
     * qqid=0 means @all.
     * @param qqid
     */
    AtMatcher(long qqid) {
        this.qqid = qqid;
    }

    @Override
    public boolean isMatch(SingleMessage msg) {
        if(msg instanceof At) {
            return qqid == -1 || ((At) msg).getTarget() == qqid;
        } else if (msg instanceof AtAll) {
            return qqid == 0;
        }
        return false;
    }

    @Override
    public Optional<Object> getMatch(SingleMessage msg) {
        if(msg instanceof At) {
            if (qqid == -1 || ((At) msg).getTarget() == qqid)
                return Optional.of(((At) msg).getTarget());
        } else if (msg instanceof AtAll) {
            return Optional.of(0);
        }
        return Optional.empty();
    }

    @Override
    public SingleMessage getMatchRemainder() {
        return null;
    }

    @Override
    public Optional<Object> seekMatch(List<SingleMessage> msgChain) {
        return Optional.empty();
    }

    @Override
    public EnumSet<MatchOptions> getDefaultOpts() {
        return EnumSet.of(
                MatchOptions.MATCH_ALL,
                MatchOptions.SEEK_ADJ,
                MatchOptions.DISPOSE,
                MatchOptions.CATCH_MATC
        );
    }

    @Override
    public EnumSet<MatchOptions> getCurrentOpts() {
        return EnumSet.of(
                MatchOptions.MATCH_ALL,
                MatchOptions.SEEK_ADJ,
                MatchOptions.DISPOSE,
                MatchOptions.CATCH_MATC
        );
    }
}
