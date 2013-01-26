package com.jamierf.rsc.server.net.handlers;

import com.jamierf.rsc.server.net.PacketHandler;
import com.jamierf.rsc.server.net.Session;
import com.jamierf.rsc.server.net.packet.LoginRequestPacket;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;

import java.security.interfaces.RSAPrivateKey;

public class LoginHandler extends PacketHandler<LoginRequestPacket> {

    public static enum LoginStatus {
        REGULAR_LOGIN(Byte.MAX_VALUE, true),

        INVALID_CREDENTIALS(3, false),
        ALREADY_LOGGED_IN(4, false),
        CLIENT_UPDATED(5, false),
        IP_IN_USE(6, false),
        LOGIN_THROTTLED(7, false),
        SESSION_REJECTED(8, false),
        UNDERAGE_ACCOUNT(9, false),
        ACCOUNT_IN_USE(10, false),
        ACCOUNT_SUSPENDED(11, false),
        ACCOUNT_BANNED(12, false),
        SERVER_FULL(14, false),
        MEMBERSHIP_REQUIRED(15, false),
        LOGIN_SERVER_ERROR(16, false),
        CORRUPT_PROFILE(17, false),
        ACCOUNT_SUSPECT_STOLEN(18, false),
        LOGIN_SERVER_MISMATCH(20, false),
        VETERAN_ACCOUNT_REQUIRED(21, false),
        PASS_SUSPECT_STOLEN(22, false),
        DISPLAY_NAME_REQUIRED(23, false),
        NEW_ACCOUNTS_DISABLED(24, false),
        ALL_CHARACTERS_BLOCKED(25, false);


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

    private static ChannelBuffer buildResponse(LoginStatus status) {
        final ChannelBuffer payload = ChannelBuffers.buffer(Byte.SIZE);
        payload.writeByte(status.getCode());

        return payload;
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
        System.err.println("Attempting to login for: " + username);
        return LoginStatus.REGULAR_LOGIN; // TODO
    }

    @Override
    public void handle(ChannelHandlerContext ctx, Session session, LoginRequestPacket packet) throws Exception {
        final LoginRequestPacket.SessionData sessionData = packet.decryptSessionData(key);
        final LoginRequestPacket.LoginData loginData = packet.decryptLoginData(sessionData.getSessionKeys());

        // Attempt to log the user in
        final LoginStatus status = this.attemptLogin(session, loginData.getUsername(), sessionData.getPassword(), packet.isReconnecting());

        // Create a session for this client
        session = new Session(ctx.getChannel(),loginData.getUsername(), sessionData.getSessionKeys());
        ctx.setAttachment(session);

        // Send the response
        ctx.getChannel().write(LoginHandler.buildResponse(status)).sync();

        // We failed to login, end the session
        if (!status.isSuccess()) {
            session.close();
            return;
        }

        // TODO: Send them the required shit
    }
}
