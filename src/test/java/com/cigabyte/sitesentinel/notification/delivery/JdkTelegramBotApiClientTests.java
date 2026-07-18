package com.cigabyte.sitesentinel.notification.delivery;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class JdkTelegramBotApiClientTests {

    private static final String BOT_TOKEN =
            "test-bot-token";

    private HttpServer httpServer;

    @AfterEach
    void stopHttpServer() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
    }

    @Test
    void getMeSendsGetRequestToExpectedTelegramEndpoint()
            throws IOException {

        String expectedPath =
                "/bot" + BOT_TOKEN + "/getMe";

        String responseBody =
                """
                {
                  "ok": true,
                  "result": {
                    "id": 12345,
                    "is_bot": true
                  }
                }
                """;

        AtomicReference<RecordedRequest> recordedRequest =
                new AtomicReference<>();

        String apiBaseUrl =
                startHttpServer(
                        expectedPath,
                        200,
                        responseBody,
                        recordedRequest
                );

        TelegramDeliveryProperties properties =
                telegramProperties(
                        apiBaseUrl + "/"
                );

        JdkTelegramBotApiClient client =
                new JdkTelegramBotApiClient(properties);

        TelegramBotApiResponse response =
                client.getMe();

        RecordedRequest request =
                recordedRequest.get();

        assertNotNull(request);

        assertEquals(
                "GET",
                request.method()
        );

        assertEquals(
                expectedPath,
                request.path()
        );

        assertTrue(
                request.body().isEmpty()
        );

        assertEquals(
                200,
                response.getStatusCode()
        );

        assertEquals(
                responseBody,
                response.getBody()
        );

        assertTrue(
                response.indicatesSuccessfulTelegramResponse()
        );
    }

    @Test
    void sendMessageSendsEncodedFormRequestToExpectedEndpoint()
            throws IOException {

        String expectedPath =
                "/bot" + BOT_TOKEN + "/sendMessage";

        AtomicReference<RecordedRequest> recordedRequest =
                new AtomicReference<>();

        String apiBaseUrl =
                startHttpServer(
                        expectedPath,
                        200,
                        """
                        {
                          "ok": true,
                          "result": {
                            "message_id": 9876
                          }
                        }
                        """,
                        recordedRequest
                );

        TelegramDeliveryProperties properties =
                telegramProperties(apiBaseUrl);

        JdkTelegramBotApiClient client =
                new JdkTelegramBotApiClient(properties);

        String chatId =
                "-100 123+456&789";

        String message =
                """
                SiteSentinel alert: A+B & C/D?
                Second line with spaces.
                """;

        TelegramBotApiResponse response =
                client.sendMessage(
                        chatId,
                        message
                );

        RecordedRequest request =
                recordedRequest.get();

        assertNotNull(request);

        assertEquals(
                "POST",
                request.method()
        );

        assertEquals(
                expectedPath,
                request.path()
        );

        assertNotNull(request.contentType());

        assertTrue(
                request.contentType().startsWith(
                        "application/x-www-form-urlencoded"
                )
        );

        Map<String, String> formValues =
                decodeForm(request.body());

        assertEquals(
                3,
                formValues.size()
        );

        assertEquals(
                chatId,
                formValues.get("chat_id")
        );

        assertEquals(
                message,
                formValues.get("text")
        );

        assertEquals(
                "true",
                formValues.get(
                        "disable_web_page_preview"
                )
        );

        assertEquals(
                200,
                response.getStatusCode()
        );

        assertTrue(
                response.indicatesSuccessfulTelegramResponse()
        );
    }

    @Test
    void sendDocumentSendsMultipartRequestToExpectedEndpoint()
            throws IOException {

        String expectedPath =
                "/bot"
                        + BOT_TOKEN
                        + "/sendDocument";

        AtomicReference<RecordedRequest> recordedRequest =
                new AtomicReference<>();

        String apiBaseUrl =
                startHttpServer(
                        expectedPath,
                        200,
                        """
                        {
                          "ok": true,
                          "result": {
                            "message_id": 2468
                          }
                        }
                        """,
                        recordedRequest
                );

        TelegramDeliveryProperties properties =
                telegramProperties(
                        apiBaseUrl
                );

        JdkTelegramBotApiClient client =
                new JdkTelegramBotApiClient(
                        properties
                );

        TelegramDocumentUploadRequest uploadRequest =
                new TelegramDocumentUploadRequest(
                        "test-chat-id",
                        "monitoring-report.pdf",
                        "application/pdf",
                        "%PDF-test-document"
                                .getBytes(
                                        StandardCharsets.US_ASCII
                                ),
                        "Monitoring run report"
                );

        TelegramBotApiResponse response =
                client.sendDocument(
                        uploadRequest
                );

        RecordedRequest request =
                recordedRequest.get();

        assertNotNull(
                request
        );

        assertEquals(
                "POST",
                request.method()
        );

        assertEquals(
                expectedPath,
                request.path()
        );

        assertNotNull(
                request.contentType()
        );

        assertTrue(
                request.contentType().startsWith(
                        "multipart/form-data; boundary="
                )
        );

        assertTrue(
                request.body().contains(
                        "name=\"chat_id\"\r\n\r\n"
                                + "test-chat-id\r\n"
                )
        );

        assertTrue(
                request.body().contains(
                        "name=\"caption\"\r\n\r\n"
                                + "Monitoring run report\r\n"
                )
        );

        assertTrue(
                request.body().contains(
                        "name=\"document\"; "
                                + "filename=\"monitoring-report.pdf\"\r\n"
                                + "Content-Type: application/pdf\r\n\r\n"
                                + "%PDF-test-document"
                )
        );

        assertEquals(
                200,
                response.getStatusCode()
        );

        assertTrue(
                response
                        .indicatesSuccessfulTelegramResponse()
        );

        assertEquals(
                Long.valueOf(2468L),
                response.getTelegramMessageId()
        );
    }

    @Test
    void getMePreservesUnsuccessfulHttpStatusAndResponseBody()
            throws IOException {

        String expectedPath =
                "/bot" + BOT_TOKEN + "/getMe";

        String responseBody =
                """
                {
                  "ok": false,
                  "error_code": 401,
                  "description": "Unauthorized"
                }
                """;

        AtomicReference<RecordedRequest> recordedRequest =
                new AtomicReference<>();

        String apiBaseUrl =
                startHttpServer(
                        expectedPath,
                        401,
                        responseBody,
                        recordedRequest
                );

        TelegramDeliveryProperties properties =
                telegramProperties(apiBaseUrl);

        JdkTelegramBotApiClient client =
                new JdkTelegramBotApiClient(properties);

        TelegramBotApiResponse response =
                client.getMe();

        RecordedRequest request =
                recordedRequest.get();

        assertNotNull(request);

        assertEquals(
                "GET",
                request.method()
        );

        assertEquals(
                expectedPath,
                request.path()
        );

        assertEquals(
                401,
                response.getStatusCode()
        );

        assertEquals(
                responseBody,
                response.getBody()
        );

        assertTrue(
                !response.hasSuccessfulStatusCode()
        );

        assertTrue(
                !response.indicatesSuccessfulTelegramResponse()
        );
    }

    private TelegramDeliveryProperties telegramProperties(
            String apiBaseUrl
    ) {
        TelegramDeliveryProperties properties =
                new TelegramDeliveryProperties();

        properties.setEnabled(true);
        properties.setBotToken(BOT_TOKEN);
        properties.setChatId("test-chat-id");
        properties.setApiBaseUrl(apiBaseUrl);
        properties.setConnectTimeoutSeconds(2);
        properties.setRequestTimeoutSeconds(2);

        return properties;
    }

    private String startHttpServer(
            String expectedPath,
            int responseStatus,
            String responseBody,
            AtomicReference<RecordedRequest> recordedRequest
    ) throws IOException {

        httpServer =
                HttpServer.create(
                        new InetSocketAddress(
                                "127.0.0.1",
                                0
                        ),
                        0
                );

        httpServer.createContext(
                expectedPath,
                exchange -> handleRequest(
                        exchange,
                        responseStatus,
                        responseBody,
                        recordedRequest
                )
        );

        httpServer.start();

        return "http://127.0.0.1:"
                + httpServer.getAddress().getPort();
    }

    private void handleRequest(
            HttpExchange exchange,
            int responseStatus,
            String responseBody,
            AtomicReference<RecordedRequest> recordedRequest
    ) throws IOException {

        byte[] requestBody =
                exchange.getRequestBody().readAllBytes();

        recordedRequest.set(
                new RecordedRequest(
                        exchange.getRequestMethod(),
                        exchange.getRequestURI().getRawPath(),
                        exchange.getRequestHeaders().getFirst(
                                "Content-Type"
                        ),
                        new String(
                                requestBody,
                                StandardCharsets.UTF_8
                        )
                )
        );

        byte[] responseBytes =
                responseBody.getBytes(
                        StandardCharsets.UTF_8
                );

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json"
        );

        exchange.sendResponseHeaders(
                responseStatus,
                responseBytes.length
        );

        exchange.getResponseBody().write(
                responseBytes
        );

        exchange.close();
    }

    private Map<String, String> decodeForm(
            String requestBody
    ) {
        Map<String, String> values =
                new LinkedHashMap<>();

        for (String pair : requestBody.split("&")) {
            String[] parts =
                    pair.split("=", 2);

            String name =
                    decode(parts[0]);

            String value =
                    parts.length == 2
                            ? decode(parts[1])
                            : "";

            values.put(name, value);
        }

        return values;
    }

    private String decode(String value) {
        return URLDecoder.decode(
                value,
                StandardCharsets.UTF_8
        );
    }

    private record RecordedRequest(
            String method,
            String path,
            String contentType,
            String body
    ) {
    }
}