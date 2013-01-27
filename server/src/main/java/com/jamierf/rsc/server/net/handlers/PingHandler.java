package com.jamierf.rsc.server.net.handlers;

import com.jamierf.rsc.server.net.PacketHandler;
import com.jamierf.rsc.server.net.packet.PingPacket;
import com.jamierf.rsc.server.net.session.Session;
import org.jboss.netty.channel.ChannelHandlerContext;

public class PingHandler extends PacketHandler<PingPacket> {
    @Override
    public Class<PingPacket> getRequestType() {
        return PingPacket.class;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, Session session, PingPacket packet) throws Exception {
        System.out.println(session + " PING");
    }
}
