package com.jamierf.rsc.server.net.codec.field;

import org.jboss.netty.buffer.ChannelBuffer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class StringFieldCodec extends FieldCodec<String> {

    public static final Charset CHARSET = Charset.forName("UTF-8");

    private static final byte TERMINATOR = (byte) 0;

    @Override
    public String decode(ChannelBuffer buffer) throws IOException {
        final int length = buffer.bytesBefore(TERMINATOR);
        if (length < 1)
            throw new IOException("Malformed buffer, cannot read string with length: " + length);

        // Read the value then skip the terminator
        final String value = new String(buffer.readBytes(length).array(), CHARSET);
        buffer.skipBytes(1);

        return value;
    }

    @Override
    public void encode(String value, ChannelBuffer buffer) throws UnsupportedEncodingException {
        buffer.writeBytes(value.getBytes(CHARSET));
        buffer.writeByte(TERMINATOR);
    }
}
