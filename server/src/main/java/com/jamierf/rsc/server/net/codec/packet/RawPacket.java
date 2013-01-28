package com.jamierf.rsc.server.net.codec.packet;

import com.google.common.base.Objects;
import com.jamierf.rsc.server.net.codec.field.FieldCodec;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public abstract class RawPacket extends Packet {

    private ChannelBuffer buffer;

    public RawPacket() {
        buffer = ChannelBuffers.dynamicBuffer();
    }

    protected void write(Object value) throws PacketCodecException {
        FieldCodec.encode(value, buffer);
    }

    protected <T> T read(Class<T> type) throws PacketCodecException {
        return FieldCodec.decode(type, buffer);
    }

    protected abstract void decode() throws Exception;

    @Override
    protected void decode(ChannelBuffer buffer) throws Exception {
        this.buffer = buffer;

        this.decode();
    }

    @Override
    protected ChannelBuffer encode() throws Exception {
        return buffer;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("buffer", buffer)
                .toString();
    }
}
