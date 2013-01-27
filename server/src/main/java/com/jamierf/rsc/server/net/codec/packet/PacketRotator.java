package com.jamierf.rsc.server.net.codec.packet;

import org.apache.commons.math3.random.ISAACRandom;

public class PacketRotator {

    private static int[] padSessionKeys(int[] sessionKeys, int size) {
        final int[] encryptionKeys = new int[size];

        // Copy what session keys we have in to the new correctly sized encryption keys array
        System.arraycopy(sessionKeys, 0, encryptionKeys, 0, Math.min(sessionKeys.length, size));

        return encryptionKeys;
    }

    private final ISAACRandom incoming;
    private final ISAACRandom outgoing;

    public PacketRotator(int[] sessionKeys) {
        // The ISAAC should generate noise if we give a < 256 size seed but we don't want that so pad it
        sessionKeys = PacketRotator.padSessionKeys(sessionKeys, 256);

        incoming = new ISAACRandom(sessionKeys);
        outgoing = new ISAACRandom(sessionKeys);
    }

    public int rotateIncoming(int id) {
        final int adjust = incoming.nextInt();
        return 0xff & (id - adjust);
    }

    public int rotateOutgoing(int id) {
        final int adjust = outgoing.nextInt();
        return (id & 0xff) + adjust;
    }
}
