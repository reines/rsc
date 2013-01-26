package com.jamierf.rsc.client.loader;

import com.google.common.collect.ImmutableMap;

import java.applet.AppletContext;
import java.applet.AppletStub;
import java.net.URL;

public class MockAppletStub implements AppletStub, GameClientCallback {

    private final URL resourceURL;
    private final URL serverURL;
    private final ImmutableMap<String, String> parameters;

    private boolean connecting;

    public MockAppletStub(URL resourceURL, URL serverURL, ImmutableMap<String, String> parameters) {
        this.resourceURL = resourceURL;
        this.serverURL = serverURL;
        this.parameters = parameters;

        connecting = false;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public URL getDocumentBase() {
        return resourceURL;
    }

    @Override
    public URL getCodeBase() {
        final URL codeBase = connecting ? serverURL : resourceURL;
        System.out.println("Providing codeBase = " + codeBase);
        return codeBase;
    }

    @Override
    public String getParameter(String name) {
        return parameters.get(name);
    }

    @Override
    public AppletContext getAppletContext() {
        return null;
    }

    @Override
    public void appletResize(int width, int height) {

    }

    @Override
    public void beforeConnect() {
        connecting = true;
    }

    @Override
    public void afterConnect() {
        connecting = false;
    }
}
