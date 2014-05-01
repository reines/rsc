package com.jamierf.rsc.server.net.packet;

import com.google.common.base.Objects;
import com.jamierf.rsc.server.net.codec.packet.Packet;
import com.jamierf.rsc.server.net.codec.packet.PacketBuffer;
import com.jamierf.rsc.server.net.codec.packet.PacketDecoder;
import com.jamierf.rsc.server.net.crypto.RSACipher;
import com.jamierf.rsc.server.net.crypto.XTEACipher;
import org.jboss.netty.buffer.ChannelBuffer;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;

public class LoginRequestPacket extends Packet {

    public static class SessionData extends Packet {

        private byte unknown1;
        private int sessionKey1;
        private int sessionKey2;
        private int sessionKey3;
        private int sessionKey4;
        private String password;

        public SessionData() {}

        public int[] getSessionKeys() {
            return new int[]{ sessionKey1, sessionKey2, sessionKey3, sessionKey4 };
        }

        public String getPassword() {
            return password.trim();
        }

        @Override
        protected void decode(PacketBuffer buffer) throws IOException {
            unknown1 = buffer.read(byte.class);
            sessionKey1 = buffer.read(int.class);
            sessionKey2 = buffer.read(int.class);
            sessionKey3 = buffer.read(int.class);
            sessionKey4 = buffer.read(int.class);
            password = buffer.read(String.class);
        }

        @Override
        protected void encode(PacketBuffer buffer) throws IOException {
            buffer.write(unknown1);
            buffer.write(sessionKey1);
            buffer.write(sessionKey2);
            buffer.write(sessionKey3);
            buffer.write(sessionKey4);
            buffer.write(password);
        }
    }

    public static class LoginData extends Packet {

        private boolean limit30;
        private String username;

        public LoginData() {}

        public boolean isLimit30() {
            return limit30;
        }

        public String getUsername() {
            return username.trim();
        }

        @Override
        protected void decode(PacketBuffer buffer) throws IOException {
            limit30 = buffer.read(boolean.class);
            username = buffer.read(String.class);
        }

        @Override
        protected void encode(PacketBuffer buffer) throws IOException {
            buffer.write(limit30);
            buffer.write(username);
        }
    }

    private boolean reconnecting;
    private int clientVersion;
    private byte[] sessionData;
    private byte[] loginData;

    public LoginRequestPacket() {}

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
    protected void decode(PacketBuffer buffer) throws IOException {
        reconnecting = buffer.read(boolean.class);
        clientVersion = buffer.read(int.class);
        sessionData = buffer.read(byte[].class);
        loginData = buffer.read(byte[].class);
    }

    @Override
    protected void encode(PacketBuffer buffer) throws IOException {
        buffer.write(reconnecting);
        buffer.write(clientVersion);
        buffer.write(sessionData);
        buffer.write(loginData);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("reconnecting", reconnecting)
                .add("clientVersion", clientVersion)
                .toString();
    }
}
