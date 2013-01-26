package com.jamierf.rsc.server.net.crypto;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class RSACipher {

    public static ChannelBuffer encrypt(byte[] data, RSAPublicKey key) {
        final BigInteger decrypted = new BigInteger(data);
        final BigInteger encrypted = decrypted.modPow(key.getPublicExponent(), key.getModulus());

        return ChannelBuffers.wrappedBuffer(encrypted.toByteArray());
    }

    public static ChannelBuffer encrypt(ChannelBuffer data, RSAPublicKey key) {
        return RSACipher.encrypt(data.array(), key);
    }

    public static ChannelBuffer decrypt(byte[] data, RSAPrivateKey key) {
        final BigInteger encrypted = new BigInteger(data);
        final BigInteger decrypted = encrypted.modPow(key.getPrivateExponent(), key.getModulus());

        return ChannelBuffers.wrappedBuffer(decrypted.toByteArray());
    }

    public static ChannelBuffer decrypt(ChannelBuffer data, RSAPrivateKey key) {
        return RSACipher.decrypt(data.array(), key);
    }

    private RSACipher() { }
}
