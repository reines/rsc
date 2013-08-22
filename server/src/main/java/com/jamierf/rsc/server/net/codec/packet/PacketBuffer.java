package com.jamierf.rsc.server.net.codec.packet;

import com.jamierf.rsc.server.net.codec.field.FieldCodec;
import org.jboss.netty.buffer.ChannelBuffer;

import java.io.IOException;

public class PacketBuffer {

    private final ChannelBuffer buffer;

    public static PacketBuffer wrap(ChannelBuffer buffer) {
        return new PacketBuffer(buffer);
    }

    private PacketBuffer(ChannelBuffer buffer) {
        this.buffer = buffer;
    }

    public <T> T read(Class<T> type) throws IOException {
        return FieldCodec.decode(type, buffer);
    }

    public void write(Object value) throws IOException {
        FieldCodec.encode(value, buffer);
    }
}
