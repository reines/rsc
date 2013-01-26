package com.jamierf.rsc.server.net.codec.field;

import org.jboss.netty.buffer.ChannelBuffer;

public class StringFieldCodec extends FieldCodec<String> {

    private static final byte TERMINATOR = (byte) 10;

    @Override
    public String decode(ChannelBuffer buffer) {
        final int length = buffer.bytesBefore(TERMINATOR);

        // Read the value then skip the terminator
        final String value = new String(buffer.readBytes(length).array());
        buffer.skipBytes(1);

        return value;
    }

    @Override
    public void encode(String value, ChannelBuffer buffer) {
        buffer.writeBytes(value.getBytes());
        buffer.writeByte(TERMINATOR);
    }
}
