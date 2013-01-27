package com.jamierf.rsc.dataserver.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

import java.nio.charset.Charset;

public class UserCredentials {

    public static final Charset CHARSET = Charset.forName("UTF-8");

    public static boolean isValid(String username, String password) {
        return !Strings.isNullOrEmpty(username) && !Strings.isNullOrEmpty(password);
    }

    @JsonProperty
    private String username;

    @JsonProperty
    private String password;

    @JsonCreator
    public UserCredentials(
            @JsonProperty("username") String username,
            @JsonProperty("password") String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
