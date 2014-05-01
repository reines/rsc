package com.jamierf.rsc.client.loader;

public interface GameClientCallback {
    @SuppressWarnings("unused")
    void beforeConnect();

    @SuppressWarnings("unused")
    void afterConnect();
}
