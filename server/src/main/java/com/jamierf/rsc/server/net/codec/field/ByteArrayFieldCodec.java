package com.jamierf.rsc.server.net.codec.field;

import org.jboss.netty.buffer.ChannelBuffer;

public class ByteArrayFieldCodec extends FieldCodec<byte[]> {
    @Override
    public byte[] decode(ChannelBuffer buffer) {
        final int length = buffer.readShort();

        final byte[] bytes = new byte[length];
        buffer.readBytes(bytes);

        return bytes;
    }

    @Override
    public void encode(byte[] value, ChannelBuffer buffer) {
        buffer.writeShort(value.length);
        buffer.writeBytes(value);
    }
}
