package com.jamierf.rsc.server.net.codec.packet;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Histogram;
import com.yammer.metrics.core.Meter;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PacketDecoder extends FrameDecoder {

    public static final String NAME = "packet-decoder";

    private static final Meter OVERSIZE_PACKET_METER = Metrics.newMeter(PacketDecoder.class, "oversized-packets", "errors", TimeUnit.SECONDS);
    private static final Meter UNRECOGNISED_PACKET_METER = Metrics.newMeter(PacketDecoder.class, "unrecognised-packets", "errors", TimeUnit.SECONDS);
    private static final Histogram PACKET_SIZE_HISTOGRAM = Metrics.newHistogram(PacketDecoder.class, "packet-size");

    public static <T extends Packet> T decodePacket(Class<T> type, ChannelBuffer buffer) throws Exception {
        final T packet = type.newInstance();
        packet.decode(buffer);

        return packet;
    }

    private static int readLength(ChannelBuffer buffer) {
        if (buffer.readableBytes() < 2)
            return -1;

        final int length = buffer.readUnsignedByte();
        return length < 160 ? length : (length - 160) * 256 + buffer.readUnsignedByte();
    }

    private final Map<Integer, Class<? extends Packet>> packetTypes;
    private PacketRotator packetRotator;

    public PacketDecoder(Map<Integer, Class<? extends Packet>> packetTypes) {
        this.packetTypes = packetTypes;

        packetRotator = null;
    }

    public void setPacketRotator(PacketRotator packetRotator) {
        this.packetRotator = packetRotator;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        // No point in trying if we don't have enough data
        if (buffer.readableBytes() < 2)
            return null;

        // Mark the buffer position incase it doesn't include an entire packet
        buffer.markReaderIndex();

        // Check we have enough content in the buffer
        final int length = PacketDecoder.readLength(buffer);
        if (length < 0 || buffer.readableBytes() < length) {
            buffer.resetReaderIndex();
            return null;
        }

        int id = 0;
        final ChannelBuffer payload;

        if (length >= 160) {
            id = buffer.readUnsignedByte();
            payload = buffer.readSlice(length - 1);
        }
        else if (length >= 2) {
            payload = ChannelBuffers.buffer(length - 1);

            final byte end = buffer.readByte();
            id = buffer.readUnsignedByte();

            buffer.readBytes(payload, length - 2);
            payload.writeByte(end);
        }
        else {
            id = buffer.readUnsignedByte();
            payload = ChannelBuffers.EMPTY_BUFFER;
        }

        if (packetRotator != null)
            id = packetRotator.rotateIncoming(id);

        final Class<? extends Packet> type = packetTypes.get(id);
        if (type == null) {
            UNRECOGNISED_PACKET_METER.mark();
            throw new PacketCodecException("Unrecognised packet: id = " + id);
        }

        // Decode the packet
        final Packet packet = PacketDecoder.decodePacket(type, payload);

        final int remaining = payload.readableBytes();
        if (remaining > 0) {
            OVERSIZE_PACKET_METER.mark();
            throw new PacketCodecException("Buffer not empty (" + remaining + ") after decoding packet " + packet + ": " + payload);
        }

        PACKET_SIZE_HISTOGRAM.update(length);
        return packet;
    }
}
