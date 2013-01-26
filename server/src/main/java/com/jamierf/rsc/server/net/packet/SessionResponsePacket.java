package com.jamierf.rsc.server.net.packet;

import com.google.common.base.Objects;
import com.jamierf.rsc.server.net.codec.packet.RawPacket;
import org.jboss.netty.buffer.ChannelBuffers;

public class SessionResponsePacket extends RawPacket {

    private final long sessionId;

    public SessionResponsePacket(long sessionId) {
        this.sessionId = sessionId;

        super.buffer = ChannelBuffers.buffer(Long.SIZE);
        super.buffer.writeLong(sessionId);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("sessionId", sessionId)
                .toString();
    }
}
