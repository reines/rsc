package com.jamierf.rsc.server.net;

import com.google.common.base.Objects;
import com.jamierf.rsc.server.net.codec.PacketRotator;
import com.jamierf.rsc.server.net.codec.packet.Packet;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;

import java.io.Closeable;
import java.security.SecureRandom;

public class Session implements Closeable {

    private static final SecureRandom random = new SecureRandom();

    private static long generateUniqueId() {
        return random.nextLong();
    }

    private final Channel channel;
    private final String username;
    private final long sessionId;
    private final PacketRotator packetRotator;

    public Session(Channel channel, String username, int[] sessionKeys) {
        this.channel = channel;
        this.username = username;

        sessionId = Session.generateUniqueId();
        packetRotator = new PacketRotator(sessionKeys);
    }

    public String getUsername() {
        return username;
    }

    public long getSessionId() {
        return sessionId;
    }

    public PacketRotator getPacketRotator() {
        return packetRotator;
    }

    public ChannelFuture write(Packet packet) {
        return channel.write(packet);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("sessionId", sessionId)
                .add("channel", channel)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Session session = (Session) o;

        if (sessionId != session.sessionId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (sessionId ^ (sessionId >>> 32));
    }

    @Override
    public void close() {
        channel.close();
    }
}
