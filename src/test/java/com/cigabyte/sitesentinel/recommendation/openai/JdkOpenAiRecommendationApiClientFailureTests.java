package com.cigabyte.sitesentinel.recommendation.openai;

import com.cigabyte.sitesentinel.recommendation
        .RiskRemediationAiRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class JdkOpenAiRecommendationApiClientFailureTests {

    @AfterEach
    void clearThreadInterruptFlag() {
        Thread.interrupted();
    }

    @Test
    void timeoutIsReturnedAsTypedResult()
            throws IOException, InterruptedException {

        HttpClient httpClient =
                mock(HttpClient.class);

        when(
                httpClient.send(
                        any(HttpRequest.class),
                        org.mockito.ArgumentMatchers
                                .<HttpResponse.BodyHandler<String>>any()
                )
        ).thenThrow(
                new HttpTimeoutException(
                        "Simulated timeout"
                )
        );

        JdkOpenAiRecommendationApiClient client =
                createClient(httpClient);

        OpenAiRecommendationApiResult result =
                client.generateRecommendation(
                        validRequest()
                );

        assertAll(
                () -> assertFalse(
                        result.isSuccessful()
                ),
                () -> assertEquals(
                        OpenAiRecommendationApiStatus.TIMEOUT,
                        result.getStatus()
                ),
                () -> assertTrue(
                        result.getOutput().isEmpty()
                ),
                () -> assertEquals(
                        null,
                        result.getHttpStatusCode()
                )
        );
    }

    @Test
    void interruptionIsReturnedAndThreadFlagIsRestored()
            throws IOException, InterruptedException {

        HttpClient httpClient =
                mock(HttpClient.class);

        when(
                httpClient.send(
                        any(HttpRequest.class),
                        org.mockito.ArgumentMatchers
                                .<HttpResponse.BodyHandler<String>>any()
                )
        ).thenThrow(
                new InterruptedException(
                        "Simulated interruption"
                )
        );

        JdkOpenAiRecommendationApiClient client =
                createClient(httpClient);

        OpenAiRecommendationApiResult result =
                client.generateRecommendation(
                        validRequest()
                );

        assertAll(
                () -> assertFalse(
                        result.isSuccessful()
                ),
                () -> assertEquals(
                        OpenAiRecommendationApiStatus.INTERRUPTED,
                        result.getStatus()
                ),
                () -> assertTrue(
                        result.getOutput().isEmpty()
                ),
                () -> assertTrue(
                        Thread.currentThread().isInterrupted()
                )
        );
    }

    @Test
    void networkFailureIsReturnedAsGenericFailure()
            throws IOException, InterruptedException {

        HttpClient httpClient =
                mock(HttpClient.class);

        when(
                httpClient.send(
                        any(HttpRequest.class),
                        org.mockito.ArgumentMatchers
                                .<HttpResponse.BodyHandler<String>>any()
                )
        ).thenThrow(
                new IOException(
                        "Simulated network failure"
                )
        );

        JdkOpenAiRecommendationApiClient client =
                createClient(httpClient);

        OpenAiRecommendationApiResult result =
                client.generateRecommendation(
                        validRequest()
                );

        assertAll(
                () -> assertFalse(
                        result.isSuccessful()
                ),
                () -> assertEquals(
                        OpenAiRecommendationApiStatus.FAILURE,
                        result.getStatus()
                ),
                () -> assertTrue(
                        result.getOutput().isEmpty()
                ),
                () -> assertEquals(
                        null,
                        result.getHttpStatusCode()
                )
        );
    }

    @Test
    void nullRequestIsRejectedBeforeHttpTransport() {
        HttpClient httpClient =
                mock(HttpClient.class);

        JdkOpenAiRecommendationApiClient client =
                createClient(httpClient);

        assertThrows(
                NullPointerException.class,
                () -> client.generateRecommendation(
                        null
                )
        );

        verifyNoInteractions(
                httpClient
        );
    }

    private JdkOpenAiRecommendationApiClient createClient(
            HttpClient httpClient
    ) {
        OpenAiRecommendationProperties properties =
                configuredProperties();

        OpenAiRecommendationRequestBodyFactory
                requestBodyFactory =
                new OpenAiRecommendationRequestBodyFactory(
                        properties
                );

        OpenAiRecommendationResponseParser responseParser =
                new OpenAiRecommendationResponseParser();

        return new JdkOpenAiRecommendationApiClient(
                properties,
                requestBodyFactory,
                responseParser,
                httpClient
        );
    }

    private OpenAiRecommendationProperties
    configuredProperties() {

        OpenAiRecommendationProperties properties =
                new OpenAiRecommendationProperties();

        properties.setEnabled(true);
        properties.setApiKey(
                "test-openai-api-key"
        );
        properties.setApiBaseUrl(
                "https://api.openai.example.test/v1"
        );
        properties.setModel(
                "gpt-5.6-terra"
        );
        properties.setConnectTimeoutSeconds(
                2
        );
        properties.setRequestTimeoutSeconds(
                2
        );
        properties.setMaxOutputTokens(
                1500
        );

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
}