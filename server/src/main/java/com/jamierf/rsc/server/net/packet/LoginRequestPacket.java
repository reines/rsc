package com.jamierf.rsc.server.net.packet;

import com.google.common.base.Objects;
import com.jamierf.rsc.server.net.codec.packet.Packet;
import com.jamierf.rsc.server.net.codec.packet.PacketDecoder;
import com.jamierf.rsc.server.net.crypto.RSACipher;
import com.jamierf.rsc.server.net.crypto.XTEACipher;
import org.jboss.netty.buffer.ChannelBuffer;

import java.security.interfaces.RSAPrivateKey;

public class LoginRequestPacket extends Packet {

    public static class SessionData extends Packet {

        private byte unknown1;
        private int sessionKey1;
        private int sessionKey2;
        private int sessionKey3;
        private int sessionKey4;
        private int unknown2;
        private String username;
        private String password;

        public int[] getSessionKeys() {
            return new int[]{ sessionKey1, sessionKey2, sessionKey3, sessionKey4 };
        }

        public String getUsername() {
            return username.trim();
        }

        public String getPassword() {
            return password.trim();
        }
    }

    private boolean reconnecting;
    private short clientVersion;
    private boolean limit30;
    private byte[] sessionData;

    public boolean isReconnecting() {
        return reconnecting;
    }

    public int getClientVersion() {
        return clientVersion;
    }

    public boolean isLimit30() {
        return limit30;
    }

    public SessionData decryptSessionData(RSAPrivateKey key) throws Exception {
        final ChannelBuffer payload = RSACipher.decrypt(sessionData, key);
        return PacketDecoder.decodePacket(SessionData.class, payload);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("reconnecting", reconnecting)
                .add("clientVersion", clientVersion)
                .add("limit30", limit30)
                .toString();
    }
}
