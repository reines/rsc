package com.jamierf.rsc.server.net.session;

import com.jamierf.rsc.dataserver.api.LoginStatus;

public class SessionCreationException extends Exception {

    private final LoginStatus response;

    public SessionCreationException(LoginStatus response) {
        this (response, null);
    }

    public SessionCreationException(LoginStatus response, Throwable cause) {
        super("Failed to login user: " + response, cause);

        this.response = response;
    }

    public LoginStatus getResponse() {
        return response;
    }
}
