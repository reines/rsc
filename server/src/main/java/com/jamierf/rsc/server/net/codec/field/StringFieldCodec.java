package com.jamierf.rsc.server.net.codec.field;

import com.jamierf.rsc.dataserver.api.UserCredentials;
import org.jboss.netty.buffer.ChannelBuffer;

public class StringFieldCodec extends FieldCodec<String> {

    private static final byte TERMINATOR = (byte) 10;

    @Override
    public String doDecode(ChannelBuffer buffer) throws FieldCodecException {
        final int length = buffer.bytesBefore(TERMINATOR);
        if (length < 1)
            throw new FieldCodecException("Malformed buffer, cannot read string with length: " + length);

        // Read the value then skip the terminator
        final String value = new String(buffer.readBytes(length).array(), UserCredentials.CHARSET);
        buffer.skipBytes(1);

        return value;
    }

    @Override
    public void doEncode(String value, ChannelBuffer buffer) throws FieldCodecException {
        buffer.writeBytes(value.getBytes(UserCredentials.CHARSET));
        buffer.writeByte(TERMINATOR);
    }
}
