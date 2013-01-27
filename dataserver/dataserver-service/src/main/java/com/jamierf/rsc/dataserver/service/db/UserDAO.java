package com.jamierf.rsc.dataserver.service.db;

import com.google.common.base.Optional;
import com.yammer.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

public class UserDAO extends AbstractDAO<User> {

    public UserDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<User> findById(long userId) {
        return Optional.fromNullable(super.get(userId));
    }

    public Optional<User> findByUsername(String username) {
        final Criteria criteria = super.criteria();

        criteria.add(Restrictions.like("username", username));

        return Optional.fromNullable(super.uniqueResult(criteria));
    }

    public Optional<User> findByCredentials(String username, byte[] password) {
        final Criteria criteria = super.criteria();

        criteria.add(Restrictions.like(User.USERNAME_FIELD, username));
        criteria.add(Restrictions.eq(User.PASSWORD_FIELD, User.hashPassword(password)));

        return Optional.fromNullable(super.uniqueResult(criteria));
    }

    public User create(String username, byte[] password) {
        return super.persist(new User(username, password));
    }
}
