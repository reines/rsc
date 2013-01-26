package com.jamierf.rsc.server.net.packet;

import com.google.common.base.Objects;
import com.jamierf.rsc.server.net.codec.packet.Packet;
import com.jamierf.rsc.server.net.codec.packet.PacketDecoder;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.io.IOException;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;

public class LoginRequestPacket extends Packet {

    public static class SessionCredentials extends Packet {

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

        public char[] getPassword() {
            return password.trim().toCharArray();
        }
    }

    private boolean reconnecting;
    private short clientVersion;
    private boolean limit30;
    private byte[] encrypted;

    public boolean isReconnecting() {
        return reconnecting;
    }

    public short getClientVersion() {
        return clientVersion;
    }

    public boolean isLimit30() {
        return limit30;
    }

    public SessionCredentials decryptSessionCredentials(RSAPrivateKey key) throws IllegalAccessException, IOException, InstantiationException, NoSuchFieldException {
        final BigInteger encrypted = new BigInteger(this.encrypted);
        final BigInteger decrypted = encrypted.modPow(key.getPrivateExponent(), key.getModulus());

        final ChannelBuffer payload = ChannelBuffers.wrappedBuffer(decrypted.toByteArray());
        return PacketDecoder.decodePacket(SessionCredentials.class, payload);
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
