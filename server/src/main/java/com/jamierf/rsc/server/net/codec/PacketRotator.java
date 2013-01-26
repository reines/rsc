package com.jamierf.rsc.server.net.codec;

import org.apache.commons.math3.random.ISAACRandom;

public class PacketRotator {

    private ISAACRandom incoming;
    private ISAACRandom outgoing;

    public PacketRotator() {
        incoming = null;
        outgoing = null;
    }

    public void setSeed(int[] seed) {
        incoming = new ISAACRandom(seed);
        outgoing = new ISAACRandom(seed);
    }

    public int rotateIncoming(int id) {
        if (incoming == null)
            return id;

        return id & 0xff + incoming.nextInt();
    }

    public int rotateOutgoing(int id) {
        if (outgoing == null)
            return id;

        return id - outgoing.nextInt() & 0xff;
    }
}
