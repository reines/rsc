package com.jamierf.rsc.dataserver.service;

import com.jamierf.rsc.dataserver.service.config.DataserverConfiguration;
import com.jamierf.rsc.dataserver.service.db.DataserverHibernateBundle;
import com.jamierf.rsc.dataserver.service.db.UserDAO;
import com.jamierf.rsc.dataserver.service.resources.SessionResource;
import com.jamierf.rsc.dataserver.service.resources.UserResource;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

public class Dataserver extends Service<DataserverConfiguration> {

    public static void main(String[] args) throws Exception {
        new Dataserver().run(args);
    }

    private final DataserverHibernateBundle hibernate;

    public Dataserver() {
        hibernate = new DataserverHibernateBundle();
    }

    @Override
    public void initialize(Bootstrap<DataserverConfiguration> bootstrap) {
        bootstrap.addBundle(hibernate);
    }

    @Override
    public void run(DataserverConfiguration config, Environment env) throws Exception {
        final UserDAO userDAO = new UserDAO(hibernate.getSessionFactory());

        env.addResource(new SessionResource(userDAO, config.getSessionSecret()));
        env.addResource(new UserResource(userDAO));
    }
}
