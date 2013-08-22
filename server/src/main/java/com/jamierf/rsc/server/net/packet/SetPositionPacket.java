package com.jamierf.rsc.server.net.packet;

import com.google.common.base.Objects;
import com.jamierf.rsc.server.net.codec.packet.Packet;
import com.jamierf.rsc.server.net.codec.packet.PacketBuffer;

import java.io.IOException;

public class SetPositionPacket extends Packet {

    private short playerIndex;
    private short xPosition;
    private short yPosition;
    private short zPosition;
    private short unknown = 944;

    protected SetPositionPacket() {}

    public SetPositionPacket(int playerIndex, int xPosition, int yPosition, int zPosition) {
        this.playerIndex = (short) playerIndex;
        this.xPosition = (short) xPosition;
        this.yPosition = (short) yPosition;
        this.zPosition = (short) zPosition;
    }

    @Override
    protected void decode(PacketBuffer buffer) throws IOException {
        playerIndex = buffer.read(short.class);
        xPosition = buffer.read(short.class);
        yPosition = buffer.read(short.class);
        zPosition = buffer.read(short.class);
        unknown = buffer.read(short.class);
    }

    @Override
    protected void encode(PacketBuffer buffer) throws IOException {
        buffer.write(playerIndex);
        buffer.write(xPosition);
        buffer.write(yPosition);
        buffer.write(zPosition);
        buffer.write(unknown);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("playerIndex", playerIndex)
                .add("position", String.format("(%d,%d,%d)", xPosition, yPosition, zPosition))
                .toString();
    }
}
