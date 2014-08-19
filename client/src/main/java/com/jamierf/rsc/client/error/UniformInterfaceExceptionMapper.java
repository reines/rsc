package com.jamierf.rsc.client.error;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class UniformInterfaceExceptionMapper implements ExceptionMapper<UniformInterfaceException> {
    @Override
    public Response toResponse(final UniformInterfaceException exception) {
        return Response.status(ClientResponse.Status.BAD_GATEWAY)
                .type(MediaType.TEXT_PLAIN)
                .entity(exception.getMessage())
                .build();
    }
}
