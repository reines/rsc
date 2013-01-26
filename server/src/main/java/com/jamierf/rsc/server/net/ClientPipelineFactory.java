package com.jamierf.rsc.server.net;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.jamierf.rsc.server.net.codec.packet.Packet;
import com.jamierf.rsc.server.net.codec.packet.PacketDecoder;
import com.jamierf.rsc.server.net.codec.packet.PacketEncoder;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

public class ClientPipelineFactory implements ChannelPipelineFactory {

    private final LogicHandler logicHandler;
    private final BiMap<Integer, Class<? extends Packet>> packetTypes;

    public ClientPipelineFactory(LogicHandler logicHandler) {
        this.logicHandler = logicHandler;

        packetTypes = HashBiMap.create();
    }

    public void addPacketType(int id, Class<? extends Packet> type) {
        packetTypes.put(id, type);
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        final ChannelPipeline pipeline = Channels.pipeline();

        pipeline.addLast(PacketDecoder.NAME, new PacketDecoder(packetTypes));
        pipeline.addLast(LogicHandler.NAME, logicHandler);
        pipeline.addLast(PacketEncoder.NAME, new PacketEncoder(packetTypes.inverse()));

        return pipeline;
    }
}
