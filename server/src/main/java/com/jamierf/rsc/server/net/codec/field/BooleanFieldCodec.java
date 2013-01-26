package com.jamierf.rsc.server.net.codec.field;

import org.jboss.netty.buffer.ChannelBuffer;

public class BooleanFieldCodec extends FieldCodec<Boolean> {
    @Override
    public Boolean decode(ChannelBuffer buffer) {
        return buffer.readByte() == 1;
    }

    @Override
    public void encode(Boolean value, ChannelBuffer buffer) {
        buffer.writeByte(value ? 1 : 0);
    }
}
