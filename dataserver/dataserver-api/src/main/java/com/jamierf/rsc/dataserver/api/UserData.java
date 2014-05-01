package com.jamierf.rsc.dataserver.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

public class UserData {

    public static UserData create(long userId, String username) {
        return new UserData(userId, username, false, false, false, false);
    }

    @JsonProperty
    protected final long userId;

    @JsonProperty
    protected final String username;

    @JsonProperty
    protected final boolean banned;

    @JsonProperty
    protected final boolean member;

    @JsonProperty
    protected final boolean veteran;

    @JsonProperty
    protected final boolean moderator;

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

    public String getUsername() {
        return username;
    }

    public boolean isBanned() {
        return banned;
    }

    public boolean isMember() {
        return member;
    }

    public boolean isVeteran() {
        return veteran;
    }

    public boolean isModerator() {
        return moderator;
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
