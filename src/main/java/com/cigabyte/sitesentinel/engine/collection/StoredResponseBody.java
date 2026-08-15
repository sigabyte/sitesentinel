package com.cigabyte.sitesentinel.engine.collection;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

public final class StoredResponseBody implements AutoCloseable {

    private final byte[] inMemoryContent;
    private final Path temporaryFile;
    private final long byteLength;

    private boolean closed;

    private StoredResponseBody(
            byte[] inMemoryContent,
            Path temporaryFile,
            long byteLength
    ) {
        this.inMemoryContent = inMemoryContent;
        this.temporaryFile = temporaryFile;
        this.byteLength = byteLength;
    }

    public static StoredResponseBody inMemory(
            byte[] content
    ) {
        Objects.requireNonNull(
                content,
                "Response body content is required."
        );

        byte[] protectedContent =
                Arrays.copyOf(content, content.length);

        return new StoredResponseBody(
                protectedContent,
                null,
                protectedContent.length
        );
    }

    public static StoredResponseBody temporaryFile(
            Path temporaryFile
    ) throws IOException {
        Objects.requireNonNull(
                temporaryFile,
                "Temporary response body file is required."
        );

        Path normalizedPath =
                temporaryFile.toAbsolutePath().normalize();

        if (!Files.isRegularFile(normalizedPath)) {
            throw new IllegalArgumentException(
                    "Temporary response body file does not exist: "
                            + normalizedPath
            );
        }

        return new StoredResponseBody(
                null,
                normalizedPath,
                Files.size(normalizedPath)
        );
    }

    public boolean isInMemory() {
        return inMemoryContent != null;
    }

    public boolean isTemporaryFileBacked() {
        return temporaryFile != null;
    }

    public long getByteLength() {
        return byteLength;
    }

    public InputStream openInputStream()
            throws IOException {
        ensureOpen();

        if (isInMemory()) {
            return new ByteArrayInputStream(
                    inMemoryContent
            );
        }

        return Files.newInputStream(
                temporaryFile
        );
    }

    public Reader openReader(
            Charset charset
    ) throws IOException {
        Objects.requireNonNull(
                charset,
                "Response body charset is required."
        );

        return new InputStreamReader(
                openInputStream(),
                charset
        );
    }

    @Override
    public void close() throws IOException {
        if (closed) {
            return;
        }

        closed = true;

        if (temporaryFile != null) {
            Files.deleteIfExists(temporaryFile);
        }
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException(
                    "Stored response body has already been closed."
            );
        }
    }
}