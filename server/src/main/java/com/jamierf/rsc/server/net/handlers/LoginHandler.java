package com.jamierf.rsc.server.net.handlers;

import com.google.common.collect.Maps;
import com.jamierf.rsc.dataserver.api.LoginStatus;
import com.jamierf.rsc.dataserver.api.SessionCredentials;
import com.jamierf.rsc.server.net.PacketHandler;
import com.jamierf.rsc.server.net.packet.LoginRequestPacket;
import com.jamierf.rsc.server.net.session.Session;
import com.jamierf.rsc.server.net.session.SessionCreationException;
import com.jamierf.rsc.server.net.session.SessionManager;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Meter;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

import java.security.interfaces.RSAPrivateKey;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginHandler extends PacketHandler<LoginRequestPacket> {

    private static final Meter CONNECTION_METER = Metrics.newMeter(LoginHandler.class, "connections", "requests", TimeUnit.SECONDS);
    private static final Meter RECONNECTION_METER = Metrics.newMeter(LoginHandler.class, "reconnections", "requests", TimeUnit.SECONDS);
    private static final Meter REJECTION_METER = Metrics.newMeter(LoginHandler.class, "rejections", "requests", TimeUnit.SECONDS);

    private static final Map<LoginStatus, Meter> STATUS_METER = Maps.newEnumMap(LoginStatus.class);

    private static Meter getStatusMeter(LoginStatus status) {
        synchronized (STATUS_METER) {
            if (!STATUS_METER.containsKey(status))
                STATUS_METER.put(status, Metrics.newMeter(LoginStatus.class, status.toString(), "requests", TimeUnit.SECONDS));

            return STATUS_METER.get(status);
        }
    }

    private static void sendLoginResponse(ChannelHandlerContext ctx, LoginStatus status) throws InterruptedException {
        final Channel channel = ctx.getChannel();

        final ChannelBuffer payload = ChannelBuffers.buffer(Byte.SIZE);
        payload.writeByte(status.getCode());

        // Send the response (and wait for it to be sent)
        channel.write(payload).sync();

        // This was an unsuccessful login, so close their connection
        if (!status.isSuccess())
            channel.close().sync();

        LoginHandler.getStatusMeter(status).mark();
    }

    private final SessionManager sessionManager;
    private final RSAPrivateKey key;

    public LoginHandler(SessionManager sessionManager, RSAPrivateKey key) {
        this.sessionManager = sessionManager;
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

        try {
            final String username = sessionData.getUsername();
            final String password = sessionData.getPassword();
            final int[] keys = sessionData.getSessionKeys();

            // TODO: Validate the client version is recognised

            if (!SessionCredentials.isValid(username, password, keys))
                throw new SessionCreationException(LoginStatus.INVALID_CREDENTIALS);

            // Create a session for this client
            session = sessionManager.createSession(ctx.getChannel(), username, password, keys, packet.getClientVersion(), packet.isReconnecting());
            ctx.setAttachment(session);

            // Send successful login response to the client
            LoginHandler.sendLoginResponse(ctx, LoginStatus.SUCCESSFUL_LOGIN);

            // Mark if this is a connection or a reconnection
            (packet.isReconnecting() ? RECONNECTION_METER : CONNECTION_METER).mark();

            // TODO: Send them the required shit
        }
        catch (SessionCreationException e) {
            // Error creating session, let the client know then kill their connection
            LoginHandler.sendLoginResponse(ctx, e.getResponse());

            // Mark this as a rejection
            REJECTION_METER.mark();
        }
    }
}
