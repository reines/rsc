package com.jamierf.rsc.server.net.codec.packet;

import com.jamierf.rsc.server.net.codec.field.FieldCodec;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Histogram;
import com.yammer.metrics.core.Meter;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PacketEncoder extends OneToOneEncoder {

    public static final String NAME = "packet-encoder";

    private static final Meter UNRECOGNISED_PACKET_METER = Metrics.newMeter(PacketEncoder.class, "unrecognised-packets", "errors", TimeUnit.SECONDS);
    private static final Histogram PACKET_SIZE_HISTOGRAM = Metrics.newHistogram(PacketEncoder.class, "packet-size");

    private static void writeLength(int length, ChannelBuffer buffer) {
        if (length >= 160) {
            buffer.writeByte(160 + (length / 256));
            buffer.writeByte(length & 0xff);
        }
        else {
            buffer.writeByte(length);
        }
    }

    private final Map<Class<? extends Packet>, Integer> packetTypes;
    private PacketRotator packetRotator;

    public PacketEncoder(Map<Class<? extends Packet>, Integer> packetTypes) {
        this.packetTypes = packetTypes;

        packetRotator = null;
    }

    public void setPacketRotator(PacketRotator packetRotator) {
        this.packetRotator = packetRotator;
    }

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        // Allow channel buffers through directly
        if (msg instanceof ChannelBuffer)
            return msg;

        final Packet packet = (Packet) msg;

        final Class<? extends Packet> type = packet.getClass();
        if (!packetTypes.containsKey(type)) {
            UNRECOGNISED_PACKET_METER.mark();
            throw new PacketCodecException("Unrecognised packet type: " + type);
        }

        int id = packetTypes.get(type);

        if (packetRotator != null)
            id = packetRotator.rotateOutgoing(id);

        // Encode the paylad
        final ChannelBuffer payload = packet.encode();

        final int payloadLength = payload.readableBytes(); // length of the payload
        final int packetLength = payloadLength + 1; // + 1 for the ID

        final ChannelBuffer buffer = ChannelBuffers.buffer(packetLength + 2); // allocate a buffer for packet + length header

        // Write the length header
        PacketEncoder.writeLength(packetLength, buffer);
        if (packetLength >= 160) {
            buffer.writeByte(id);
            buffer.writeBytes(payload);
        }
        else if (packetLength >= 2) {
            buffer.writeBytes(payload, payloadLength - 1, 1);
            buffer.writeByte(id);
            buffer.writeBytes(payload, 0, payloadLength - 1);
        }
        else {
            buffer.writeByte(id);
        }

        PACKET_SIZE_HISTOGRAM.update(packetLength);
        return buffer;
    }
}
