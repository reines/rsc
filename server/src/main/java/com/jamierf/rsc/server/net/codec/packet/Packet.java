package com.jamierf.rsc.server.net.codec.packet;

import java.io.IOException;

public abstract class Packet {
    protected abstract void decode(PacketBuffer buffer) throws IOException;
    protected abstract void encode(PacketBuffer buffer) throws IOException;
}
