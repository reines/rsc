package com.jamierf.rsc.server.net.codec.field;

import org.jboss.netty.buffer.ChannelBuffer;

public class ShortFieldCodec extends FieldCodec<Short> {
    @Override
    public Short doDecode(ChannelBuffer buffer) {
        return buffer.readShort(); // TODO: Unsigned
    }

    @Override
    public void doEncode(Short value, ChannelBuffer buffer) {
        buffer.writeShort(value); // TODO: Unsigned
    }
}
