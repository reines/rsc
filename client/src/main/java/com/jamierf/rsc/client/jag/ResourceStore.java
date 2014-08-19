package com.jamierf.rsc.client.jag;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingInputStream;
import com.google.common.io.Files;
import com.jamierf.rsc.client.config.ProxyConfiguration;
import com.sun.jersey.api.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class ResourceStore {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceStore.class);
    private static final String REMOTE_PATH_TEMPLATE = "/%s";
    private static final HashFunction HASH_FUNCTION = Hashing.md5();

    private final Client client;
    private final File root;
    private final URI remoteHost;

    public ResourceStore(Client client, ProxyConfiguration config) throws IOException {
        this.client = client;

        root = config.getCacheDirectory();
        remoteHost = config.getRemoteHost();

        if (!root.isDirectory() && !root.mkdirs()) {
            throw new IOException("Unable to create cache directory: " + root);
        }
    }

    public InputStream getResource(String name) throws IOException {
        final File localFile = new File(root, name);
        if (!localFile.exists()) {
            fetchResource(name, localFile);
        }

        LOG.debug("Loading {}", localFile);
        return new FileInputStream(localFile);
    }

    private void fetchResource(String name, File target) throws IOException {
        try (final HashingInputStream in = new HashingInputStream(HASH_FUNCTION, fetchResource(name))) {
            Files.asByteSink(target).writeFrom(in);

            LOG.debug("Downloaded {} (hash: {})", name, in.hash());
        }
    }

    private InputStream fetchResource(String name) {
        LOG.debug("Fetching {} from remote", name);

        return client.resource(remoteHost)
                .path(String.format(REMOTE_PATH_TEMPLATE, name))
                .get(InputStream.class);
    }
}
