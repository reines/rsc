package com.jamierf.rsc.client.loader;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import javassist.ClassPath;
import javassist.NotFoundException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

public class DirectoryClassLoader extends URLClassLoader implements ClassPath {

    private static final String CLASS_FILE_EXTENSION = "class";

    private final File file;

    public DirectoryClassLoader(File file) throws MalformedURLException {
        super(new URL[]{file.toURI().toURL()});

        this.file = file;
    }

    public DirectoryClassLoader(File file, ClassLoader parent) throws MalformedURLException {
        super(new URL[]{file.toURI().toURL()}, parent);

        this.file = file;
    }

    @Override
    public InputStream openClassfile(String classname) throws NotFoundException {
        classname = classname.concat(".").concat(CLASS_FILE_EXTENSION);
        return super.getResourceAsStream(classname);
    }

    @Override
    public URL find(String classname) {
        classname = classname.concat(".").concat(CLASS_FILE_EXTENSION);
        return super.findResource(classname);
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
        final Collection<File> files = FileUtils.listFiles(file, new String[]{ CLASS_FILE_EXTENSION }, true);

        final int pathLength = file.getAbsolutePath().length() + 1; // + 1 accounts for the trailing slash
        final int extensionLength = CLASS_FILE_EXTENSION.length() + 1; // + 1 accounts for the period of the extension

        return Collections2.transform(files, new Function<File, String>() {
            @Override
            public String apply(File input) {
                final String name = input.getAbsolutePath();
                return name.substring(pathLength, name.length() - extensionLength).replaceAll(File.separator, ".");
            }
        });
    }
}
