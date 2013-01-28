package com.jamierf.rsc.server.net.codec.field;

import org.jboss.netty.buffer.ChannelBuffer;

public class ChannelBufferFieldCodec extends FieldCodec<ChannelBuffer> {
    @Override
    public ChannelBuffer doDecode(ChannelBuffer buffer) {
        return buffer;
    }

    @Override
    public void doEncode(ChannelBuffer value, ChannelBuffer buffer) {
        buffer.writeBytes(value);
    }
}
