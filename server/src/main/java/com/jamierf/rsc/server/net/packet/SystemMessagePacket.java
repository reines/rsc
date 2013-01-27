package com.jamierf.rsc.server.net.packet;

import com.google.common.base.Objects;
import com.jamierf.rsc.dataserver.api.UserCredentials;
import com.jamierf.rsc.server.net.codec.packet.Packet;

public class SystemMessagePacket extends Packet {

    private byte[] message;

    public SystemMessagePacket(String message) {
        this.message = message.getBytes(UserCredentials.CHARSET);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("message", new String(message, UserCredentials.CHARSET))
                .toString();
    }
}
