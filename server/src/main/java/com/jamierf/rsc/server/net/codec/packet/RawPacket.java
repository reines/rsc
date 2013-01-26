package com.jamierf.rsc.server.net.codec.packet;

import com.google.common.base.Objects;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class RawPacket extends Packet {

    protected ChannelBuffer buffer;

    public RawPacket() {
        buffer = ChannelBuffers.EMPTY_BUFFER;
    }

    protected RawPacket(int size) {
        buffer = ChannelBuffers.buffer(size);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("buffer", buffer)
                .toString();
    }
}
