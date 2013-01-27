package com.jamierf.rsc.dataserver.service.resources;

import com.google.common.base.Optional;
import com.jamierf.rsc.dataserver.api.LoginCredentials;
import com.jamierf.rsc.dataserver.api.LoginStatus;
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

    public static long generateSessionId(long userId, int[] keys) {
        return userId; // TODO: Generate a deterministic session id based on username and keys
    }

    private final UserDAO userDAO;

    public SessionResource(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    private LoginStatus validateUser(User user, byte[] password) {
        if (!user.isPasswordMatch(password))
            return LoginStatus.INVALID_CREDENTIALS;

        if (user.isBanned())
            return LoginStatus.ACCOUNT_BANNED;

        if (user.isSuspended())
            return LoginStatus.ACCOUNT_SUSPENDED;

        if (user.isSuspectedStolen())
            return LoginStatus.ACCOUNT_SUSPECT_STOLEN;

        return LoginStatus.SUCCESSFUL_LOGIN;
    }

    private SessionData login(String username, byte[] password, int[] keys) {
        final Optional<User> user = userDAO.findByUsername(username);
        if (!user.isPresent())
            return SessionData.invalidSession(LoginStatus.INVALID_CREDENTIALS);

        final LoginStatus status = this.validateUser(user.get(), password);
        if (!status.isSuccess())
            return SessionData.invalidSession(status);

        final long sessionId = SessionResource.generateSessionId(user.get().getId(), keys);
        return new SessionData(status, sessionId, user.get().getUsername(), user.get().isMember(), user.get().isVeteran());
    }

    @PUT
    @UnitOfWork( transactional = true )
    public Response login(LoginCredentials credentials) {
        final SessionData data = this.login(credentials.getUsername(), credentials.getPassword(), credentials.getKeys());
        return Response.status(Response.Status.OK).entity(data).build();
    }
}
