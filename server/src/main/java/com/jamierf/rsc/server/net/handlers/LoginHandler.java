package com.jamierf.rsc.server.net.handlers;

import com.jamierf.rsc.server.net.PacketHandler;
import com.jamierf.rsc.server.net.Session;
import com.jamierf.rsc.server.net.packet.LoginRequestPacket;
import com.jamierf.rsc.server.net.packet.LoginResponsePacket;
import org.jboss.netty.channel.ChannelHandlerContext;

import java.security.interfaces.RSAPrivateKey;

public class LoginHandler extends PacketHandler<LoginRequestPacket> {

    public enum LoginStatus {
        MODERATOR_LOGIN(25, true),
        REGULAR_LOGIN(0, true),
        RECONNECTION(1, true),

        INVALID_CREDENTIALS(3, false),
        ALREADY_LOGGED_IN(4, false),
        CLIENT_UPDATED(5, false),
        IP_IN_USE(6, false),
        LOGIN_THROTTLED(7, false),
        SESSION_REJECTED(8, false),
        ACCOUNT_SUSPENDED(11, false),
        ACCOUNT_BANNED(12, false),
        SERVER_FULL(14, false),
        MEMBERSHIP_REQUIRED(15, false),
        CORRUPT_PROFILE(17, false);


        private final byte code;
        private final boolean success;

        private LoginStatus(int code, boolean success) {
            this.code = (byte) code;
            this.success = success;
        }

        public byte getCode() {
            return code;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    private final RSAPrivateKey key;

    public LoginHandler(RSAPrivateKey key) {
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

    private LoginStatus attemptLogin(Session session, String username, char[] password, boolean reconnection) {
        System.err.println("Attempting to login for: " + username + " : " + new String(password));
        return LoginStatus.REGULAR_LOGIN; // TODO
    }

    @Override
    public void handle(ChannelHandlerContext ctx, Session session, LoginRequestPacket packet) throws Exception {
        final LoginRequestPacket.SessionData sessionData = packet.decryptSessionData(key);
        final LoginRequestPacket.LoginData loginData = packet.decryptLoginData(sessionData.getSessionKeys());

        // Attempt to log the user in
        final LoginStatus status = this.attemptLogin(session, loginData.getUsername(), sessionData.getPassword(), packet.isReconnecting());

        // Create a session for this client
        session = new Session(ctx.getChannel());

        // Send the response
        session.write(new LoginResponsePacket(status)).sync();

        // We failed to login, end the session
        if (!status.isSuccess()) {
            session.close();

            return;
        }

        // Attach this session to the channel
        ctx.setAttachment(session);

        // Enable packet rotation using the session keys
        session.getPacketRotator().setSeed(sessionData.getSessionKeys());

        // TODO: Update the session meta data
    }
}
