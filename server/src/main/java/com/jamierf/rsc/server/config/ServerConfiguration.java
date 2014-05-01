package com.jamierf.rsc.server.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jamierf.rsc.dataserver.client.config.DataserverClientConfig;
import io.dropwizard.Configuration;
import org.bouncycastle.openssl.PEMReader;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyPair;

public class ServerConfiguration extends Configuration {

    @Min(1024)
    @Max(65535)
    @JsonProperty
    private int port = 43594;

    @NotNull
    @JsonProperty("privateKey")
    private File privateKeyFile;

    @NotNull
    @JsonProperty
    private DataserverClientConfig dataserver = new DataserverClientConfig();

    public int getPort() {
        return port;
    }

    public KeyPair getKeyPair() throws IOException {
        try (final PEMReader reader = new PEMReader(new FileReader(privateKeyFile))) {
            return (KeyPair) reader.readObject();
        }
    }

    public DataserverClientConfig getDataserverClientConfig() {
        return dataserver;
    }
}
