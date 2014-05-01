package com.jamierf.rsc.dataserver.service.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.mindrot.jbcrypt.BCrypt;

public class Password {

    public static Password fromString(String password) {
        final String salt = BCrypt.gensalt();
        final String hash = BCrypt.hashpw(password, salt);

        return new Password(salt, hash);
    }

    @JsonProperty
    private final String salt;

    @JsonProperty
    private final String hash;

    @JsonCreator
    public Password(
            @JsonProperty("salt") String salt,
            @JsonProperty("hash") String hash) {
        this.salt = salt;
        this.hash = hash;
    }

    public boolean isMatch(String input) {
        return BCrypt.checkpw(input, hash);
    }
}
