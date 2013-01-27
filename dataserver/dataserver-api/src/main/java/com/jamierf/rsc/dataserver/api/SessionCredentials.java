package com.jamierf.rsc.dataserver.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionCredentials extends UserCredentials {

    @JsonProperty
    private int[] keys;

    @JsonCreator
    public SessionCredentials(
            @JsonProperty("username") String username,
            @JsonProperty("password") byte[] password,
            @JsonProperty("keys") int[] keys) {
        super(username, password);

        this.keys = keys;
    }

    public int[] getKeys() {
        return keys;
    }
}
