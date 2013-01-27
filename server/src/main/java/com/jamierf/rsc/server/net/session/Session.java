package com.jamierf.rsc.server.net.session;

import com.google.common.base.Objects;
import com.jamierf.rsc.server.net.codec.packet.PacketRotator;
import com.jamierf.rsc.server.net.codec.packet.Packet;
import com.jamierf.rsc.server.net.codec.packet.PacketDecoder;
import com.jamierf.rsc.server.net.codec.packet.PacketEncoder;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;

import java.io.Closeable;

public class Session implements Closeable {

    private final long id;
    private final String username;
    private final PacketRotator packetRotator;

    private Channel channel;

    public Session(long id, String username, PacketRotator packetRotator) {
        this.id = id;
        this.username = username;
        this.packetRotator = packetRotator;
    }

    protected synchronized boolean moveChannel(Channel channel, boolean allowExisting) {
        if (this.channel != null) {
            // There is already an existing channel, and we don't allow that
            if (!allowExisting)
                return false;

            // Ensure that the old connection is dead
            this.channel.close().awaitUninterruptibly();
        }

        // Enable packet rotation on the packet decoder
        final PacketDecoder decoder = (PacketDecoder) channel.getPipeline().get(PacketDecoder.NAME);
        decoder.setPacketRotator(packetRotator);

        // Enable packet rotation on the packet encoder
        final PacketEncoder encoder = (PacketEncoder) channel.getPipeline().get(PacketEncoder.NAME);
        encoder.setPacketRotator(packetRotator);

        this.channel = channel;
        return true;
    }

    public String getUsername() {
        return username;
    }

    public long getId() {
        return id;
    }

    public ChannelFuture write(Packet packet) {
        return channel.write(packet);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("channel", channel)
                .add("username", username)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Session session = (Session) o;

        if (id != session.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public void close() {
        channel.close();
    }
}
