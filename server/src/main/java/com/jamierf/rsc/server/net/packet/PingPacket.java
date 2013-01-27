package com.jamierf.rsc.server.net.packet;

import com.google.common.base.Objects;
import com.jamierf.rsc.server.net.codec.packet.Packet;

public class PingPacket extends Packet {

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .toString();
    }
}
