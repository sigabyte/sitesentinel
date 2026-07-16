package com.cigabyte.sitesentinel.notification.delivery;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class JdkTelegramBotApiClientFailureTests {

    private static final String BOT_TOKEN =
            "test-bot-token";

    private HttpServer httpServer;

    private ExecutorService httpServerExecutor;

    private CountDownLatch releaseResponse;

    @AfterEach
    void stopHttpServer() {
        if (releaseResponse != null) {
            releaseResponse.countDown();
        }

        if (httpServer != null) {
            httpServer.stop(0);
        }

        if (httpServerExecutor != null) {
            httpServerExecutor.shutdownNow();
        }
    }

    @Test
    void getMeWrapsRequestTimeoutAndPreservesTimeoutCause()
            throws IOException {

        CountDownLatch requestArrived =
                new CountDownLatch(1);

        releaseResponse =
                new CountDownLatch(1);

        String apiBaseUrl =
                startBlockingHttpServer(
                        requestArrived,
                        releaseResponse
                );

        TelegramDeliveryProperties properties =
                telegramProperties(
                        apiBaseUrl,
                        1
                );

        JdkTelegramBotApiClient client =
                new JdkTelegramBotApiClient(properties);

        TelegramBotApiClientException exception;

        try {
            client.getMe();

            fail(
                    "A request timeout should produce "
                            + "TelegramBotApiClientException."
            );

            return;

        } catch (TelegramBotApiClientException caughtException) {
            exception = caughtException;
        }

        assertTrue(
                requestArrived.getCount() == 0,
                "The local HTTP server should receive the request."
        );

        assertEquals(
                "Telegram Bot API request failed.",
                exception.getMessage()
        );

        assertInstanceOf(
                HttpTimeoutException.class,
                exception.getCause()
        );

        assertSafeExceptionMessage(
                exception.getMessage(),
                apiBaseUrl
        );
    }

    @Test
    void getMeRestoresInterruptStatusWhenCallingThreadIsInterrupted()
            throws Exception {

        CountDownLatch requestArrived =
                new CountDownLatch(1);

        releaseResponse =
                new CountDownLatch(1);

        String apiBaseUrl =
                startBlockingHttpServer(
                        requestArrived,
                        releaseResponse
                );

        TelegramDeliveryProperties properties =
                telegramProperties(
                        apiBaseUrl,
                        10
                );

        JdkTelegramBotApiClient client =
                new JdkTelegramBotApiClient(properties);

        AtomicReference<TelegramBotApiClientException> exceptionReference =
                new AtomicReference<>();

        AtomicReference<Throwable> unexpectedFailureReference =
                new AtomicReference<>();

        AtomicBoolean interruptStatusRestored =
                new AtomicBoolean(false);

        Thread workerThread =
                new Thread(
                        () -> {
                            try {
                                client.getMe();

                            } catch (TelegramBotApiClientException exception) {
                                exceptionReference.set(exception);

                                interruptStatusRestored.set(
                                        Thread.currentThread().isInterrupted()
                                );

                            } catch (Throwable throwable) {
                                unexpectedFailureReference.set(throwable);
                            }
                        },
                        "telegram-client-interruption-test"
                );

        workerThread.start();

        try {
            assertTrue(
                    requestArrived.await(
                            2,
                            TimeUnit.SECONDS
                    ),
                    "The local HTTP server did not receive the request."
            );

            workerThread.interrupt();

            workerThread.join(
                    TimeUnit.SECONDS.toMillis(2)
            );

            assertFalse(
                    workerThread.isAlive(),
                    "The interrupted client thread should terminate."
            );

            if (unexpectedFailureReference.get() != null) {
                throw new AssertionError(
                        "Unexpected failure in client thread.",
                        unexpectedFailureReference.get()
                );
            }

            TelegramBotApiClientException exception =
                    exceptionReference.get();

            assertNotNull(
                    exception,
                    "The interrupted call should throw "
                            + "TelegramBotApiClientException."
            );

            assertEquals(
                    "Telegram Bot API request was interrupted.",
                    exception.getMessage()
            );

            assertInstanceOf(
                    InterruptedException.class,
                    exception.getCause()
            );

            assertTrue(
                    interruptStatusRestored.get(),
                    "JdkTelegramBotApiClient must restore "
                            + "the thread interrupt status."
            );

            assertSafeExceptionMessage(
                    exception.getMessage(),
                    apiBaseUrl
            );

        } finally {
            if (workerThread.isAlive()) {
                workerThread.interrupt();

                releaseResponse.countDown();

                workerThread.join(
                        TimeUnit.SECONDS.toMillis(2)
                );
            }
        }
    }

    private TelegramDeliveryProperties telegramProperties(
            String apiBaseUrl,
            long requestTimeoutSeconds
    ) {
        TelegramDeliveryProperties properties =
                new TelegramDeliveryProperties();

        properties.setEnabled(true);
        properties.setBotToken(BOT_TOKEN);
        properties.setChatId("test-chat-id");
        properties.setApiBaseUrl(apiBaseUrl);
        properties.setConnectTimeoutSeconds(2);
        properties.setRequestTimeoutSeconds(
                requestTimeoutSeconds
        );

        return properties;
    }

    private String startBlockingHttpServer(
            CountDownLatch requestArrived,
            CountDownLatch responseRelease
    ) throws IOException {

        String expectedPath =
                "/bot" + BOT_TOKEN + "/getMe";

        httpServer =
                HttpServer.create(
                        new InetSocketAddress(
                                "127.0.0.1",
                                0
                        ),
                        0
                );

        httpServerExecutor =
                Executors.newCachedThreadPool(
                        runnable -> {
                            Thread thread =
                                    new Thread(
                                            runnable,
                                            "telegram-test-http-server"
                                    );

                            thread.setDaemon(true);

                            return thread;
                        }
                );

        httpServer.setExecutor(httpServerExecutor);

        httpServer.createContext(
                expectedPath,
                exchange -> handleBlockingRequest(
                        exchange,
                        requestArrived,
                        responseRelease
                )
        );

        httpServer.start();

        return "http://127.0.0.1:"
                + httpServer.getAddress().getPort();
    }

    private void handleBlockingRequest(
            HttpExchange exchange,
            CountDownLatch requestArrived,
            CountDownLatch responseRelease
    ) {
        try {
            exchange.getRequestBody().readAllBytes();

            requestArrived.countDown();

            boolean released =
                    responseRelease.await(
                            5,
                            TimeUnit.SECONDS
                    );

            if (!released) {
                return;
            }

            byte[] responseBody =
                    """
                    {
                      "ok": true,
                      "result": {
                        "id": 12345
                      }
                    }
                    """.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set(
                    "Content-Type",
                    "application/json"
            );

            exchange.sendResponseHeaders(
                    200,
                    responseBody.length
            );

            exchange.getResponseBody().write(
                    responseBody
            );

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

        } catch (IOException ignored) {
            // The client may close the connection after timeout
            // or interruption. That is expected in these tests.

        } finally {
            exchange.close();
        }
    }

    private void assertSafeExceptionMessage(
            String message,
            String apiBaseUrl
    ) {
        assertFalse(
                message.contains(BOT_TOKEN),
                "Exception message must not expose the bot token."
        );

        assertFalse(
                message.contains(apiBaseUrl),
                "Exception message must not expose the API base URL."
        );

        assertFalse(
                message.contains("test-chat-id"),
                "Exception message must not expose the chat ID."
        );
    }
}