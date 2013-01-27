package com.jamierf.rsc.dataserver.api;

public enum LoginStatus {
    SUCCESSFUL_LOGIN(Byte.MAX_VALUE),

    INVALID_CREDENTIALS(3),
    ALREADY_LOGGED_IN(4),
    CLIENT_UPDATED(5),
    IP_IN_USE(6),
    LOGIN_THROTTLED(7),
    SESSION_REJECTED(8),
    UNDERAGE_ACCOUNT(9),
    ACCOUNT_IN_USE(10),
    ACCOUNT_SUSPENDED(11),
    ACCOUNT_BANNED(12),
    SERVER_FULL(14),
    MEMBERSHIP_REQUIRED(15),
    LOGIN_SERVER_ERROR(16),
    CORRUPT_PROFILE(17),
    ACCOUNT_SUSPECT_STOLEN(18),
    LOGIN_SERVER_MISMATCH(20),
    VETERAN_ACCOUNT_REQUIRED(21),
    PASS_SUSPECT_STOLEN(22),
    DISPLAY_NAME_REQUIRED(23),
    NEW_ACCOUNTS_DISABLED(24),
    ALL_CHARACTERS_BLOCKED(25);


    private final byte code;

    private LoginStatus(int code) {
        this.code = (byte) code;
    }

    public boolean isSuccess() {
        return this == SUCCESSFUL_LOGIN;
    }

    public byte getCode() {
        return code;
    }
}
