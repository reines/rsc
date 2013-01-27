package com.jamierf.rsc.dataserver.service.resources;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.jamierf.rsc.dataserver.api.UserCredentials;
import com.jamierf.rsc.dataserver.api.UserData;
import com.jamierf.rsc.dataserver.service.db.User;
import com.jamierf.rsc.dataserver.service.db.UserDAO;
import com.yammer.dropwizard.hibernate.UnitOfWork;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Timer;
import com.yammer.metrics.core.TimerContext;
import org.hibernate.exception.ConstraintViolationException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private static final Timer CREATE_USER_TIMER = Metrics.newTimer(UserResource.class, "create");
    private static final Timer FIND_USER_TIMER = Metrics.newTimer(UserResource.class, "find");

    private final UserDAO userDAO;

    public UserResource(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @PUT
    @UnitOfWork ( transactional = true )
    @Path("/{username}")
    public Response create(@PathParam("username") String username, @QueryParam("password") String password) {
        final TimerContext timer = CREATE_USER_TIMER.time();

        try {
            if (!UserCredentials.isValid(username, password))
                return Response.status(Response.Status.BAD_REQUEST).build();

            // Clean the username before attempting to find it
            username = User.cleanUsername(username);

            // TODO: Validate the username and password matches criteria

            final User user = userDAO.create(username, password);
            return Response.status(Response.Status.OK).entity((UserData) user).build();
        }
        catch (ConstraintViolationException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        }
        finally {
            timer.stop();
        }
    }

    @GET
    @UnitOfWork( readOnly = true )
    @Path("/{username}")
    public Response find(@PathParam("username") String username) {
        final TimerContext timer = FIND_USER_TIMER.time();

        try {
            if (Strings.isNullOrEmpty(username))
                return Response.status(Response.Status.BAD_REQUEST).build();

            // Clean the username before attempting to find it
            username = User.cleanUsername(username);

            final Optional<User> user = userDAO.findByUsername(username);
            if (!user.isPresent())
                return Response.status(Response.Status.NOT_FOUND).build();

            return Response.status(Response.Status.OK).entity((UserData) user.get()).build();
        }
        finally {
            timer.stop();
        }
    }
}
