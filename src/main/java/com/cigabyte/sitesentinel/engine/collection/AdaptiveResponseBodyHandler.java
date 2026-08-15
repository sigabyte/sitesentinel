package com.cigabyte.sitesentinel.engine.collection;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.http.HttpResponse;

@Component
public class AdaptiveResponseBodyHandler
        implements HttpResponse.BodyHandler<StoredResponseBody> {

    private final AdaptiveResponseBodyCollector
            responseBodyCollector;

    public AdaptiveResponseBodyHandler(
            AdaptiveResponseBodyCollector responseBodyCollector
    ) {
        this.responseBodyCollector =
                responseBodyCollector;
    }

    @Override
    public HttpResponse.BodySubscriber<StoredResponseBody> apply(
            HttpResponse.ResponseInfo responseInfo
    ) {
        return HttpResponse.BodySubscribers.mapping(
                HttpResponse.BodySubscribers.ofInputStream(),
                this::collectResponseBody
        );
    }

    private StoredResponseBody collectResponseBody(
            InputStream responseStream
    ) {
        try {
            return responseBodyCollector.collect(
                    responseStream
            );
        } catch (IOException exception) {
            throw new UncheckedIOException(
                    "Failed to collect HTTP response body.",
                    exception
            );
        }
    }
}