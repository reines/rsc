package com.jamierf.rsc.server.net.packet;

import com.google.common.base.Objects;
import com.jamierf.rsc.server.net.codec.packet.Packet;

public class SessionRequestPacket extends Packet {

    private byte sessionId;

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("sessionId", sessionId)
                .toString();
    }
}
