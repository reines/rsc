package com.jamierf.rsc.dataserver.service.error;

public class UserAlreadyExistsException extends Exception {

    private final String username;

    public UserAlreadyExistsException(String username) {
        super (String.format("User %s already exists", username));

        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
