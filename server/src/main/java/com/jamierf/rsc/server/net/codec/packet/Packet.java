package com.jamierf.rsc.server.net.codec.packet;

import com.google.common.collect.Maps;
import com.yammer.metrics.core.Timer;

import java.io.IOException;
import java.util.Map;

public abstract class Packet {

    public static class Metrics {
        public final Timer requestTimer;

        public Metrics(Class<? extends Packet> type) {
            requestTimer = com.yammer.metrics.Metrics.newTimer(type, "request");
        }
    }

    private static final Map<Class<? extends Packet>, Metrics> METRICS = Maps.newHashMap();

    public static Metrics getMetrics(Class<? extends Packet> type) {
        synchronized (METRICS) {
            if (!METRICS.containsKey(type))
                METRICS.put(type, new Metrics(type));

            return METRICS.get(type);
        }
    }

    protected abstract void decode(PacketBuffer buffer) throws IOException;
    protected abstract void encode(PacketBuffer buffer) throws IOException;
}
