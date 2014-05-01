package com.jamierf.rsc.server.net;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.collect.Maps;
import com.jamierf.rsc.server.net.codec.packet.Packet;
import com.jamierf.rsc.server.net.codec.packet.PacketCodecException;
import com.jamierf.rsc.server.net.session.Session;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import java.util.Map;

public class LogicHandler extends SimpleChannelUpstreamHandler {

    public static final String NAME = "logic-handler";

    private static final String CONNECTION_METER_NAME = "connections";
    private static final String DISCONNECTION_METER_NAME = "disconnections";
    private static final String EXCEPTION_METER_NAME = "exceptions";
    private static final String UNHANDLED_PACKET_METER_NAME = "unhandled-packets";
    private static final String MALFORMED_PACKET_METER_NAME = "malformed-packets";
    private static final String MISSING_SESSION_METER_NAME = "missing-sessions";
    private static final String REQUEST_TIMER_TEMPLATE = "request-%s";

    private final MetricRegistry metricRegistry;
    private final Map<Class<? extends Packet>, PacketHandler> handlers;
    private final ChannelGroup channels;

    public LogicHandler(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;

        handlers = Maps.newHashMap();
        channels = new DefaultChannelGroup(LogicHandler.NAME);

        metricRegistry.register("active-connections", new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return channels.size();
            }
        });
    }

    public void addPacketHandler(PacketHandler handler) {
        handlers.put(handler.getRequestType(), handler);
    }

    public ChannelGroupFuture closeChannels() {
        return channels.close();
    }

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        channels.add(e.getChannel());
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        metricRegistry.meter(CONNECTION_METER_NAME).mark();

        System.err.println(e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        final Packet packet = (Packet) e.getMessage();
        final Class<? extends Packet> type = packet.getClass();

        final Timer.Context timer = metricRegistry.timer(String.format(REQUEST_TIMER_TEMPLATE, type.getSimpleName())).time();

        try {
            final PacketHandler handler = handlers.get(type);
            // We have no handler for this packet type
            if (handler == null) {
                metricRegistry.meter(UNHANDLED_PACKET_METER_NAME).mark();

                System.err.println("no handler"); // but we got this far which means it was a recognised packet type at least
                return;
            }

            final Session session = (Session) ctx.getAttachment();
            // We have no session and the handler for this packet type requires one
            if (session == null && handler.isSessionRequired()) {
                metricRegistry.meter(MISSING_SESSION_METER_NAME).mark();

                System.err.println("no client");

                ctx.getChannel().close();
                return;
            }

            System.err.println("Handling: " + packet);

            handler.handle(ctx, session, packet);
        }
        finally {
            timer.stop();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        metricRegistry.meter(EXCEPTION_METER_NAME).mark();

        final Throwable cause = e.getCause();
        cause.printStackTrace();

        // If a client is sending bad packets disconnect them
        if (cause instanceof PacketCodecException) {
            metricRegistry.meter(MALFORMED_PACKET_METER_NAME).mark();
            ctx.getChannel().close();
        }
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        metricRegistry.meter(DISCONNECTION_METER_NAME).mark();

        System.err.println(e);
    }
}
