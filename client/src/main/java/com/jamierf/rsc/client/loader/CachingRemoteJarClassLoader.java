package com.jamierf.rsc.client.loader;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import javassist.ClassPath;
import javassist.NotFoundException;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

public class CachingRemoteJarClassLoader extends URLClassLoader implements ClassPath, Closeable {

    private static final String CLASS_FILE_EXTENSION = ".class";
    private static final String CACHE_DIR_NAME = "game-client";

    private static final File CACHE_DIR = CachingRemoteJarClassLoader.getTempDirectory();

    private static File getTempDirectory() {
        final File tempDir = new File(System.getProperty("java.io.tmpdir"));
        if (!tempDir.isDirectory())
            return Files.createTempDir();

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
        final File localJar = new File(CACHE_DIR, "rsclassic.jar");
        if (!localJar.exists()) {
            System.err.println("Downloading copy of jar, none locally");
            ByteStreams.copy(jarURL.openStream(), Files.newOutputStreamSupplier(localJar));
        }

        // Extract all classes from the jar
        try (final JarInputStream in = new JarInputStream(new FileInputStream(localJar), true)) {
            for (ZipEntry entry; (entry = in.getNextEntry()) != null;) {
                // Skip non class files
                final String name = entry.getName(); // TODO: Handle packages, replace . (except extension) with /
                if (!name.endsWith(CLASS_FILE_EXTENSION))
                    continue;

                try (final FileOutputStream out = new FileOutputStream(new File(CACHE_DIR, entry.getName()))) {
                    ByteStreams.copy(in, out);
                }
            }
        }

        return new URL[]{ CACHE_DIR.toURI().toURL() };
    }


    public CachingRemoteJarClassLoader(URL jarURL) throws IOException {
        super(CachingRemoteJarClassLoader.ensureCachedClasses(jarURL));
    }

    public CachingRemoteJarClassLoader(URL jarURL, ClassLoader parent) throws IOException {
        super(CachingRemoteJarClassLoader.ensureCachedClasses(jarURL), parent);
    }

    @Override
    public InputStream openClassfile(String classname) throws NotFoundException {
        classname = classname.concat(CLASS_FILE_EXTENSION);
        return this.getResourceAsStream(classname);
    }

    @Override
    public URL find(String classname) {
        classname = classname.concat(CLASS_FILE_EXTENSION);
        return this.findResource(classname);
    }

    @Override
    public void close() {
        try {
            super.close();
        }
        catch (IOException e) {
            e.printStackTrace();
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
