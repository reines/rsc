package com.jamierf.rsc.server.net.packet;

import com.google.common.base.Objects;
import com.jamierf.rsc.server.net.codec.packet.Packet;

public class SetPositionCommandPacket extends Packet {

    private final short playerIndex;
    private final short xPosition;
    private final short yPosition;
    private final short zPosition;
    private final short unknown = 944;

    public SetPositionCommandPacket(int playerIndex, int xPosition, int yPosition, int zPosition) {
        this.playerIndex = (short) playerIndex;
        this.xPosition = (short) xPosition;
        this.yPosition = (short) yPosition;
        this.zPosition = (short) zPosition;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("playerIndex", playerIndex)
                .add("position", String.format("(%d,%d,%d)", xPosition, yPosition, zPosition))
                .toString();
    }
}
