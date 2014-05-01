package com.jamierf.rsc.client.loader;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.io.Files;
import javassist.ClassPath;
import javassist.NotFoundException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

public class CachingRemoteJarClassLoader extends URLClassLoader implements ClassPath, Closeable {

    private static final String CLASS_FILE_EXTENSION = ".class";
    private static final String CACHE_DIR_NAME = "game-client";

    private static final Logger LOG = LoggerFactory.getLogger(CachingRemoteJarClassLoader.class);
    private static final File CACHE_DIR = CachingRemoteJarClassLoader.getTempDirectory();

    private static File getTempDirectory() {
        final File tempDir = new File(System.getProperty("java.io.tmpdir"));
        if (!tempDir.isDirectory()) {
            return Files.createTempDir();
        }

        try {
            final File dir = new File(tempDir, CACHE_DIR_NAME);
            FileUtils.forceMkdir(dir);

            return dir;
        }
        catch (IOException e) {
            return Files.createTempDir();
        }
    }

    private static URL[] ensureCachedClasses(URL jarURL) throws IOException {
        LOG.info("Using cache directory: {}", CACHE_DIR);

        final File localJar = new File(CACHE_DIR, "rsclassic.jar");
        if (!localJar.exists()) {
            try (final InputStream in = jarURL.openStream()) {
                LOG.debug("Downloading copy of jar to {}, not found locally", localJar);
                Files.asByteSink(localJar).writeFrom(in);
            }
        }

        // Extract all classes from the jar
        try (final JarInputStream in = new JarInputStream(new FileInputStream(localJar), true)) {
            for (ZipEntry entry; (entry = in.getNextEntry()) != null;) {
                // Skip non class files
                final String name = entry.getName();
                if (!name.endsWith(CLASS_FILE_EXTENSION)) {
                    continue;
                }

                final File classFile = new File(CACHE_DIR, entry.getName());
                if (!classFile.exists()) {
                    Files.asByteSink(classFile).writeFrom(in);
                }
            }
        }

        return new URL[]{ CACHE_DIR.toURI().toURL() };
    }


    public CachingRemoteJarClassLoader(URL jarURL, ClassLoader parent) throws IOException {
        super(CachingRemoteJarClassLoader.ensureCachedClasses(jarURL), parent);
    }

    @Override
    public InputStream openClassfile(String type) throws NotFoundException {
        return this.getResourceAsStream(type.concat(CLASS_FILE_EXTENSION));
    }

    @Override
    public URL find(String type) {
        return this.findResource(type.concat(CLASS_FILE_EXTENSION));
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
