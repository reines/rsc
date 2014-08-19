package com.jamierf.rsc.client.loader.applet;

import com.google.common.collect.ImmutableMap;
import com.jamierf.rsc.client.loader.client.GameClientCallback;

import java.applet.AppletContext;
import java.applet.AppletStub;
import java.net.URL;

public class MockAppletStub implements AppletStub, GameClientCallback {

    private final URL resourceURL;
    private final URL serverURL;
    private final ImmutableMap<String, String> parameters;

    private URL codeBase;

    public MockAppletStub(URL resourceURL, URL serverURL, ImmutableMap<String, String> parameters) {
        this.resourceURL = resourceURL;
        this.serverURL = serverURL;
        this.parameters = parameters;

        codeBase = resourceURL;
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
        // NOOP
    }

    @Override
    public void beforeConnect() {
        codeBase = serverURL;
    }

    @Override
    public void afterConnect() {
        codeBase = resourceURL;
    }
}
