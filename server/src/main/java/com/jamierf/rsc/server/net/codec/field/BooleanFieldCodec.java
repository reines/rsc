package com.jamierf.rsc.server.net.codec.field;

import org.jboss.netty.buffer.ChannelBuffer;

public class BooleanFieldCodec extends FieldCodec<Boolean> {
    @Override
    public Boolean doDecode(ChannelBuffer buffer) throws FieldCodecException {
        return FieldCodec.decode(byte.class, buffer) == 1;
    }

    @Override
    public void doEncode(Boolean value, ChannelBuffer buffer) throws FieldCodecException {
        FieldCodec.encode((byte) (value ? 1 : 0), buffer);
    }
}
