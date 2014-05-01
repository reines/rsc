package com.jamierf.rsc.dataserver.service.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.jamierf.rsc.dataserver.api.UserCredentials;
import com.jamierf.rsc.dataserver.api.UserData;
import com.jamierf.rsc.dataserver.service.db.UserStore;
import com.jamierf.rsc.dataserver.service.error.UserAlreadyExistsException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/user/{username}")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserStore userStore;

    public UserResource(UserStore userStore) {
        this.userStore = userStore;
    }

    @PUT
    @Timed
    public UserData create(@PathParam("username") String username, @QueryParam("password") String password) throws UserAlreadyExistsException {
        if (!UserCredentials.isValid(username, password)) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }

        // TODO: Validate the username and password matches criteria

        return userStore.create(username, password);
    }

    @GET
    @Timed
    public UserData find(@PathParam("username") String username) {
        if (Strings.isNullOrEmpty(username)) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        final Optional<UserData> user = userStore.findByUsername(username);
        if (!user.isPresent()) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return user.get();
    }
}
