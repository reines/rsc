package com.jamierf.rsc.dataserver.service.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import com.jamierf.rsc.dataserver.api.SessionCredentials;
import com.jamierf.rsc.dataserver.api.SessionData;
import com.jamierf.rsc.dataserver.api.UserData;
import com.jamierf.rsc.dataserver.service.db.UserStore;
import com.jamierf.rsc.dataserver.service.model.UserAndPassword;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/session")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SessionResource {

    private final UserStore userStore;
    private final String sessionSecret;

    public SessionResource(UserStore userStore, String sessionSecret) {
        this.userStore = userStore;
        this.sessionSecret = sessionSecret;
    }

    @PUT
    @Timed
    public SessionData login(SessionCredentials credentials) {
        final String username = UserAndPassword.cleanUsername(credentials.getUsername());
        final String password = credentials.getPassword();
        final int[] keys = credentials.getKeys();
        final int clientVersion = credentials.getClientVersion();

        if (!SessionCredentials.isValid(username, password, keys)) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        final Optional<UserData> user = userStore.findByCredentials(username, password);
        if (!user.isPresent()) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }

        if (user.get().isBanned()) {
            throw new WebApplicationException(Response.Status.CONFLICT);
        }

        return SessionData.createValidSession(user.get(), keys, clientVersion, sessionSecret);
    }
}
