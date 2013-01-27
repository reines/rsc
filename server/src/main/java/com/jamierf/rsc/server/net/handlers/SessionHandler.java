package com.jamierf.rsc.server.net.handlers;

import com.jamierf.rsc.server.net.PacketHandler;
import com.jamierf.rsc.server.net.packet.SessionRequestPacket;
import com.jamierf.rsc.server.net.session.Session;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;

public class SessionHandler extends PacketHandler<SessionRequestPacket> {
    @Override
    public Class<SessionRequestPacket> getRequestType() {
        return SessionRequestPacket.class;
    }

    @Override
    public boolean isSessionRequired() {
        return false;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, Session session, SessionRequestPacket packet) throws Exception {
        final ChannelBuffer payload = ChannelBuffers.buffer(Long.SIZE);
        payload.writeLong(1L);

        ctx.getChannel().write(payload).sync();
    }
}
