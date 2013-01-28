package com.jamierf.rsc.server.net.codec.field;

import org.jboss.netty.buffer.ChannelBuffer;

public class ByteArrayFieldCodec extends FieldCodec<byte[]> {
    @Override
    public byte[] doDecode(ChannelBuffer buffer) throws FieldCodecException {
        final int length = buffer.readShort();

        final byte[] bytes = new byte[length];
        buffer.readBytes(bytes);

        return bytes;
    }

    @Override
    public void doEncode(byte[] value, ChannelBuffer buffer) throws FieldCodecException {
        buffer.writeShort(value.length);
        buffer.writeBytes(value);
    }
}
