package com.jamierf.rsc.server.net;

import com.google.common.collect.Maps;
import com.jamierf.rsc.server.net.codec.packet.Packet;
import com.jamierf.rsc.server.net.codec.packet.PacketCodecException;
import com.jamierf.rsc.server.net.session.Session;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Gauge;
import com.yammer.metrics.core.Meter;
import com.yammer.metrics.core.TimerContext;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LogicHandler extends SimpleChannelUpstreamHandler {

    public static final String NAME = "logic-handler";

    private static final Meter CONNECTION_METER = Metrics.newMeter(LogicHandler.class, "connections", "requests", TimeUnit.SECONDS);
    private static final Meter DISCONNECTION_METER = Metrics.newMeter(LogicHandler.class, "disconnections", "requests", TimeUnit.SECONDS);
    private static final Meter EXCEPTION_METER = Metrics.newMeter(LogicHandler.class, "exceptions", "errors", TimeUnit.SECONDS);
    private static final Meter UNHANDLED_PACKET_METER = Metrics.newMeter(LogicHandler.class, "unhandled-packets", "errors", TimeUnit.SECONDS);
    private static final Meter MALFORMED_PACKET_METER = Metrics.newMeter(LogicHandler.class, "malformed-packets", "errors", TimeUnit.SECONDS);
    private static final Meter MISSING_SESSION_METER = Metrics.newMeter(LogicHandler.class, "missing-sessions", "errors", TimeUnit.SECONDS);

    private final Map<Class<? extends Packet>, PacketHandler> handlers;
    private final ChannelGroup channels;

    public LogicHandler() {
        handlers = Maps.newHashMap();
        channels = new DefaultChannelGroup(LogicHandler.NAME);

        Metrics.newGauge(LogicHandler.class, "active-connections", new Gauge<Integer>() {
            @Override
            public Integer value() {
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
        CONNECTION_METER.mark();

        System.err.println(e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        final Packet packet = (Packet) e.getMessage();
        final Class<? extends Packet> type = packet.getClass();

        final Packet.Metrics metrics = Packet.getMetrics(type);
        final TimerContext timer = metrics.requestTimer.time();

        try {
            final PacketHandler handler = handlers.get(type);
            // We have no handler for this packet type
            if (handler == null) {
                UNHANDLED_PACKET_METER.mark();

                System.err.println("no handler"); // but we got this far which means it was a recognised packet type at least
                return;
            }

            final Session session = (Session) ctx.getAttachment();
            // We have no session and the handler for this packet type requires one
            if (session == null && handler.isSessionRequired()) {
                MISSING_SESSION_METER.mark();

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
        EXCEPTION_METER.mark();

        final Throwable cause = e.getCause();
        cause.printStackTrace();

        // If a client is sending bad packets disconnect them
        if (cause instanceof PacketCodecException) {
            MALFORMED_PACKET_METER.mark();
            ctx.getChannel().close();
        }
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        DISCONNECTION_METER.mark();

        System.err.println(e);

        // TODO: Remove this session
    }
}
