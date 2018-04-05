package com.ctrlshift.server.file;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import javax.annotation.Nullable;

import com.ctrlshift.commons.HttpData;

final class ClassPathHttpVfs extends AbstractHttpVfs {

    private final ClassLoader classLoader;
    private final String rootDir;

    ClassPathHttpVfs(ClassLoader classLoader, String rootDir) {
        this.classLoader = requireNonNull(classLoader, "classLoader");
        this.rootDir = normalizeRootDir(rootDir);
    }

    private static String normalizeRootDir(String rootDir) {
        requireNonNull(rootDir, "rootDir");
        if (rootDir.startsWith("/")) {
            rootDir = rootDir.substring(1);
        }

        if (rootDir.endsWith("/")) {
            rootDir = rootDir.substring(0, rootDir.length() - 1);
        }

        return rootDir;
    }

    @Override
    public Entry get(String path, @Nullable String contentEncoding) {
        final String resourcePath = rootDir.isEmpty() ? path.substring(1) : rootDir + path;
        final URL url = classLoader.getResource(resourcePath);
        if (url == null || url.getPath().endsWith("/")) {
            return Entry.NONE;
        }

        final Entry entry;
        // Convert to a real file if possible.
        if ("file".equals(url.getProtocol())) {
            File f;
            try {
                f = new File(url.toURI());
            } catch (URISyntaxException ignored) {
                f = new File(url.getPath());
            }

            entry = new FileSystemHttpVfs.FileSystemEntry(f, path, contentEncoding);
        } else {
            entry = new ClassPathEntry(url, path, contentEncoding);
        }

        return entry;
    }

    @Override
    public String meterTag() {
        return "classpath:" + rootDir;
    }

    static final class ClassPathEntry extends AbstractEntry {

        private final URL url;
        private final long lastModifiedMillis = System.currentTimeMillis();

        ClassPathEntry(URL url, String path, @Nullable String contentEncoding) {
            super(path, contentEncoding);
            this.url = url;
        }

        @Override
        public long lastModifiedMillis() {
            return lastModifiedMillis;
        }

        @Override
        public HttpData readContent() throws IOException {
            try (InputStream in = url.openStream()) {
                return readContent(in);
            }
        }
    }
}
