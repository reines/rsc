package com.jamierf.rsc.dataserver.client;

import com.jamierf.rsc.dataserver.api.LoginCredentials;
import com.jamierf.rsc.dataserver.api.SessionData;
import com.jamierf.rsc.dataserver.client.config.DataserverClientConfig;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.core.MediaType;

public class DataserverClient {

    private static final String LOGIN_PATH = "/session";

    private final WebResource resource;

    public DataserverClient(Client client, DataserverClientConfig config) {
        resource = client.resource(config.getRoot());
    }

    public SessionData requestLogin(String username, byte[] password, int[] keys) {
        final LoginCredentials credentials = new LoginCredentials(username, password, keys);
        return resource.path(LOGIN_PATH)
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .put(SessionData.class, credentials);
    }
}
