package com.jamierf.rsc.dataserver.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

public class SessionData extends UserData {

    private static final HashFunction sessionHashFunction = Hashing.goodFastHash(Long.SIZE);

    private static long generateSessionId(long userId, int[] keys, int clientVersion, String secret) {
        final Hasher hasher = sessionHashFunction.newHasher();

        hasher.putLong(userId);
        hasher.putString(secret);
        hasher.putInt(clientVersion);

        for (int key : keys)
            hasher.putInt(key);

        return hasher.hash().asLong();
    }

    public static SessionData createValidSession(UserData user, int[] keys, int clientVersion, String secret) {
        final long sessionId = SessionData.generateSessionId(user.getUserId(), keys, clientVersion, secret);
        return new SessionData(sessionId, user.getUserId(), user.getUsername(), user.isBanned(), user.isMember(), user.isVeteran(), user.isModerator());
    }

    @JsonProperty
    private long sessionId;

    @JsonCreator
    public SessionData(
            @JsonProperty("sessionId") long sessionId,
            @JsonProperty("userId") long userId,
            @JsonProperty("username") String username,
            @JsonProperty("banned") boolean banned,
            @JsonProperty("member") boolean member,
            @JsonProperty("veteran") boolean veteran,
            @JsonProperty("mdoerator") boolean moderator) {
        super(userId, username, banned, member, veteran, moderator);

        this.sessionId = sessionId;
    }

    public long getSessionId() {
        return sessionId;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).omitNullValues()
                .add("sessionId", sessionId)
                .addValue(super.toString())
                .toString();
    }
}
