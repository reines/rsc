package com.jamierf.rsc.server.net.crypto;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class XTEACipher {

    private static final int NUM_ROUNDS = 32;
    private static final int DELTA = 0x9e3779b9;

    public static ChannelBuffer encrypt(byte[] data, int[] key) {
        return XTEACipher.encrypt(ChannelBuffers.wrappedBuffer(data), key);
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
                v0 += (((v1 << 4) ^ (v1 >>> 5)) + v1) ^ (sum + key[sum & 3]);
                sum += DELTA;
                v1 += (((v0 << 4) ^ (v0 >>> 5)) + v0) ^ (sum + key[(sum >>> 11) & 3]);
            }

            output.writeInt(v0);
            output.writeInt(v1);
        }

        // Copy any remaining bytes
        output.writeBytes(data.readBytes(data.readableBytes()));

        return output;
    }

    public static ChannelBuffer decrypt(byte[] data, int[] key) {
        return XTEACipher.decrypt(ChannelBuffers.wrappedBuffer(data), key);
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
                v1 -= (((v0 << 4) ^ (v0 >>> 5)) + v0) ^ (sum + key[(sum >>> 11) & 3]);
                sum -= DELTA;
                v0 -= (((v1 << 4) ^ (v1 >>> 5)) + v1) ^ (sum + key[sum & 3]);
            }

            output.writeInt(v0);
            output.writeInt(v1);
        }

        // Copy any remaining bytes
        output.writeBytes(data.readBytes(data.readableBytes()));

        return output;
    }

    private XTEACipher() { }
}
