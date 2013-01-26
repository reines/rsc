package com.jamierf.rsc.server.net.codec.field;

import org.jboss.netty.buffer.ChannelBuffer;

public class ShortFieldCodec extends FieldCodec<Short> {
    @Override
    public Short decode(ChannelBuffer buffer) {
        return buffer.readShort();
    }

    @Override
    public void encode(Short value, ChannelBuffer buffer) {
        buffer.writeShort(value);
    }
}
