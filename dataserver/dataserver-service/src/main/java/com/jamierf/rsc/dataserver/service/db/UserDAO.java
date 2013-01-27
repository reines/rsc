package com.jamierf.rsc.dataserver.service.db;

import com.google.common.base.Optional;
import com.yammer.dropwizard.hibernate.AbstractDAO;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Timer;
import com.yammer.metrics.core.TimerContext;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

public class UserDAO extends AbstractDAO<User> {

    private static final Timer FIND_BY_ID_TIMER = Metrics.newTimer(UserDAO.class, "find-by-id");
    private static final Timer FIND_BY_USERNAME_TIMER = Metrics.newTimer(UserDAO.class, "find-by-username");
    private static final Timer FIND_BY_CREDENTIALS_TIMER = Metrics.newTimer(UserDAO.class, "find-by-credentials");
    private static final Timer CREATE_TIMER = Metrics.newTimer(UserDAO.class, "create");

    public UserDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<User> findById(long userId) {
        final TimerContext timer = FIND_BY_ID_TIMER.time();

        try {
            return Optional.fromNullable(super.get(userId));
        }
        finally {
            timer.stop();
        }
    }

    public Optional<User> findByUsername(String username) {
        final TimerContext timer = FIND_BY_USERNAME_TIMER.time();

        try {
            final Criteria criteria = super.criteria();

            criteria.add(Restrictions.like("username", username));

            return Optional.fromNullable(super.uniqueResult(criteria));
        }
        finally {
            timer.stop();
        }
    }

    public Optional<User> findByCredentials(String username, String password) {
        final TimerContext timer = FIND_BY_CREDENTIALS_TIMER.time();

        try {
            final Criteria criteria = super.criteria();

            criteria.add(Restrictions.like(User.USERNAME_FIELD, username));
            criteria.add(Restrictions.eq(User.PASSWORD_FIELD, User.hashPassword(password)));

            return Optional.fromNullable(super.uniqueResult(criteria));
        }
        finally {
            timer.stop();
        }
    }

    public User create(String username, String password) {
        final TimerContext timer = CREATE_TIMER.time();

        try {
            return super.persist(new User(username, password));
        }
        finally {
            timer.stop();
        }
    }
}
