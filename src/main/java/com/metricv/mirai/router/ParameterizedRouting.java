package com.metricv.mirai.router;

import com.metricv.mirai.matcher.MatchOptions;
import net.mamoe.mirai.message.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageSource;
import net.mamoe.mirai.message.data.SingleMessage;
import org.bouncycastle.pqc.crypto.MessageEncryptor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A parametrized routing. Constructed by adding parameters to a {@link Routing} with {@see Routing::putParam()}.
 */
public class ParameterizedRouting extends Routing {
    Map<String, Object> parameters;

    /**
     * Copy constructor.
     * @param routing Routing to copy from
     */
    protected ParameterizedRouting(@NotNull Routing routing) {
        this.matcherChain = routing.matcherChain;
        this.target = routing.target;
        this.matchParallel = routing.matchParallel;
    }

    /**
     * Add parameter to this routing.
     * @param name Name of the parameter
     * @param param Value.
     * @return self
     */
    public ParameterizedRouting putParam(String name, Object param) {
        parameters.put(name, param);
        return this;
    }

    /**
     * Attach parameters, then accept an event.
     * @param event {@link MessageEvent} from mirai.
     */
    @Override
    public void startRouting(@NotNull MessageEvent event) {
        RoutingResult initialResult = new RoutingResult();
        initialResult.eventSource = event;

        for(Map.Entry<String, Object> e : parameters.entrySet()) {
            initialResult.put(e.getKey(), e.getValue());
        }

        startRouting(event, initialResult);
    }
}
