package com.jamierf.rsc.dataserver.service.error;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class UserAlreadyExistsExceptionMapper implements ExceptionMapper<UserAlreadyExistsException> {
    @Override
    public Response toResponse(UserAlreadyExistsException exception) {
        return Response.status(Response.Status.CONFLICT).build();
    }
}
