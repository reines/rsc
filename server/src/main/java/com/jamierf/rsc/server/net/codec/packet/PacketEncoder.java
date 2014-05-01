package com.jamierf.rsc.server.net.codec.packet;

import com.codahale.metrics.MetricRegistry;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import java.util.Map;

public class PacketEncoder extends OneToOneEncoder {

    public static final String NAME = "packet-encoder";

    private static final String UNRECOGNISED_PACKET_METER_NAME = "unrecognised-packets";
    private static final String PACKET_SIZE_HISTOGRAM_NAME = "packet-size";

    private static void writeLength(int length, ChannelBuffer buffer) {
        if (length >= 160) {
            buffer.writeByte(160 + (length / 256));
            buffer.writeByte(length & 0xff);
        }
        else {
            buffer.writeByte(length);
        }
    }

    private final MetricRegistry metricRegistry;
    private final Map<Class<? extends Packet>, Integer> packetTypes;
    private PacketRotator packetRotator;

    public PacketEncoder(MetricRegistry metricRegistry, Map<Class<? extends Packet>, Integer> packetTypes) {
        this.metricRegistry = metricRegistry;
        this.packetTypes = packetTypes;

        packetRotator = null;
    }

    public void setPacketRotator(PacketRotator packetRotator) {
        this.packetRotator = packetRotator;
    }

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        // Allow channel buffers through directly
        if (msg instanceof ChannelBuffer) {
            return msg;
        }

        final Packet packet = (Packet) msg;

        final Class<? extends Packet> type = packet.getClass();
        if (!packetTypes.containsKey(type)) {
            metricRegistry.meter(UNRECOGNISED_PACKET_METER_NAME).mark();
            throw new PacketCodecException("Unrecognised packet type: " + type);
        }

        int id = packetTypes.get(type);

        if (packetRotator != null) {
            id = packetRotator.rotateOutgoing(id);
        }

        // Encode the payload
        final ChannelBuffer payload = ChannelBuffers.dynamicBuffer();
        packet.encode(PacketBuffer.wrap(payload));

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

        metricRegistry.histogram(PACKET_SIZE_HISTOGRAM_NAME).update(packetLength);
        return buffer;
    }
}
