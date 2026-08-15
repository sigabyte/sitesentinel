package com.cigabyte.sitesentinel.engine.collection;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.http.HttpHeaders;
import java.nio.charset.Charset;
import java.util.Objects;

public final class CollectedHttpResponse implements AutoCloseable {

    private final int statusCode;
    private final URI uri;
    private final HttpHeaders headers;
    private final StoredResponseBody body;

    private boolean closed;

    public CollectedHttpResponse(
            int statusCode,
            URI uri,
            HttpHeaders headers,
            StoredResponseBody body
    ) {
        if (statusCode < 100 || statusCode > 599) {
            throw new IllegalArgumentException(
                    "HTTP status code must be between 100 and 599."
            );
        }

        this.statusCode = statusCode;
        this.uri = Objects.requireNonNull(
                uri,
                "HTTP response URI is required."
        );
        this.headers = Objects.requireNonNull(
                headers,
                "HTTP response headers are required."
        );
        this.body = Objects.requireNonNull(
                body,
                "Stored HTTP response body is required."
        );
    }

    public int getStatusCode() {
        return statusCode;
    }

    public URI getUri() {
        return uri;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public long getBodyByteLength() {
        ensureOpen();
        return body.getByteLength();
    }

    public boolean isBodyInMemory() {
        ensureOpen();
        return body.isInMemory();
    }

    public boolean isBodyTemporaryFileBacked() {
        ensureOpen();
        return body.isTemporaryFileBacked();
    }

    public InputStream openBodyInputStream()
            throws IOException {
        ensureOpen();
        return body.openInputStream();
    }

    public Reader openBodyReader(
            Charset charset
    ) throws IOException {
        ensureOpen();
        return body.openReader(charset);
    }

    @Override
    public void close() throws IOException {
        if (closed) {
            return;
        }

        closed = true;
        body.close();
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException(
                    "Collected HTTP response has already been closed."
            );
        }
    }
}