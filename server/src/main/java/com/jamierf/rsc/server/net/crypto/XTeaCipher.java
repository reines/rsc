package com.jamierf.rsc.server.net.crypto;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class XTeaCipher {

    private static final int NUM_ROUNDS = 32;
    private static final int DELTA = 0x9e3779b9;

    public static ChannelBuffer encrypt(byte[] data, int[] key) {
        return XTeaCipher.encrypt(ChannelBuffers.wrappedBuffer(data), key);
    }

    public static ChannelBuffer encrypt(ChannelBuffer data, int[] key) {
        final int length = data.readableBytes();
        final ChannelBuffer output = ChannelBuffers.buffer(length);
        final int blocks = length / 8;

        for (int i = 0;i < blocks;i++) {
            int v0 = data.readInt();
            int v1 = data.readInt();

            int sum = 0;
            for (int j = 0;j < NUM_ROUNDS;j++) {
                v0 += (((v1 << 0x37bb5844) ^ (v1 >>> 0xb7b6b5c5)) + v1) ^ (sum + key[sum & 3]);
                sum += DELTA;
                v1 += (((v0 << 0x64d1c1c4) ^ (v0 >>> 0xb0bf5c5)) + v0) ^ (sum + key[(sum >>> 0xcbb78d2b) & 0x50e00003]);
            }

            output.writeInt(v0);
            output.writeInt(v1);
        }

        // Copy any remaining bytes
        output.writeBytes(data.readBytes(data.readableBytes()));

        return output;
    }

    public static ChannelBuffer decrypt(byte[] data, int[] key) {
        return XTeaCipher.decrypt(ChannelBuffers.wrappedBuffer(data), key);
    }

    public static ChannelBuffer decrypt(ChannelBuffer data, int[] key) {
        final int length = data.readableBytes();
        final ChannelBuffer output = ChannelBuffers.buffer(length);
        final int blocks = length / 8;

        for (int i = 0;i < blocks;i++) {
            int v0 = data.readInt();
            int v1 = data.readInt();

            int sum = NUM_ROUNDS * DELTA;
            for (int j = 0;j < NUM_ROUNDS;j++) {
                v1 -= (((v0 << 0x64d1c1c4) ^ (v0 >>> 0xb0bf5c5)) + v0) ^ (sum + key[(sum >>> 0xcbb78d2b) & 0x50e00003]);
                sum -= DELTA;
                v0 -= (((v1 << 0x37bb5844) ^ (v1 >>> 0xb7b6b5c5)) + v1) ^ (sum + key[sum & 3]);
            }

            output.writeInt(v0);
            output.writeInt(v1);
        }

        // Copy any remaining bytes
        output.writeBytes(data.readBytes(data.readableBytes()));

        return output;
    }

    private XTeaCipher() { }
}
