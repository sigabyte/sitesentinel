package com.cigabyte.sitesentinel.engine.collection;

import com.cigabyte.sitesentinel.scanner.ScannerProperties;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class AdaptiveResponseBodyHandlerTests {

    private static final int IN_MEMORY_THRESHOLD_BYTES =
            64 * 1024;

    @TempDir
    Path temporaryDirectory;

    private HttpServer httpServer;

    private ExecutorService httpServerExecutor;

    private ExecutorService httpClientExecutor;

    @AfterEach
    void stopResources() {
        if (httpServer != null) {
            httpServer.stop(0);
            httpServer = null;
        }

        shutdownExecutor(
                httpServerExecutor,
                "HTTP server executor"
        );

        httpServerExecutor = null;

        shutdownExecutor(
                httpClientExecutor,
                "HTTP client executor"
        );

        httpClientExecutor = null;
    }

    @Test
    void smallHttpResponseIsCollectedInMemory()
            throws Exception {

        byte[] expectedContent =
                createPayload(1024);

        URI endpoint =
                startServer(expectedContent);

        HttpResponse<StoredResponseBody> response =
                send(
                        endpoint,
                        createBodyHandler()
                );

        assertEquals(
                200,
                response.statusCode()
        );

        try (StoredResponseBody responseBody =
                     response.body()) {

            assertTrue(
                    responseBody.isInMemory()
            );

            assertFalse(
                    responseBody
                            .isTemporaryFileBacked()
            );

            assertEquals(
                    expectedContent.length,
                    responseBody.getByteLength()
            );

            try (InputStream inputStream =
                         responseBody.openInputStream()) {

                assertArrayEquals(
                        expectedContent,
                        inputStream.readAllBytes()
                );
            }
        }

        assertEquals(
                0,
                countTemporaryFiles()
        );
    }

    @Test
    void largeHttpResponseSpillsToFileWithoutTruncation()
            throws Exception {

        byte[] expectedContent =
                createPayload(
                        IN_MEMORY_THRESHOLD_BYTES
                                * 3
                                + 417
                );

        URI endpoint =
                startServer(expectedContent);

        HttpResponse<StoredResponseBody> response =
                send(
                        endpoint,
                        createBodyHandler()
                );

        assertEquals(
                200,
                response.statusCode()
        );

        try (StoredResponseBody responseBody =
                     response.body()) {

            assertFalse(
                    responseBody.isInMemory()
            );

            assertTrue(
                    responseBody
                            .isTemporaryFileBacked()
            );

            assertEquals(
                    expectedContent.length,
                    responseBody.getByteLength()
            );

            assertEquals(
                    1,
                    countTemporaryFiles()
            );

            try (InputStream inputStream =
                         responseBody.openInputStream()) {

                assertArrayEquals(
                        expectedContent,
                        inputStream.readAllBytes()
                );
            }
        }

        assertEquals(
                0,
                countTemporaryFiles()
        );
    }

    @Test
    void collectorFailureIsTranslatedToHttpClientIOException()
            throws Exception {

        byte[] responseContent =
                createPayload(1024);

        URI endpoint =
                startServer(responseContent);

        IOException exception =
                assertThrows(
                        IOException.class,
                        () -> send(
                                endpoint,
                                createFailingBodyHandler()
                        )
                );

        assertTrue(
                containsMessage(
                        exception,
                        "Failed to collect HTTP response body."
                )
        );

        assertEquals(
                0,
                countTemporaryFiles()
        );
    }

    private AdaptiveResponseBodyHandler createBodyHandler() {
        ScannerProperties scannerProperties =
                createScannerProperties();

        AdaptiveResponseBodyCollector collector =
                new AdaptiveResponseBodyCollector(
                        scannerProperties
                );

        return new AdaptiveResponseBodyHandler(
                collector
        );
    }

    private AdaptiveResponseBodyHandler
    createFailingBodyHandler() {

        ScannerProperties scannerProperties =
                createScannerProperties();

        AdaptiveResponseBodyCollector collector =
                new AdaptiveResponseBodyCollector(
                        scannerProperties
                ) {
                    @Override
                    public StoredResponseBody collect(
                            InputStream responseStream
                    ) throws IOException {

                        try (responseStream) {
                            throw new IOException(
                                    "Simulated collector failure."
                            );
                        }
                    }
                };

        return new AdaptiveResponseBodyHandler(
                collector
        );
    }

    private ScannerProperties
    createScannerProperties() {

        ScannerProperties scannerProperties =
                new ScannerProperties();

        scannerProperties
                .setInMemoryBodyThresholdBytes(
                        IN_MEMORY_THRESHOLD_BYTES
                );

        scannerProperties.setTemporaryDirectory(
                temporaryDirectory.toString()
        );

        return scannerProperties;
    }

    private HttpResponse<StoredResponseBody> send(
            URI endpoint,
            AdaptiveResponseBodyHandler bodyHandler
    ) throws Exception {

        httpClientExecutor =
                Executors.newFixedThreadPool(2);

        HttpClient httpClient =
                HttpClient.newBuilder()
                        .executor(
                                httpClientExecutor
                        )
                        .connectTimeout(
                                Duration.ofSeconds(5)
                        )
                        .build();

        HttpRequest request =
                HttpRequest.newBuilder(endpoint)
                        .timeout(
                                Duration.ofSeconds(5)
                        )
                        .GET()
                        .build();

        return httpClient.send(
                request,
                bodyHandler
        );
    }

    private URI startServer(
            byte[] responseContent
    ) throws IOException {

        httpServer =
                HttpServer.create(
                        new InetSocketAddress(
                                "127.0.0.1",
                                0
                        ),
                        0
                );

        httpServerExecutor =
                Executors.newSingleThreadExecutor();

        httpServer.setExecutor(
                httpServerExecutor
        );

        httpServer.createContext(
                "/response",
                exchange -> writeResponse(
                        exchange,
                        responseContent
                )
        );

        httpServer.start();

        return URI.create(
                "http://127.0.0.1:"
                        + httpServer
                        .getAddress()
                        .getPort()
                        + "/response"
        );
    }

    private void writeResponse(
            HttpExchange exchange,
            byte[] responseContent
    ) throws IOException {

        exchange.getResponseHeaders().add(
                "Content-Type",
                "application/octet-stream"
        );

        exchange.sendResponseHeaders(
                200,
                responseContent.length
        );

        try (OutputStream responseBody =
                     exchange.getResponseBody()) {

            responseBody.write(
                    responseContent
            );
        } finally {
            exchange.close();
        }
    }

    private byte[] createPayload(
            int byteLength
    ) {
        byte[] payload =
                new byte[byteLength];

        for (int index = 0;
             index < payload.length;
             index++) {

            payload[index] =
                    (byte) (index % 251);
        }

        return payload;
    }

    private long countTemporaryFiles()
            throws IOException {

        try (Stream<Path> files =
                     Files.list(
                             temporaryDirectory
                     )) {

            return files
                    .filter(Files::isRegularFile)
                    .count();
        }
    }

    private void shutdownExecutor(
            ExecutorService executorService,
            String executorName
    ) {
        if (executorService == null) {
            return;
        }

        executorService.shutdownNow();

        try {
            boolean terminated =
                    executorService.awaitTermination(
                            5,
                            TimeUnit.SECONDS
                    );

            if (!terminated) {
                throw new IllegalStateException(
                        executorName
                                + " did not terminate within 5 seconds."
                );
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Interrupted while stopping "
                            + executorName
                            + ".",
                    exception
            );
        }
    }

    private boolean containsMessage(
            Throwable throwable,
            String expectedMessageFragment
    ) {
        Throwable current =
                throwable;

        while (current != null) {
            String message =
                    current.getMessage();

            if (message != null
                    && message.contains(
                    expectedMessageFragment
            )) {

                return true;
            }

            current =
                    current.getCause();
        }

        return false;
    }
}