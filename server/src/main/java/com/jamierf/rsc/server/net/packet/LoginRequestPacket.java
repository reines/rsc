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
        private String password;

        public int[] getSessionKeys() {
            return new int[]{ sessionKey1, sessionKey2, sessionKey3, sessionKey4 };
        }

        public String getPassword() {
            return password.trim();
        }
    }

    public static class LoginData extends Packet {

        private boolean limit30;
        private String username;

        public boolean isLimit30() {
            return limit30;
        }

        public String getUsername() {
            return username.trim();
        }

    }

    private boolean reconnecting;
    private int clientVersion;
    private byte[] sessionData;
    private byte[] loginData;

    public boolean isReconnecting() {
        return reconnecting;
    }

    public int getClientVersion() {
        return clientVersion;
    }

    public SessionData decryptSessionData(RSAPrivateKey key) throws Exception {
        final ChannelBuffer payload = RSACipher.decrypt(sessionData, key);
        return PacketDecoder.decodePacket(SessionData.class, payload);
    }

    public LoginData decryptLoginData(int[] key) throws Exception {
        final ChannelBuffer payload = XTEACipher.decrypt(loginData, key);

        // Skip the first 24 bytes, they are padding...
        payload.skipBytes(24);

        return PacketDecoder.decodePacket(LoginData.class, payload);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("reconnecting", reconnecting)
                .add("clientVersion", clientVersion)
                .toString();
    }
}
