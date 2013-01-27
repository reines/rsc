package com.jamierf.rsc.dataserver.client;

import com.jamierf.rsc.dataserver.api.SessionCredentials;
import com.jamierf.rsc.dataserver.api.SessionData;
import com.jamierf.rsc.dataserver.api.UserCredentials;
import com.jamierf.rsc.dataserver.api.UserData;
import com.jamierf.rsc.dataserver.client.config.DataserverClientConfig;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.core.MediaType;

public class DataserverClient {

    private static final String SESSION_PATH = "/session";
    private static final String USER_PATH = "/user";

    private final WebResource resource;

    public DataserverClient(Client client, DataserverClientConfig config) {
        resource = client.resource(config.getRoot());
    }

    public SessionData createSession(String username, byte[] password, int[] keys) {
        final SessionCredentials credentials = new SessionCredentials(username, password, keys);
        return resource.path(SESSION_PATH)
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .put(SessionData.class, credentials);
    }

    public UserData createUser(String username, byte[] password) {
        final UserCredentials credentials = new UserCredentials(username, password);
        return resource.path(USER_PATH)
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .put(UserData.class, credentials);
    }

    public UserData getUser(String username) {
        return resource.path(USER_PATH)
                .path(username)
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .get(UserData.class);
    }
}
