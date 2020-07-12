package com.metricv.mirai.matcher;

import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.SingleMessage;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Regular expression matcher.
 *
 * Default options are: MATCH_ALL, SEEK_ADJ, DISPOSE, CATCH_MATC.
 *
 * Return Behaviors differ depending on CATCH option.
 * CATCH_ALL: Returns the whole string.
 * CATCH_MATC: Returns a java.util.regex.MatchResult that contains elements matched.
 *
 * MATCH_PART may not work well. It works by adding ".*" to the start and end to the pattern you provided.
 */
public class RegexMatcher implements Matcher {
    Pattern pattern;
    EnumSet<MatchOptions> opt;
    @RegExp String pattern_original = "";
    SingleMessage remainder = null;

    /**
     * Initialize a Regex matcher with patterns you compiled and default options.
     * This must match the whole message. No partial amendment will be performed.
     * @param pattern a java.util.regex.Pattern you compiled.
     */
    public RegexMatcher(@NotNull final Pattern pattern) {
        this.pattern_original = pattern.pattern();
        this.pattern = pattern;
        this.opt = EnumSet.of(
                MatchOptions.MATCH_ALL,
                MatchOptions.SEEK_ADJ,
                MatchOptions.DISPOSE,
                MatchOptions.CATCH_MATC
        );
    }

    /**
     * Initialize a Regex matcher with the settings you provide.
     * ONLY CHOOSE ONE FROM EACH CATEGORY. Unexpected things will occur if conflicting rules are present.
     * MATCH_PART will be ignored. The whole message must match the pattern you provide.
     * @param pattern A compiled pattern.
     * @param opt Options. Use Enum.of() to construct options.
     */
    public RegexMatcher(@NotNull final Pattern pattern, @NotNull final EnumSet<MatchOptions> opt) {
        this(pattern);
        this.opt = opt;
    }

    /**
     * Initialize a Regex matcher with default settings.
     * @param pattern The regular expression pattern.
     */
    public RegexMatcher(@RegExp final String pattern) {
        this(Pattern.compile(pattern));
    }

    /**
     * Initialize a Regex matcher with the settings you provide.
     * ONLY CHOOSE ONE FROM EACH CATEGORY. Unexpected things will occur if conflicting rules are present.
     * @param pattern The regular expression pattern.
     * @param opt Options. Use Enum.of() to construct options.
     */
    public RegexMatcher(@RegExp final String pattern, @NotNull final EnumSet<MatchOptions> opt) {
        this.pattern_original = pattern;
        if (opt.contains(MatchOptions.MATCH_PART)) {
            this.pattern = Pattern.compile(".*" + pattern + ".*");
        } else {
            this.pattern = Pattern.compile(pattern);
        }
        this.opt = opt;
    }

    public EnumSet<MatchOptions> getDefaultOpts() {
        return EnumSet.of(
                MatchOptions.MATCH_ALL,
                MatchOptions.SEEK_ADJ,
                MatchOptions.DISPOSE,
                MatchOptions.CATCH_MATC
        );
    }

    public EnumSet<MatchOptions> getCurrentOpts() {
        return this.opt;
    }

    @Override
    public boolean isMatch(final SingleMessage msg) {
        if (msg instanceof PlainText && pattern != null) {
            String content = ((PlainText) msg).getContent();
            return pattern.matcher(content).matches();
        } else {
            return false;
        }
    }

    @Override
    public Optional<Object> getMatch(final SingleMessage msg) {
        if(!(msg instanceof PlainText)) return Optional.empty();
        String content = ((PlainText) msg).getContent();
        java.util.regex.Matcher matcher = pattern.matcher(content);

        // Check if we are asked to match the whole thing.
        if(matcher.matches()) {
            if (opt.contains(MatchOptions.RETAIN)) {
                remainder = new PlainText(msg.contentToString());
            } else {    // DISPOSE.
                remainder = new PlainText(content.replaceFirst(pattern_original, "").trim());
            }

            if(opt.contains(MatchOptions.CATCH_ALL)) {
                return Optional.of(content);
            } else {
                return Optional.of(matcher);
            }
        } else {
            remainder = null;
            return Optional.empty();
        }
    }

    @Override
    public SingleMessage getMatchRemainder() {
        return remainder;
    }

    @Override
    public Optional<Object> seekMatch(@NotNull List<SingleMessage> msgChain) {
        int index_matched = -1;

        for (int index=0; index<msgChain.size(); index += 1) {
            if(msgChain.get(index) instanceof PlainText) {
                String content = ((PlainText) msgChain.get(index)).getContent();
                if(pattern.matcher(content).matches()) {
                    index_matched = index;
                    break;
                }
            }
        }

        if (index_matched != -1) {
            // Clear everything before the matched part.
            if (index_matched > 0) {
                msgChain.subList(0, index_matched).clear();
            }

            Optional<Object> result = getMatch(msgChain.get(0));

            // See if we are asked to retain the matched part, or truncate it.
            if(!opt.contains(MatchOptions.RETAIN)) {
                msgChain.set(0, remainder);
            }

            if(opt.contains(MatchOptions.CATCH_ALL)) {
                return Optional.of(msgChain.get(0).contentToString());
            } else {
                return result;
            }
        } else {
            return Optional.empty();
        }
    }
}
