package com.jamierf.rsc.client;

import com.jamierf.rsc.client.config.RSClientConfiguration;
import com.jamierf.rsc.client.error.ClientHandlerExceptionMapper;
import com.jamierf.rsc.client.error.UniformInterfaceExceptionMapper;
import com.jamierf.rsc.client.jag.ResourceStore;
import com.jamierf.rsc.client.loader.client.GameClient;
import com.jamierf.rsc.client.loader.client.GameClientLifeCycleManager;
import com.jamierf.rsc.client.resources.ProxyResource;
import com.sun.jersey.api.client.Client;
import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.net.URI;
import java.security.Security;

public class RSClient extends Application<RSClientConfiguration> {

    private static final String CLIENT_TITLE = "Test";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(String[] args) throws Exception {
        new RSClient().run(args);
    }

    @Override
    public void initialize(Bootstrap<RSClientConfiguration> bootstrap) {

    }

    @Override
    public void run(final RSClientConfiguration configuration, final Environment environment) throws Exception {
        final URI proxy = URI.create("http://localhost:8090"); // TODO
        System.getProperties().put("http.proxyHost", proxy.getHost());
        System.getProperties().put("http.proxyPort", String.valueOf(proxy.getPort()));

        final Client proxyClient = new JerseyClientBuilder(environment).using(configuration.getProxyConfiguration()).build("proxy");
        final ResourceStore resourceStore = new ResourceStore(proxyClient, configuration.getProxyConfiguration());

        environment.jersey().register(new ProxyResource(resourceStore));

        environment.jersey().register(new ClientHandlerExceptionMapper());
        environment.jersey().register(new UniformInterfaceExceptionMapper());

        final GameClient client = new GameClient(
                configuration.getProxyConfiguration().getRemoteHost().toURL(),
                configuration.getServerHost(),
                configuration.getPublicKey(),
                CLIENT_TITLE
        );
        environment.lifecycle().addLifeCycleListener(new GameClientLifeCycleManager(client));
    }
}
