package com.jamierf.rsc.dataserver.service.resources;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.jamierf.rsc.dataserver.api.UserCredentials;
import com.jamierf.rsc.dataserver.api.UserData;
import com.jamierf.rsc.dataserver.service.db.User;
import com.jamierf.rsc.dataserver.service.db.UserDAO;
import com.yammer.dropwizard.hibernate.UnitOfWork;
import com.yammer.metrics.annotation.Timed;
import org.hibernate.exception.ConstraintViolationException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/user/{username}")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserDAO userDAO;

    public UserResource(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @PUT
    @Timed
    @UnitOfWork ( transactional = true )
    public UserData create(@PathParam("username") String username, @QueryParam("password") String password) {
        try {
            if (!UserCredentials.isValid(username, password))
                throw new WebApplicationException(Response.Status.FORBIDDEN);

            // Clean the username before attempting to find it
            username = User.cleanUsername(username);

            // TODO: Validate the username and password matches criteria

            return userDAO.create(username, password);
        }
        catch (ConstraintViolationException e) {
            throw new WebApplicationException(e, Response.Status.CONFLICT);
        }
    }

    @GET
    @Timed
    @UnitOfWork( readOnly = true )
    public UserData find(@PathParam("username") String username) {
        if (Strings.isNullOrEmpty(username))
            throw new WebApplicationException(Response.Status.BAD_REQUEST);

        // Clean the username before attempting to find it
        username = User.cleanUsername(username);

        final Optional<User> user = userDAO.findByUsername(username);
        if (!user.isPresent())
            throw new WebApplicationException(Response.Status.NOT_FOUND);

        return user.get();
    }
}
