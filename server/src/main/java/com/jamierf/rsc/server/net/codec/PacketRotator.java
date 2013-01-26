package com.jamierf.rsc.server.net.codec;

import org.apache.commons.math3.random.ISAACRandom;

public class PacketRotator {

    private final ISAACRandom incoming;
    private final ISAACRandom outgoing;

    public PacketRotator(int[] sessionKeys) {
        incoming = new ISAACRandom(sessionKeys);
        outgoing = new ISAACRandom(sessionKeys);
    }

    public int rotateIncoming(int id) {
        if (incoming == null)
            return id;

        return id - incoming.nextInt() & 0xff;
    }

    public int rotateOutgoing(int id) {
        if (outgoing == null)
            return id;

        return (id & 0xff) + outgoing.nextInt();
    }
}
