package com.jamierf.rsc.client.resources;

import com.google.common.io.ByteStreams;
import com.jamierf.rsc.client.jag.ResourceStore;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Path("/")
public class ProxyResource {

    private final ResourceStore store;

    public ProxyResource(ResourceStore store) {
        this.store = store;
    }

    @GET
    @Path("/{path:.*}")
    public StreamingOutput proxy(@PathParam("path") final String path) throws IOException {
        return new StreamingOutput() {
            @Override
            public void write(OutputStream out) throws IOException, WebApplicationException {
                try (final InputStream in = store.getResource(path)) {
                    ByteStreams.copy(in, out);
                }
            }
        };
    }
}
