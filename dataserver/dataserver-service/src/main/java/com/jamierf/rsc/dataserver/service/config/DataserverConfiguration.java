package com.jamierf.rsc.dataserver.service.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

public class DataserverConfiguration extends Configuration {

    @NotEmpty
    @JsonProperty
    private String sessionSecret;

    public String getSessionSecret() {
        return sessionSecret;
    }
}
