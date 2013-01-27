package com.jamierf.rsc.server.net.handlers;

import com.jamierf.rsc.dataserver.api.LoginStatus;
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

    private static void sendLoginResponse(ChannelHandlerContext ctx, LoginStatus status) throws InterruptedException {
        final Channel channel = ctx.getChannel();

        final ChannelBuffer payload = ChannelBuffers.buffer(Byte.SIZE);
        payload.writeByte(status.getCode());

        // Send the response (and wait for it to be sent)
        channel.write(payload).sync();

        // This was an unsuccessful login, so close their connection
        if (!status.isSuccess())
            channel.close().sync();
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
        final LoginRequestPacket.LoginData loginData = packet.decryptLoginData(sessionData.getSessionKeys());

        try {
            // Create a session for this client
            session = sessionManager.getSession(ctx.getChannel(), loginData.getUsername(), sessionData.getPassword(), sessionData.getSessionKeys(), packet.isReconnecting());
            ctx.setAttachment(session);

            // Send successful login response to the client
            LoginHandler.sendLoginResponse(ctx, LoginStatus.SUCCESSFUL_LOGIN);

            // TODO: Send them the required shit
        }
        catch (SessionCreationException e) {
            // Error creating session, let the client know then kill their connection
            LoginHandler.sendLoginResponse(ctx, e.getResponse());
        }
    }
}
