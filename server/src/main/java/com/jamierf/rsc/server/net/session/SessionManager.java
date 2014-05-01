package com.jamierf.rsc.server.net.session;

import com.google.common.collect.Maps;
import com.jamierf.rsc.dataserver.api.LoginStatus;
import com.jamierf.rsc.dataserver.api.SessionData;
import com.jamierf.rsc.dataserver.client.DataserverClient;
import com.jamierf.rsc.server.net.codec.packet.PacketRotator;
import com.sun.jersey.api.client.UniformInterfaceException;
import io.dropwizard.lifecycle.Managed;
import org.eclipse.jetty.http.HttpStatus;
import org.jboss.netty.channel.Channel;

import java.util.Map;

public class SessionManager implements Managed {

    private final DataserverClient client;
    private final Map<Long, Session> sessions;

    public SessionManager(DataserverClient client) {
        this.client = client;

        sessions = Maps.newConcurrentMap(); // TODO: Ensure these fall out some timeout after they are closed (either locally or remotely)
    }

    private synchronized Session getOrCreateSession(SessionData data, int[] keys) {
        // If reconnecting and there is already a session for this client
        if (sessions.containsKey(data.getSessionId()))
            return sessions.get(data.getSessionId());

        final PacketRotator packetRotator = new PacketRotator(keys);

        // Create a new session
        final Session session = new Session(data.getSessionId(), data.getUsername(), packetRotator);
        sessions.put(session.getId(), session);

        return session;
    }

    public Session createSession(Channel channel, String username, String password, int[] keys, int clientVersion, boolean reconnecting) throws SessionCreationException {
        try {
            final SessionData data = client.createSession(username, password, clientVersion, keys);

            // Create (or retrieve existing) session
            final Session session = this.getOrCreateSession(data, keys);
            if (!session.moveChannel(channel, reconnecting))
                throw new SessionCreationException(LoginStatus.SESSION_REJECTED);

            return session;
        }
        catch (UniformInterfaceException e) {
            switch (e.getResponse().getStatus()) {
                case HttpStatus.BAD_REQUEST_400: throw new SessionCreationException(LoginStatus.LOGIN_SERVER_MISMATCH);
                case HttpStatus.FORBIDDEN_403: throw new SessionCreationException(LoginStatus.INVALID_CREDENTIALS);
                case HttpStatus.CONFLICT_409: throw new SessionCreationException(LoginStatus.ACCOUNT_BANNED);
                default: throw new SessionCreationException(LoginStatus.LOGIN_SERVER_ERROR, e);
            }
        }
    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void stop() throws Exception {

    }
}
