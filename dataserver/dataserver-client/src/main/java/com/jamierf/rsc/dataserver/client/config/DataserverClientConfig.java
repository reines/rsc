package com.jamierf.rsc.dataserver.client.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.client.JerseyClientConfiguration;

import java.net.URI;

public class DataserverClientConfig {

    @JsonProperty
    private JerseyClientConfiguration jersey = new JerseyClientConfiguration();

    @JsonProperty
    private URI root;

    public JerseyClientConfiguration getJerseyClientConfiguration() {
        return jersey;
    }

    public URI getRoot() {
        return root;
    }
}
