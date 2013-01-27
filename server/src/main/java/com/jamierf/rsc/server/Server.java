package com.jamierf.rsc.server;

import com.jamierf.rsc.dataserver.client.DataserverClient;
import com.jamierf.rsc.server.config.ServerConfiguration;
import com.jamierf.rsc.server.net.ClientAcceptor;
import com.jamierf.rsc.server.net.handlers.LoginHandler;
import com.jamierf.rsc.server.net.handlers.LogoutHandler;
import com.jamierf.rsc.server.net.session.SessionManager;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.client.JerseyClientBuilder;
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

    @Override
    public void initialize(Bootstrap<ServerConfiguration> bootstrap) {
        // TODO
    }

    @Override
    public void run(ServerConfiguration config, Environment env) throws Exception {
        final ClientAcceptor acceptor = new ClientAcceptor(config.getPort());
        env.manage(acceptor);

        // Create dataserver client
        final JerseyClientBuilder jerseyClientBuilder = new JerseyClientBuilder().using(env).using(config.getDataserverClientConfig().getJerseyClientConfiguration());
        final DataserverClient dataserverClient = new DataserverClient(jerseyClientBuilder.build(), config.getDataserverClientConfig());

        // Create session manager
        final SessionManager sessionManager = new SessionManager(dataserverClient);
        env.manage(sessionManager);

        // TODO: This should really be handled in some kind of configuration?
        acceptor.addPacketHandler(0, new LoginHandler(sessionManager, (RSAPrivateKey) config.getKeyPair().getPrivate()));
        acceptor.addPacketHandler(29, new LogoutHandler());
    }
}
