package com.jamierf.rsc.server.net.packet;

import com.google.common.base.Objects;
import com.jamierf.rsc.server.net.codec.packet.RawPacket;
import com.jamierf.rsc.server.net.handlers.LoginHandler;
import org.jboss.netty.buffer.ChannelBuffers;

public class LoginResponsePacket extends RawPacket {

    private final LoginHandler.LoginStatus status;

    public LoginResponsePacket(LoginHandler.LoginStatus status) {
        this.status = status;

        super.buffer = ChannelBuffers.buffer(Byte.SIZE);
        super.buffer.writeByte(status.getCode());
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("status", status)
                .toString();
    }
}
