package com.jamierf.rsc.server;

import com.jamierf.rsc.dataserver.client.DataserverClient;
import com.jamierf.rsc.server.config.ServerConfiguration;
import com.jamierf.rsc.server.net.ClientAcceptor;
import com.jamierf.rsc.server.net.handlers.LoginHandler;
import com.jamierf.rsc.server.net.handlers.LogoutHandler;
import com.jamierf.rsc.server.net.handlers.PingHandler;
import com.jamierf.rsc.server.net.session.SessionManager;
import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.security.interfaces.RSAPrivateKey;

public class Server extends Application<ServerConfiguration> {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(String[] args) throws Exception {
        new Server().run(args);
    }

    @Override
    public void initialize(Bootstrap<ServerConfiguration> bootstrap) {

    }

    @Override
    public void run(ServerConfiguration configuration, Environment environment) throws Exception {
        // Create dataserver client
        final JerseyClientBuilder jerseyClientBuilder = new JerseyClientBuilder(environment).using(configuration.getDataserverClientConfig());
        final DataserverClient dataserverClient = new DataserverClient(jerseyClientBuilder.build("dataserver"), configuration.getDataserverClientConfig());

        // Create session manager
        final SessionManager sessionManager = new SessionManager(dataserverClient);
        environment.lifecycle().manage(sessionManager);

        // Create blind acceptor
        final ClientAcceptor acceptor = new ClientAcceptor(environment.metrics(), configuration.getPort());
        environment.lifecycle().manage(acceptor);

        // TODO: This should really be handled in some kind of configuration?
        acceptor.addPacketHandler(67, new PingHandler());
        acceptor.addPacketHandler(0, new LoginHandler(sessionManager, environment.metrics(), (RSAPrivateKey) configuration.getKeyPair().getPrivate()));
        acceptor.addPacketHandler(29, new LogoutHandler());

        // TODO: Register outgoing message types, these should also be in some config
//        acceptor.addPacketType(131, SystemMessagePacket.class); // TODO: I don't think these IDs are correct
//        acceptor.addPacketType(93, ShowBankScreenPacket.class);
//        acceptor.addPacketType(0, SetPositionPacket.class);
    }
}
