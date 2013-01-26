package com.jamierf.rsc.server.net.codec.field;

import org.jboss.netty.buffer.ChannelBuffer;

public class ChannelBufferFieldCodec extends FieldCodec<ChannelBuffer> {
    @Override
    public ChannelBuffer decode(ChannelBuffer buffer) {
        return buffer;
    }

    @Override
    public void encode(ChannelBuffer value, ChannelBuffer buffer) {
        buffer.writeBytes(value);
    }
}
