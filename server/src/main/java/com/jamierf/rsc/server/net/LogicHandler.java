package com.jamierf.rsc.server.net;

import com.google.common.collect.Maps;
import com.jamierf.rsc.server.net.codec.packet.Packet;
import com.jamierf.rsc.server.net.session.Session;
import org.jboss.netty.channel.*;

import java.util.Map;

public class LogicHandler extends SimpleChannelUpstreamHandler {

    public static final String NAME = "logic-handler";

    private final Map<Class<? extends Packet>, PacketHandler> handlers;

    public LogicHandler() {
        handlers = Maps.newHashMap();
    }

    public void addPacketHandler(PacketHandler handler) {
        handlers.put(handler.getRequestType(), handler);
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        System.err.println(e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        final Packet packet = (Packet) e.getMessage();

        final PacketHandler handler = handlers.get(packet.getClass());
        // We have no handler for this packet type
        if (handler == null) {
            System.err.println("no handler");
            return;
        }

        final Session session = (Session) ctx.getAttachment();
        // We have no session and the handler for this packet type requires one
        if (session == null && handler.isSessionRequired()) {
            System.err.println("no client");

            ctx.getChannel().close();
            return;
        }

        System.err.println("Handling: " + packet);

        handler.handle(ctx, session, packet);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        final Throwable cause = e.getCause();
        cause.printStackTrace();
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        System.err.println(e);
    }
}
