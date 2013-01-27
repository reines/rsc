package com.jamierf.rsc.dataserver.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

public class SessionData extends UserData {

    public static SessionData createValidSession(LoginStatus status, long sessionId, UserData user) {
        return new SessionData(status, sessionId, user.getUserId(), user.getUsername(),
                user.isBanned(), user.isSuspended(), user.isMember(), user.isSuspectedStolen(), user.isVeteran(), user.isModerator());
    }

    public static SessionData createInvalidSession(LoginStatus status) {
        return new SessionData(status);
    }

    @JsonProperty
    private LoginStatus status;

    @JsonProperty
    private long sessionId;

    protected SessionData(LoginStatus status) {
        this.status = status;
    }

    @JsonCreator
    public SessionData(
            @JsonProperty("status") LoginStatus status,
            @JsonProperty("sessionId") long sessionId,
            @JsonProperty("userId") long userId,
            @JsonProperty("username") String username,
            @JsonProperty("banned") boolean banned,
            @JsonProperty("suspended") boolean suspended,
            @JsonProperty("member") boolean member,
            @JsonProperty("suspectedStolen") boolean suspectedStolen,
            @JsonProperty("veteran") boolean veteran,
            @JsonProperty("mdoerator") boolean moderator) {
        super(userId, username, banned, suspended, member, suspectedStolen, veteran, moderator);

        this.status = status;
        this.sessionId = sessionId;
    }

    public LoginStatus getStatus() {
        return status;
    }

    public long getSessionId() {
        return sessionId;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).omitNullValues()
                .add("status", status)
                .add("sessionId", sessionId)
                .addValue(super.toString())
                .toString();
    }
}
