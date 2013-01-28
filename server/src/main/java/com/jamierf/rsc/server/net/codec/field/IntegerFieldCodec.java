package com.jamierf.rsc.server.net.codec.field;

import org.jboss.netty.buffer.ChannelBuffer;

public class IntegerFieldCodec extends FieldCodec<Integer> {
    @Override
    public Integer doDecode(ChannelBuffer buffer) {
        return buffer.readInt(); // TODO: Unsigned
    }

    @Override
    public void doEncode(Integer value, ChannelBuffer buffer) {
        buffer.writeInt(value); // TODO: Unsigned
    }
}
