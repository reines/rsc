package com.jamierf.rsc.server.net.codec.packet;

import com.google.common.collect.Maps;
import com.jamierf.rsc.server.net.codec.field.FieldCodec;
import com.yammer.metrics.core.Timer;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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

    private static void setField(Field field, Packet packet, ChannelBuffer buffer) throws Exception {
        final Class<?> type = field.getType();
        final boolean accessible = field.isAccessible();

        field.setAccessible(true);
        field.set(packet, FieldCodec.decode(type, buffer));
        field.setAccessible(accessible);
    }

    private static void getField(Field field, Packet packet, ChannelBuffer buffer) throws Exception {
        final boolean accessible = field.isAccessible();

        field.setAccessible(true);
        final Object value = field.get(packet);
        field.setAccessible(accessible);

        FieldCodec.encode(value, buffer);
    }

    protected void decode(ChannelBuffer buffer) throws Exception {
        // For every field attempt to decode it
        for (Field field : this.getClass().getDeclaredFields()) {
            final int modifiers = field.getModifiers();
            // Skip static fields
            if (Modifier.isStatic(modifiers))
                continue;

            Packet.setField(field, this, buffer);
        }
    }

    protected ChannelBuffer encode() throws Exception {
        final ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();

        // For every field attempt to encode it
        for (Field field : this.getClass().getDeclaredFields()) {
            final int modifiers = field.getModifiers();
            // Skip static fields
            if (Modifier.isStatic(modifiers))
                continue;

            Packet.getField(field, this, buffer);
        }

        return buffer;
    }
}
