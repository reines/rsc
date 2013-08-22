package com.jamierf.rsc.server.net.packet;

import com.google.common.base.Objects;
import com.jamierf.rsc.server.net.codec.packet.Packet;
import com.jamierf.rsc.server.net.codec.packet.PacketBuffer;

import java.io.IOException;

public class PingPacket extends Packet {

    public PingPacket() {}

    @Override
    protected void decode(PacketBuffer buffer) throws IOException {

    }

    @Override
    protected void encode(PacketBuffer buffer) throws IOException {

    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .toString();
    }
}
