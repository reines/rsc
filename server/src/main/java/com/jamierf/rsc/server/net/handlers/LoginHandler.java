package com.jamierf.rsc.server.net.handlers;

import com.codahale.metrics.MetricRegistry;
import com.jamierf.rsc.dataserver.api.LoginStatus;
import com.jamierf.rsc.dataserver.api.SessionCredentials;
import com.jamierf.rsc.server.net.PacketHandler;
import com.jamierf.rsc.server.net.packet.LoginRequestPacket;
import com.jamierf.rsc.server.net.session.Session;
import com.jamierf.rsc.server.net.session.SessionCreationException;
import com.jamierf.rsc.server.net.session.SessionManager;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

import java.security.interfaces.RSAPrivateKey;

public class LoginHandler extends PacketHandler<LoginRequestPacket> {

    private static final String CONNECTION_METER_NAME = "connections";
    private static final String RECONNECTION_METER_NAME = "reconnections";
    private static final String REJECTION_METER_NAME = "rejections";
    private static final String STATUS_METER_TEMPLATE = "status-%s";

    private final SessionManager sessionManager;
    private final MetricRegistry metricRegistry;
    private final RSAPrivateKey key;

    public LoginHandler(SessionManager sessionManager, MetricRegistry metricRegistry, RSAPrivateKey key) {
        this.sessionManager = sessionManager;
        this.metricRegistry = metricRegistry;
        this.key = key;
    }

    @Override
    public Class<LoginRequestPacket> getRequestType() {
        return LoginRequestPacket.class;
    }

    @Override
    public boolean isSessionRequired() {
        return false;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, Session session, LoginRequestPacket packet) throws Exception {
        final LoginRequestPacket.SessionData sessionData = packet.decryptSessionData(key);
        final LoginRequestPacket.LoginData loginData = packet.decryptLoginData(sessionData.getSessionKeys());

        try {
            final String username = loginData.getUsername();
            final String password = sessionData.getPassword();
            final int[] keys = sessionData.getSessionKeys();

            // TODO: Validate the client version is recognised

            if (!SessionCredentials.isValid(username, password, keys))
                throw new SessionCreationException(LoginStatus.INVALID_CREDENTIALS);

            // Create a session for this client
            session = sessionManager.createSession(ctx.getChannel(), username, password, keys, packet.getClientVersion(), packet.isReconnecting());
            ctx.setAttachment(session);

            // Send successful login response to the client
            sendLoginResponse(ctx, LoginStatus.SUCCESSFUL_LOGIN);

            // Mark if this is a connection or a reconnection
            metricRegistry.meter(packet.isReconnecting() ? RECONNECTION_METER_NAME : CONNECTION_METER_NAME).mark();

            // TODO: Send them the required shit
        }
        catch (SessionCreationException e) {
            // Error creating session, let the client know then kill their connection
            sendLoginResponse(ctx, e.getResponse());

            // Mark this as a rejection
            metricRegistry.meter(REJECTION_METER_NAME).mark();
        }
    }

    private void sendLoginResponse(ChannelHandlerContext ctx, LoginStatus status) throws InterruptedException {
        final Channel channel = ctx.getChannel();

        final ChannelBuffer payload = ChannelBuffers.buffer(Byte.SIZE);
        payload.writeByte(status.getCode());

        // Send the response (and wait for it to be sent)
        channel.write(payload).sync();

        // This was an unsuccessful login, so close their connection
        if (!status.isSuccess()) {
            channel.close().sync();
        }

        metricRegistry.meter(String.format(STATUS_METER_TEMPLATE, status)).mark();
    }
}
