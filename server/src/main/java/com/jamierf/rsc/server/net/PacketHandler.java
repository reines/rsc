package com.jamierf.rsc.server.net;

import com.jamierf.rsc.server.net.codec.packet.Packet;
import org.jboss.netty.channel.ChannelHandlerContext;

public abstract class PacketHandler<T extends Packet> {

    public abstract Class<T> getRequestType();

    public abstract void handle(ChannelHandlerContext ctx, Session session, T packet) throws Exception;

    public boolean isSessionRequired() {
        return true;
    }
}
