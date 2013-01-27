package com.jamierf.rsc.dataserver.service.resources;

import com.google.common.base.Optional;
import com.jamierf.rsc.dataserver.api.UserCredentials;
import com.jamierf.rsc.dataserver.api.UserData;
import com.jamierf.rsc.dataserver.service.db.User;
import com.jamierf.rsc.dataserver.service.db.UserDAO;
import com.yammer.dropwizard.hibernate.UnitOfWork;
import org.hibernate.exception.ConstraintViolationException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserDAO userDAO;

    public UserResource(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @PUT
    @UnitOfWork ( transactional = true )
    public Response create(UserCredentials credentials) {
        try {
            final User user = userDAO.create(credentials.getUsername(), credentials.getPassword());
            return Response.status(Response.Status.OK).entity((UserData) user).build();
        }
        catch (ConstraintViolationException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        }
    }

    @GET
    @UnitOfWork( readOnly = true )
    @Path("/{username}")
    public Response find(@PathParam("username") String username) {
        final Optional<User> user = userDAO.findByUsername(username);
        if (!user.isPresent())
            return Response.status(Response.Status.NOT_FOUND).build();

        return Response.status(Response.Status.OK).entity((UserData) user.get()).build();
    }
}
