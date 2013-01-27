package com.jamierf.rsc.dataserver.service.resources;

import com.google.common.base.Optional;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.jamierf.rsc.dataserver.api.LoginStatus;
import com.jamierf.rsc.dataserver.api.SessionCredentials;
import com.jamierf.rsc.dataserver.api.SessionData;
import com.jamierf.rsc.dataserver.service.db.User;
import com.jamierf.rsc.dataserver.service.db.UserDAO;
import com.yammer.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/session")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SessionResource {

    private final UserDAO userDAO;
    private final String sessionSecret;
    private final HashFunction sessionHashFunction;

    public SessionResource(UserDAO userDAO, String sessionSecret) {
        this.userDAO = userDAO;
        this.sessionSecret = sessionSecret;

        sessionHashFunction = Hashing.goodFastHash(Long.SIZE);
    }

    private LoginStatus validateUser(User user) {
        if (user.isBanned())
            return LoginStatus.ACCOUNT_BANNED;

        if (user.isSuspended())
            return LoginStatus.ACCOUNT_SUSPENDED;

        if (user.isSuspectedStolen())
            return LoginStatus.ACCOUNT_SUSPECT_STOLEN;

        return LoginStatus.SUCCESSFUL_LOGIN;
    }

    public long generateSessionId(long userId, int[] keys, String secret) {
        final Hasher hasher = sessionHashFunction.newHasher();

        hasher.putLong(userId);
        hasher.putString(secret);

        for (int key : keys)
            hasher.putInt(key);

        return hasher.hash().asLong();
    }

    private SessionData login(String username, byte[] password, int[] keys) {
        final Optional<User> user = userDAO.findByCredentials(username, password);
        if (!user.isPresent())
            return SessionData.createInvalidSession(LoginStatus.INVALID_CREDENTIALS);

        final LoginStatus status = this.validateUser(user.get());
        if (!status.isSuccess())
            return SessionData.createInvalidSession(status);

        final long sessionId = this.generateSessionId(user.get().getUserId(), keys, sessionSecret);
        return SessionData.createValidSession(status, sessionId, user.get());
    }

    @PUT
    @UnitOfWork( transactional = true )
    public Response login(SessionCredentials credentials) {
        final SessionData data = this.login(credentials.getUsername(), credentials.getPassword(), credentials.getKeys());
        return Response.status(Response.Status.OK).entity(data).build();
    }
}
