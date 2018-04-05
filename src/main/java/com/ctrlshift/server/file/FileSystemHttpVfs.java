package com.ctrlshift.server.file;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.Nullable;

import com.ctrlshift.commons.HttpData;

final class FileSystemHttpVfs extends AbstractHttpVfs {

    private static final boolean FILE_SEPARATOR_IS_NOT_SLASH = File.separatorChar != '/';

    private final Path rootDir;

    FileSystemHttpVfs(Path rootDir) {
        this.rootDir = requireNonNull(rootDir, "rootDir").toAbsolutePath();
        if (!Files.exists(this.rootDir) || !Files.isDirectory(this.rootDir)) {
            throw new IllegalArgumentException("rootDir: " + rootDir + " (not a directory");
        }
    }

    @Override
    public Entry get(String path, @Nullable String contentEncoding) {
        // Replace '/' with the platform dependent file separator if necessary.
        if (FILE_SEPARATOR_IS_NOT_SLASH) {
            path = path.replace(File.separatorChar, '/');
        }

        final File f = new File(rootDir + path);
        if (!f.isFile() || !f.canRead()) {
            return Entry.NONE;
        }

        return new FileSystemEntry(f, path, contentEncoding);
    }

    @Override
    public String meterTag() {
        return "file:" + rootDir;
    }

    static final class FileSystemEntry extends AbstractEntry {

        private final File file;

        FileSystemEntry(File file, String path, @Nullable String contentEncoding) {
            super(path, contentEncoding);
            this.file = file;
        }

        @Override
        public long lastModifiedMillis() {
            return file.lastModified();
        }

        @Override
        public HttpData readContent() throws IOException {
            final long fileLength = file.length();
            if (fileLength > Integer.MAX_VALUE) {
                throw new IOException("file too large: " + file + " (" + fileLength + " bytes)");
            }

            try (InputStream in = new FileInputStream(file)) {
                return readContent(in, (int) fileLength);
            }
        }
    }
}