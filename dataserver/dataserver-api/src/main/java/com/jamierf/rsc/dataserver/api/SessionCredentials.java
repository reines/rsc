package com.jamierf.rsc.dataserver.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionCredentials extends UserCredentials {

    public static boolean isValid(String username, String password, int[] keys) {
        return UserCredentials.isValid(username, password) && keys != null && keys.length == 4;
    }

    @JsonProperty
    private int clientVersion;

    @JsonProperty
    private int[] keys;

    @JsonCreator
    public SessionCredentials(
            @JsonProperty("username") String username,
            @JsonProperty("password") String password,
            @JsonProperty("clientVersion") int clientVersion,
            @JsonProperty("keys") int[] keys) {
        super(username, password);

        this.clientVersion = clientVersion;
        this.keys = keys;
    }

    public int getClientVersion() {
        return clientVersion;
    }

    public int[] getKeys() {
        return keys;
    }
}
