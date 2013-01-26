package com.jamierf.rsc.client.loader;

public class GameClientModificationException extends ReflectiveOperationException {

    public GameClientModificationException(String message) {
        super(message);
    }

    public GameClientModificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
