package com.jamierf.rsc.client.error;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class ClientHandlerExceptionMapper implements ExceptionMapper<ClientHandlerException> {
    @Override
    public Response toResponse(final ClientHandlerException exception) {
        return Response.status(ClientResponse.Status.GATEWAY_TIMEOUT)
                .type(MediaType.TEXT_PLAIN)
                .entity(exception.getMessage())
                .build();
    }
}
