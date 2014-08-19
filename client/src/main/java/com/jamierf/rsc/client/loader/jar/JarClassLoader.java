package com.jamierf.rsc.client.loader.jar;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.io.Files;
import javassist.ClassPath;
import javassist.NotFoundException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

public class JarClassLoader extends URLClassLoader implements ClassPath, Closeable {

    private static final String CLASS_FILE_EXTENSION = ".class";
    private static final File CACHE_DIR = Files.createTempDir();

    private static final Logger LOG = LoggerFactory.getLogger(JarClassLoader.class);

    private static URL[] extractJar(URL jarURL) throws IOException {
        // Extract all files from the jar
        try (final JarInputStream in = new JarInputStream(jarURL.openStream())) {
            for (ZipEntry entry; (entry = in.getNextEntry()) != null;) {
                final File localFile = new File(CACHE_DIR, entry.getName());
                Files.asByteSink(localFile).writeFrom(in);
            }
        }

        return new URL[]{ CACHE_DIR.toURI().toURL() };
    }

    public JarClassLoader(URL jarURL, ClassLoader parent) throws IOException {
        super(JarClassLoader.extractJar(jarURL), parent);
    }

    @Override
    public InputStream openClassfile(String type) throws NotFoundException {
        return this.getResourceAsStream(type.concat(CLASS_FILE_EXTENSION));
    }

    @Override
    public URL find(String type) {
        return findResource(type.concat(CLASS_FILE_EXTENSION));
    }

    @Override
    public void close() {
        try {
            super.close();
        }
        catch (IOException e) {
            LOG.warn("Error closing class loader", e);
        }
    }

    public Collection<String> listClassNames() {
        final Collection<File> files = FileUtils.listFiles(CACHE_DIR, new String[]{ CLASS_FILE_EXTENSION.substring(1) }, true);

        final int pathLength = CACHE_DIR.getAbsolutePath().length() + 1; // + 1 accounts for the trailing slash
        final int extensionLength = CLASS_FILE_EXTENSION.length();

        return Collections2.transform(files, new Function<File, String>() {
            @Override
            public String apply(File input) {
                final String name = input.getAbsolutePath();
                return name.substring(pathLength, name.length() - extensionLength).replaceAll(File.separator, ".");
            }
        });
    }
}
