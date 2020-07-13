package com.metricv.mirai.matcher;

import java.util.EnumSet;

/**
 * Options for matchers.
 * Different matchers have different defaults, and produce different results with different options.
 * Please consult their document.
 */
public enum MatchOptions {
    SEEK_NEXT,  // Find the next match of such message, not necessarily the closest one.
    SEEK_ADJ,   // Seek only to the adjacent term.

    MATCH_ALL,  // Given Pattern must match the whole message.
    MATCH_PART, // Given Pattern could match either whole or part of message.

    DISPOSE,    // Truncate matched content after matching. Remaining message will not be parsed to next matcher.
    RETAIN,     // Retain this message element after matching. It will be matched again by the next matcher.

    CATCH_ALL,  // Return all contents in this SingleMessage.
    CATCH_PART, // Returns only a part of the message element. Different matchers have different specification of "part".
    CATCH_NONE, // Match but do not catch the content. Result will be ignored.
    ;

    public static final EnumSet<MatchOptions> ALL_OPTS = EnumSet.allOf(MatchOptions.class);

    public static final EnumSet<MatchOptions> DEFAULT_OPTS = EnumSet.of (
            MatchOptions.SEEK_ADJ,
            MatchOptions.MATCH_PART,
            MatchOptions.DISPOSE,
            MatchOptions.CATCH_PART
    );
}
