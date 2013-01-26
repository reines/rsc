package com.jamierf.rsc.server.net.handlers;

import com.jamierf.rsc.server.net.PacketHandler;
import com.jamierf.rsc.server.net.Session;
import com.jamierf.rsc.server.net.packet.SessionRequestPacket;
import com.jamierf.rsc.server.net.packet.SessionResponsePacket;
import org.jboss.netty.channel.ChannelHandlerContext;

public class SessionHandler extends PacketHandler<SessionRequestPacket> {

    @Override
    public Class<SessionRequestPacket> getRequestType() {
        return SessionRequestPacket.class;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, Session session, SessionRequestPacket packet) {
        // Create and attach a new session
        session = new Session(packet.getSessionId(), ctx.getChannel());
        ctx.setAttachment(session);

        System.err.println("Handling session request: " + session);

        // Send the session id back
        session.write(new SessionResponsePacket(session.getSessionId()));
    }

    @Override
    public boolean isSessionRequired() {
        return false;
    }
}
