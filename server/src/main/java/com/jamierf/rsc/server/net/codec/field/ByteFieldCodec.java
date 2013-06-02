package com.jamierf.rsc.server.net.codec.field;

import org.jboss.netty.buffer.ChannelBuffer;

public class ByteFieldCodec extends FieldCodec<Byte> {
    @Override
    public Byte doDecode(ChannelBuffer buffer) {
        return buffer.readByte();
    }

    @Override
    public void doEncode(Byte value, ChannelBuffer buffer) {
        buffer.writeByte(value);
    }
}
