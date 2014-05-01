package com.jamierf.rsc.dataserver.client.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.client.JerseyClientConfiguration;

import javax.validation.constraints.NotNull;
import java.net.URI;

public class DataserverClientConfig extends JerseyClientConfiguration {

    @NotNull
    @JsonProperty
    private URI root;

    public URI getRoot() {
        return root;
    }
}
