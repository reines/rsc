package com.jamierf.rsc.dataserver.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionData {

    public static SessionData invalidSession(LoginStatus status) {
        return new SessionData(status);
    }

    @JsonProperty
    private LoginStatus status;

    @JsonProperty
    private long id;

    @JsonProperty
    private String username;

    @JsonProperty
    private boolean member;

    @JsonProperty
    private boolean veteran;

    private SessionData(LoginStatus status) {
        this.status = status;
    }

    @JsonCreator
    public SessionData(
            @JsonProperty("status") LoginStatus status,
            @JsonProperty("id") long id,
            @JsonProperty("username") String username,
            @JsonProperty("member") boolean member,
            @JsonProperty("veteran") boolean veteran) {
        this.status = status;
        this.id = id;
        this.username = username;
        this.member = member;
        this.veteran = veteran;
    }

    public LoginStatus getStatus() {
        return status;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public boolean isMember() {
        return member;
    }

    public boolean isVeteran() {
        return veteran;
    }
}
