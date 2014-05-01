package com.jamierf.rsc.dataserver.service;

import com.jamierf.rsc.dataserver.service.config.DataserverConfiguration;
import com.jamierf.rsc.dataserver.service.db.UserStore;
import com.jamierf.rsc.dataserver.service.error.UserAlreadyExistsExceptionMapper;
import com.jamierf.rsc.dataserver.service.resources.SessionResource;
import com.jamierf.rsc.dataserver.service.resources.UserResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class Dataserver extends Application<DataserverConfiguration> {

    public static void main(String[] args) throws Exception {
        new Dataserver().run(args);
    }

    @Override
    public void initialize(Bootstrap<DataserverConfiguration> bootstrap) {

    }

    @Override
    public void run(DataserverConfiguration configuration, Environment environment) throws Exception {
        final UserStore userStore = new UserStore();

        environment.jersey().register(new SessionResource(userStore, configuration.getSessionSecret()));
        environment.jersey().register(new UserResource(userStore));

        environment.jersey().register(new UserAlreadyExistsExceptionMapper());
    }
}
