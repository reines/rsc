package com.jamierf.rsc.server.net.packet;

import com.google.common.base.Objects;
import com.jamierf.rsc.server.net.codec.packet.Packet;
import com.jamierf.rsc.server.net.codec.packet.PacketBuffer;

import java.io.IOException;

public class SystemMessagePacket extends Packet {

    private String message;

    protected SystemMessagePacket() {}

    public SystemMessagePacket(String message) {
        this.message = message;
    }

    @Override
    protected void decode(PacketBuffer buffer) throws IOException {
        message = buffer.read(String.class);
    }

    @Override
    protected void encode(PacketBuffer buffer) throws IOException {
        buffer.write(message);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("message", message)
                .toString();
    }
}
