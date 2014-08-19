package com.jamierf.rsc.client.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.client.JerseyClientConfiguration;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.net.URI;

public class ProxyConfiguration extends JerseyClientConfiguration {

    @NotNull
    @JsonProperty
    private URI remoteHost;

    @NotNull
    @JsonProperty
    private File cacheDirectory;

    public URI getRemoteHost() {
        return remoteHost;
    }

    public File getCacheDirectory() {
        return cacheDirectory;
    }
}
