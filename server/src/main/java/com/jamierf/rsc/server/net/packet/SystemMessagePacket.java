package com.jamierf.rsc.server.net.packet;

import com.google.common.base.Objects;
import com.jamierf.rsc.dataserver.api.UserCredentials;
import com.jamierf.rsc.server.net.codec.packet.PacketCodecException;
import com.jamierf.rsc.server.net.codec.packet.RawPacket;

public class SystemMessagePacket extends RawPacket {

    private final String message;

    public SystemMessagePacket(String message) throws PacketCodecException {
        this.message = message;

        super.write(message.getBytes(UserCredentials.CHARSET)); // TODO: Common place for CHARSET
    }

    @Override
    protected void decode() throws Exception {
        // NOOP
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("message", message)
                .toString();
    }
}
