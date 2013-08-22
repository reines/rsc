package com.jamierf.rsc.dataserver.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

public class UserData {

    @JsonProperty
    protected long userId;

    @JsonProperty
    protected String username;

    @JsonProperty
    protected  boolean banned = false;

    @JsonProperty
    protected boolean member = false;

    @JsonProperty
    protected boolean veteran = false;

    @JsonProperty
    protected boolean moderator = false;

    protected UserData() {}

    @JsonCreator
    public UserData(
            @JsonProperty("userId") long userId,
            @JsonProperty("username") String username,
            @JsonProperty("banned") boolean banned,
            @JsonProperty("member") boolean member,
            @JsonProperty("veteran") boolean veteran,
            @JsonProperty("moderator") boolean moderator) {
        this.userId = userId;
        this.username = username;
        this.banned = banned;
        this.member = member;
        this.veteran = veteran;
        this.moderator = moderator;
    }

    public long getUserId() {
        return userId;
    }

    protected void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    protected void setUsername(String username) {
        this.username = username;
    }

    public boolean isBanned() {
        return banned;
    }

    protected void setBanned(boolean banned) {
        this.banned = banned;
    }

    public boolean isMember() {
        return member;
    }

    protected void setMember(boolean member) {
        this.member = member;
    }

    public boolean isVeteran() {
        return veteran;
    }

    protected void setVeteran(boolean veteran) {
        this.veteran = veteran;
    }

    public boolean isModerator() {
        return moderator;
    }

    protected void setModerator(boolean moderator) {
        this.moderator = moderator;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).omitNullValues()
                .add("userId", userId)
                .add("username", username)
                .add("banned", banned)
                .add("member", member)
                .add("veteran", veteran)
                .add("moderator", moderator)
                .toString();
    }
}
