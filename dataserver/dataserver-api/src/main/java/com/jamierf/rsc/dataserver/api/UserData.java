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
    protected boolean suspended = false;

    @JsonProperty
    protected boolean member = false;

    @JsonProperty
    protected boolean suspectedStolen = false;

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
            @JsonProperty("suspended") boolean suspended,
            @JsonProperty("member") boolean member,
            @JsonProperty("suspectedStolen") boolean suspectedStolen,
            @JsonProperty("veteran") boolean veteran,
            @JsonProperty("moderator") boolean moderator) {
        this.userId = userId;
        this.username = username;
        this.banned = banned;
        this.suspended = suspended;
        this.member = member;
        this.suspectedStolen = suspectedStolen;
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

    public boolean isSuspended() {
        return suspended;
    }

    protected void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public boolean isMember() {
        return member;
    }

    protected void setMember(boolean member) {
        this.member = member;
    }

    public boolean isSuspectedStolen() {
        return suspectedStolen;
    }

    protected void setSuspectedStolen(boolean suspectedStolen) {
        this.suspectedStolen = suspectedStolen;
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
                .add("suspended", suspended)
                .add("member", member)
                .add("suspectedStolen", suspectedStolen)
                .add("veteran", veteran)
                .add("moderator", moderator)
                .toString();
    }
}
