package com.jamierf.rsc.dataserver.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserCredentials {

    @JsonProperty
    private String username;

    @JsonProperty
    private byte[] password;

    @JsonCreator
    public UserCredentials(
            @JsonProperty("username") String username,
            @JsonProperty("password") byte[] password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public byte[] getPassword() {
        return password;
    }
}
