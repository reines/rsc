package com.jamierf.rsc.dataserver.service.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.db.DatabaseConfiguration;

public class DataserverConfiguration extends Configuration {

    @JsonProperty
    private DatabaseConfiguration database = new DatabaseConfiguration();

    @JsonProperty
    private String sessionSecret = "";

    public DatabaseConfiguration getDatabaseConfiguration() {
        return database;
    }

    public String getSessionSecret() {
        return sessionSecret;
    }
}
