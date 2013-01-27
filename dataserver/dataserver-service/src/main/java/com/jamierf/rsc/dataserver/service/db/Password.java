package com.jamierf.rsc.dataserver.service.db;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import javax.persistence.Basic;
import javax.persistence.Entity;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Arrays;

@Entity
public class Password implements Serializable {

    public static final int DEFAULT_ITERATIONS = 100;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final HashFunction HASH_FUNCTION = Hashing.sha512();

    private static byte[] hash(int salt, byte[] input) {
        final Hasher hasher = HASH_FUNCTION.newHasher();

        hasher.putInt(salt);
        hasher.putBytes(input);

        return hasher.hash().asBytes();
    }

    @Basic
    private int salt;

    @Basic
    private int iterations;

    @Basic
    private byte[] hash;

    protected Password() { }

    public Password(String input) {
        salt = SECURE_RANDOM.nextInt();
        iterations = DEFAULT_ITERATIONS;
        hash = this.hash(input);
    }

    public boolean isMatch(String input) {
        final byte[] hash = this.hash(input);
        return Arrays.equals(this.hash, hash);
    }

    private byte[] hash(String input) {
        
    }
}
