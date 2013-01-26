package com.jamierf.rsc.server;

import com.jamierf.rsc.server.config.ServerConfiguration;
import com.jamierf.rsc.server.net.ClientAcceptor;
import com.jamierf.rsc.server.net.handlers.LoginHandler;
import com.jamierf.rsc.server.net.handlers.LogoutHandler;
import com.jamierf.rsc.server.net.handlers.SessionHandler;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.security.interfaces.RSAPrivateKey;

public class Server extends Service<ServerConfiguration> {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(String[] args) throws Exception {
        new Server().run(args);
    }

    private ClientAcceptor acceptor;

    @Override
    public void initialize(Bootstrap<ServerConfiguration> bootstrap) {
        // TODO
    }

    @Override
    public void run(ServerConfiguration config, Environment env) throws Exception {
        acceptor = new ClientAcceptor(config.getPort());

        // TODO: This should really be handled in some kind of configuration?
        acceptor.addPacketHandler(32, new SessionHandler());
        acceptor.addPacketHandler(0, new LoginHandler((RSAPrivateKey) config.getKeyPair().getPrivate()));
        acceptor.addPacketHandler(29, new LogoutHandler());

        env.manage(acceptor);
    }
}
