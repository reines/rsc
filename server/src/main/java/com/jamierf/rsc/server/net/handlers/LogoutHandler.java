package com.jamierf.rsc.server.net.handlers;

import com.jamierf.rsc.server.net.PacketHandler;
import com.jamierf.rsc.server.net.session.Session;
import com.jamierf.rsc.server.net.packet.LogoutRequestPacket;
import org.jboss.netty.channel.ChannelHandlerContext;

public class LogoutHandler extends PacketHandler<LogoutRequestPacket> {

    @Override
    public Class<LogoutRequestPacket> getRequestType() {
        return LogoutRequestPacket.class;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, Session session, LogoutRequestPacket packet) {
        System.err.println("Handling logout: " + packet);

        // TODO: We're actually meant to send back a logged out packet I think?

        session.close();
    }
}
