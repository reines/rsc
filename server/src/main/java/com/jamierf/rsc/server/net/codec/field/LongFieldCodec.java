package com.jamierf.rsc.server.net.codec.field;

import org.jboss.netty.buffer.ChannelBuffer;

public class LongFieldCodec extends FieldCodec<Long> {
    @Override
    public Long decode(ChannelBuffer buffer) {
        return buffer.readLong();
    }

    @Override
    public void encode(Long value, ChannelBuffer buffer) {
        buffer.writeLong(value);
    }
}
