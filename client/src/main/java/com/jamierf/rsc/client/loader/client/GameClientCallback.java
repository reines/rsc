package com.jamierf.rsc.client.loader.client;

public interface GameClientCallback {
    @SuppressWarnings("unused")
    void beforeConnect();

    @SuppressWarnings("unused")
    void afterConnect();
}
