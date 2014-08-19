package com.jamierf.rsc.client.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.bouncycastle.openssl.PEMReader;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;

public class RSClientConfiguration extends Configuration {

    @NotNull
    @JsonProperty
    private URL serverHost;

    @NotNull
    @JsonProperty
    private File publicKey;

    @NotNull
    @JsonProperty("proxy")
    private ProxyConfiguration proxyConfiguration;

    public URL getServerHost() {
        return serverHost;
    }

    public RSAPublicKey getPublicKey() throws IOException {
        try (final PEMReader reader = new PEMReader(new FileReader(publicKey))) {
            return (RSAPublicKey) reader.readObject();
        }
    }

    public ProxyConfiguration getProxyConfiguration() {
        return proxyConfiguration;
    }
}
