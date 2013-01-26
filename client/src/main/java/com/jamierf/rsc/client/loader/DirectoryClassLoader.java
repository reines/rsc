package com.jamierf.rsc.client.loader;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import javassist.ClassPath;
import javassist.NotFoundException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

public class DirectoryClassLoader extends URLClassLoader implements ClassPath {

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
        return super.getResourceAsStream(classname.concat(".class"));
    }

    @Override
    public URL find(String classname) {
        return super.findResource(classname.concat(".class"));
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

    public Iterable<String> getClassNames() {
        // Note: Doesn't handle packages!
        final List<String> files = Lists.transform(Arrays.asList(file.list()), new Function<String, String>() {
            @Override
            public String apply(String input) {
                return input.endsWith(".class") ? input.substring(0, input.length() - 6) : null;
            }
        });

        return Iterables.filter(files, Predicates.notNull());
    }
}
