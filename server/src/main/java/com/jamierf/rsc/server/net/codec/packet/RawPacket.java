package com.jamierf.rsc.server.net.codec.packet;

import com.google.common.base.Objects;
import org.jboss.netty.buffer.ChannelBuffer;

public class RawPacket extends Packet {

    protected ChannelBuffer buffer;

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("buffer", buffer)
                .toString();
    }
}
