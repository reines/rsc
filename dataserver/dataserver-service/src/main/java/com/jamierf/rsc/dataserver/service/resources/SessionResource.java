package com.jamierf.rsc.dataserver.service.resources;

import com.google.common.base.Optional;
import com.jamierf.rsc.dataserver.api.SessionCredentials;
import com.jamierf.rsc.dataserver.api.SessionData;
import com.jamierf.rsc.dataserver.service.db.User;
import com.jamierf.rsc.dataserver.service.db.UserDAO;
import com.yammer.dropwizard.hibernate.UnitOfWork;
import com.yammer.metrics.annotation.Timed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/session")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SessionResource {

    private final UserDAO userDAO;
    private final String sessionSecret;

    public SessionResource(UserDAO userDAO, String sessionSecret) {
        this.userDAO = userDAO;
        this.sessionSecret = sessionSecret;
    }

    @PUT
    @Timed
    @UnitOfWork( transactional = true )
    public SessionData login(SessionCredentials credentials) {
        final String username = User.cleanUsername(credentials.getUsername());
        final String password = credentials.getPassword();
        final int[] keys = credentials.getKeys();
        final int clientVersion = credentials.getClientVersion();

        if (!SessionCredentials.isValid(username, password, keys))
            throw new WebApplicationException(Response.Status.BAD_REQUEST);

        final Optional<User> user = userDAO.findByCredentials(username, password);
        if (!user.isPresent())
            throw new WebApplicationException(Response.Status.FORBIDDEN);

        if (user.get().isBanned())
            throw new WebApplicationException(Response.Status.CONFLICT);

        return SessionData.createValidSession(user.get(), keys, clientVersion, sessionSecret);
    }
}
