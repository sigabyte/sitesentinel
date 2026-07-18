package com.cigabyte.sitesentinel.recommendation.openai;

import com.cigabyte.sitesentinel.recommendation
        .RiskRemediationAiOutput;
import com.cigabyte.sitesentinel.recommendation
        .RiskRemediationAiRequest;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdkOpenAiRecommendationApiClientTests {

    private static final JsonMapper JSON_MAPPER =
            JsonMapper.builder().build();

    private static final String API_KEY =
            "test-openai-api-key";

    private static final String EXPECTED_PATH =
            "/v1/responses";

    private HttpServer httpServer;

    @AfterEach
    void stopHttpServer() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
    }

    @Test
    void generateRecommendationSendsExpectedResponsesApiRequest()
            throws IOException, JacksonException {

        AtomicReference<RecordedRequest> recordedRequest =
                new AtomicReference<>();

        String apiBaseUrl =
                startHttpServer(
                        200,
                        successfulResponseBody(),
                        recordedRequest
                );

        JdkOpenAiRecommendationApiClient client =
                createClient(apiBaseUrl);

        OpenAiRecommendationApiResult result =
                client.generateRecommendation(
                        validRequest()
                );

        RecordedRequest request =
                recordedRequest.get();

        assertNotNull(request);

        JsonNode requestBody =
                JSON_MAPPER.readTree(
                        request.body()
                );

        RiskRemediationAiOutput output =
                result.getOutput().orElseThrow();

        assertAll(
                () -> assertEquals(
                        "POST",
                        request.method()
                ),
                () -> assertEquals(
                        EXPECTED_PATH,
                        request.path()
                ),
                () -> assertEquals(
                        "Bearer " + API_KEY,
                        request.authorization()
                ),
                () -> assertTrue(
                        request.contentType()
                                .startsWith(
                                        "application/json"
                                )
                ),
                () -> assertEquals(
                        "application/json",
                        request.accept()
                ),
                () -> assertEquals(
                        "gpt-5.6-terra",
                        requestBody.get("model")
                                .textValue()
                ),
                () -> assertEquals(
                        "Generate one advisory remediation "
                                + "recommendation.",
                        requestBody.get("instructions")
                                .textValue()
                ),
                () -> assertEquals(
                        "Use only the supplied risk context.",
                        requestBody.get("input")
                                .textValue()
                ),
                () -> assertFalse(
                        requestBody.get("store")
                                .booleanValue()
                ),
                () -> assertTrue(
                        result.isSuccessful()
                ),
                () -> assertEquals(
                        OpenAiRecommendationApiStatus.SUCCESS,
                        result.getStatus()
                ),
                () -> assertEquals(
                        200,
                        result.getHttpStatusCode()
                ),
                () -> assertEquals(
                        "1",
                        output.schemaVersion()
                ),
                () -> assertEquals(
                        "Restore monitored service availability",
                        output.title()
                )
        );
    }

    @Test
    void authenticationFailureIsClassifiedWithoutResponseExposure()
            throws IOException {

        OpenAiRecommendationApiResult result =
                executeResponse(
                        401,
                        """
                        {
                          "error": {
                            "message": "Secret provider error detail"
                          }
                        }
                        """
                );

        assertFailureResult(
                result,
                OpenAiRecommendationApiStatus
                        .AUTHENTICATION_FAILED,
                401
        );
    }

    @Test
    void rateLimitResponseIsClassified()
            throws IOException {

        OpenAiRecommendationApiResult result =
                executeResponse(
                        429,
                        """
                        {
                          "error": {
                            "message": "Rate limit exceeded"
                          }
                        }
                        """
                );

        assertFailureResult(
                result,
                OpenAiRecommendationApiStatus.RATE_LIMITED,
                429
        );
    }

    @Test
    void serverFailureIsClassifiedAsProviderUnavailable()
            throws IOException {

        OpenAiRecommendationApiResult result =
                executeResponse(
                        503,
                        """
                        {
                          "error": {
                            "message": "Provider unavailable"
                          }
                        }
                        """
                );

        assertFailureResult(
                result,
                OpenAiRecommendationApiStatus
                        .PROVIDER_UNAVAILABLE,
                503
        );
    }

    @Test
    void otherNonSuccessfulResponseIsClassifiedAsRejected()
            throws IOException {

        OpenAiRecommendationApiResult result =
                executeResponse(
                        400,
                        """
                        {
                          "error": {
                            "message": "Invalid request"
                          }
                        }
                        """
                );

        assertFailureResult(
                result,
                OpenAiRecommendationApiStatus
                        .REQUEST_REJECTED,
                400
        );
    }

    private OpenAiRecommendationApiResult executeResponse(
            int responseStatus,
            String responseBody
    ) throws IOException {

        AtomicReference<RecordedRequest> recordedRequest =
                new AtomicReference<>();

        String apiBaseUrl =
                startHttpServer(
                        responseStatus,
                        responseBody,
                        recordedRequest
                );

        JdkOpenAiRecommendationApiClient client =
                createClient(apiBaseUrl);

        OpenAiRecommendationApiResult result =
                client.generateRecommendation(
                        validRequest()
                );

        assertNotNull(
                recordedRequest.get()
        );

        return result;
    }

    private JdkOpenAiRecommendationApiClient createClient(
            String apiBaseUrl
    ) {
        OpenAiRecommendationProperties properties =
                configuredProperties(
                        apiBaseUrl
                );

        OpenAiRecommendationRequestBodyFactory
                requestBodyFactory =
                new OpenAiRecommendationRequestBodyFactory(
                        properties
                );

        OpenAiRecommendationResponseParser
                responseParser =
                new OpenAiRecommendationResponseParser();

        return new JdkOpenAiRecommendationApiClient(
                properties,
                requestBodyFactory,
                responseParser
        );
    }

    private OpenAiRecommendationProperties
    configuredProperties(
            String apiBaseUrl
    ) {
        OpenAiRecommendationProperties properties =
                new OpenAiRecommendationProperties();

        properties.setEnabled(true);
        properties.setApiKey(API_KEY);
        properties.setApiBaseUrl(
                apiBaseUrl + "/v1"
        );
        properties.setModel(
                "gpt-5.6-terra"
        );
        properties.setConnectTimeoutSeconds(2);
        properties.setRequestTimeoutSeconds(2);
        properties.setMaxOutputTokens(1500);

        return properties;
    }

    private RiskRemediationAiRequest validRequest() {
        return new RiskRemediationAiRequest(
                "prompt-v1",
                "1",
                "Generate one advisory remediation "
                        + "recommendation.",
                "Use only the supplied risk context.",
                "a".repeat(64)
        );
    }

    private String successfulResponseBody()
            throws JacksonException {

        RiskRemediationAiOutput output =
                new RiskRemediationAiOutput(
                        "1",
                        "Restore monitored service availability",
                        "The monitored endpoint is unavailable.",
                        List.of(
                                "Confirm that the application process "
                                        + "is running.",
                                "Review recent deployment changes."
                        ),
                        List.of(
                                "Repeat the SiteSentinel monitoring run.",
                                "Confirm a successful HTTP response."
                        ),
                        true
                );

        String structuredOutput =
                JSON_MAPPER.writeValueAsString(
                        output
                );

        Map<String, Object> contentItem =
                new LinkedHashMap<>();

        contentItem.put(
                "type",
                "output_text"
        );

        contentItem.put(
                "text",
                structuredOutput
        );

        Map<String, Object> messageOutput =
                new LinkedHashMap<>();

        messageOutput.put(
                "type",
                "message"
        );

        messageOutput.put(
                "content",
                List.of(
                        contentItem
                )
        );

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "status",
                "completed"
        );

        response.put(
                "error",
                null
        );

        response.put(
                "output",
                List.of(
                        messageOutput
                )
        );

        return JSON_MAPPER.writeValueAsString(
                response
        );
    }

    private String startHttpServer(
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
                EXPECTED_PATH,
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

        byte[] requestBytes =
                exchange.getRequestBody().readAllBytes();

        recordedRequest.set(
                new RecordedRequest(
                        exchange.getRequestMethod(),
                        exchange.getRequestURI().getRawPath(),
                        exchange.getRequestHeaders().getFirst(
                                "Authorization"
                        ),
                        exchange.getRequestHeaders().getFirst(
                                "Content-Type"
                        ),
                        exchange.getRequestHeaders().getFirst(
                                "Accept"
                        ),
                        new String(
                                requestBytes,
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

    private void assertFailureResult(
            OpenAiRecommendationApiResult result,
            OpenAiRecommendationApiStatus expectedStatus,
            int expectedHttpStatus
    ) {
        assertAll(
                () -> assertFalse(
                        result.isSuccessful()
                ),
                () -> assertEquals(
                        expectedStatus,
                        result.getStatus()
                ),
                () -> assertTrue(
                        result.getOutput().isEmpty()
                ),
                () -> assertEquals(
                        expectedHttpStatus,
                        result.getHttpStatusCode()
                )
        );
    }

    private record RecordedRequest(
            String method,
            String path,
            String authorization,
            String contentType,
            String accept,
            String body
    ) {
    }
}