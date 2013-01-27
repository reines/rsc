package com.jamierf.rsc.dataserver.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginCredentials {

    @JsonProperty
    private String username;

    @JsonProperty
    private byte[] password;

    @JsonProperty
    private int[] keys;

    @JsonCreator
    public LoginCredentials(
            @JsonProperty("username") String username,
            @JsonProperty("password") byte[] password,
            @JsonProperty("keys") int[] keys) {
        this.username = username;
        this.password = password;
        this.keys = keys;
    }

    public String getUsername() {
        return username;
    }

    public byte[] getPassword() {
        return password;
    }

    public int[] getKeys() {
        return keys;
    }
}
