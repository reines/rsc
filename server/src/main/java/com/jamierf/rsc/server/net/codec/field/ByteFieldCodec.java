package com.jamierf.rsc.server.net.codec.field;

import org.jboss.netty.buffer.ChannelBuffer;

public class ByteFieldCodec extends FieldCodec<Byte> {
    @Override
    public Byte decode(ChannelBuffer buffer) {
        return buffer.readByte();
    }

    @Override
    public void encode(Byte value, ChannelBuffer buffer) {
        buffer.writeByte(value);
    }
}
