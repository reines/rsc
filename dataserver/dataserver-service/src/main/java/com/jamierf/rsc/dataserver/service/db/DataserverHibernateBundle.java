package com.jamierf.rsc.dataserver.service.db;

import com.jamierf.rsc.dataserver.service.config.DataserverConfiguration;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import com.yammer.dropwizard.hibernate.HibernateBundle;

public class DataserverHibernateBundle extends HibernateBundle<DataserverConfiguration> {

    public DataserverHibernateBundle() {
        super(User.class, Password.class);
    }

    @Override
    public DatabaseConfiguration getDatabaseConfiguration(DataserverConfiguration config) {
        return config.getDatabaseConfiguration();
    }
}
