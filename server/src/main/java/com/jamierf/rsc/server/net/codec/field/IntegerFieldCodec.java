package com.jamierf.rsc.server.net.codec.field;

import org.jboss.netty.buffer.ChannelBuffer;

public class IntegerFieldCodec extends FieldCodec<Integer> {
    @Override
    public Integer decode(ChannelBuffer buffer) {
        return buffer.readInt();
    }

    @Override
    public void encode(Integer value, ChannelBuffer buffer) {
        buffer.writeInt(value);
    }
}
