package com.cigabyte.sitesentinel.recommendation.openai;

import com.cigabyte.sitesentinel.recommendation
        .RiskRemediationAiOutput;
import com.cigabyte.sitesentinel.recommendation
        .RiskRemediationAiProviderResult;
import com.cigabyte.sitesentinel.recommendation
        .RiskRemediationAiProviderStatus;
import com.cigabyte.sitesentinel.recommendation
        .RiskRemediationAiRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class OpenAiRiskRemediationAiProviderTests {

    @Test
    void providerMetadataUsesOpenAiConfiguration() {
        OpenAiRecommendationApiClient apiClient =
                mock(OpenAiRecommendationApiClient.class);

        OpenAiRecommendationProperties properties =
                readyProperties();

        OpenAiRiskRemediationAiProvider provider =
                new OpenAiRiskRemediationAiProvider(
                        properties,
                        apiClient
                );

        assertAll(
                () -> assertEquals(
                        "OpenAI",
                        provider.getProviderName()
                ),
                () -> assertEquals(
                        "gpt-5.6-terra",
                        provider.getModelName()
                ),
                () -> assertTrue(
                        provider.isAvailable()
                )
        );
    }

    @Test
    void disabledProviderReturnsUnavailableWithoutCallingClient() {
        OpenAiRecommendationApiClient apiClient =
                mock(OpenAiRecommendationApiClient.class);

        OpenAiRecommendationProperties properties =
                readyProperties();

        properties.setEnabled(false);

        OpenAiRiskRemediationAiProvider provider =
                new OpenAiRiskRemediationAiProvider(
                        properties,
                        apiClient
                );

        RiskRemediationAiProviderResult result =
                provider.generate(
                        validRequest()
                );

        assertUnavailable(result);

        verifyNoInteractions(
                apiClient
        );
    }

    @Test
    void missingApiKeyReturnsUnavailableWithoutCallingClient() {
        OpenAiRecommendationApiClient apiClient =
                mock(OpenAiRecommendationApiClient.class);

        OpenAiRecommendationProperties properties =
                readyProperties();

        properties.setApiKey(
                "   "
        );

        OpenAiRiskRemediationAiProvider provider =
                new OpenAiRiskRemediationAiProvider(
                        properties,
                        apiClient
                );

        RiskRemediationAiProviderResult result =
                provider.generate(
                        validRequest()
                );

        assertAll(
                () -> assertFalse(
                        provider.isAvailable()
                ),
                () -> assertEquals(
                        RiskRemediationAiProviderStatus.UNAVAILABLE,
                        result.getStatus()
                ),
                () -> assertFalse(
                        result.isSuccessful()
                ),
                () -> assertTrue(
                        result.getOutput().isEmpty()
                )
        );

        verifyNoInteractions(
                apiClient
        );
    }

    @Test
    void successfulApiResultMapsToSuccessfulProviderResult() {
        OpenAiRecommendationApiClient apiClient =
                mock(OpenAiRecommendationApiClient.class);

        RiskRemediationAiOutput expectedOutput =
                validOutput();

        when(
                apiClient.generateRecommendation(
                        any(RiskRemediationAiRequest.class)
                )
        ).thenReturn(
                OpenAiRecommendationApiResult.success(
                        expectedOutput,
                        200
                )
        );

        OpenAiRiskRemediationAiProvider provider =
                new OpenAiRiskRemediationAiProvider(
                        readyProperties(),
                        apiClient
                );

        RiskRemediationAiRequest request =
                validRequest();

        RiskRemediationAiProviderResult result =
                provider.generate(
                        request
                );

        assertAll(
                () -> assertTrue(
                        result.isSuccessful()
                ),
                () -> assertEquals(
                        RiskRemediationAiProviderStatus.SUCCESS,
                        result.getStatus()
                ),
                () -> assertSame(
                        expectedOutput,
                        result.getOutput().orElseThrow()
                )
        );

        verify(apiClient)
                .generateRecommendation(
                        request
                );
    }

    @Test
    void everyUnsuccessfulApiStatusMapsToProviderFailure() {
        OpenAiRecommendationApiClient apiClient =
                mock(OpenAiRecommendationApiClient.class);

        OpenAiRiskRemediationAiProvider provider =
                new OpenAiRiskRemediationAiProvider(
                        readyProperties(),
                        apiClient
                );

        List<OpenAiRecommendationApiResult> failedResults =
                List.of(
                        OpenAiRecommendationApiResult
                                .requestRejected(400),
                        OpenAiRecommendationApiResult
                                .authenticationFailed(401),
                        OpenAiRecommendationApiResult
                                .rateLimited(429),
                        OpenAiRecommendationApiResult
                                .timeout(),
                        OpenAiRecommendationApiResult
                                .providerUnavailable(503),
                        OpenAiRecommendationApiResult
                                .invalidResponse(200),
                        OpenAiRecommendationApiResult
                                .interrupted(),
                        OpenAiRecommendationApiResult
                                .failure()
                );

        for (OpenAiRecommendationApiResult failedResult
                : failedResults) {

            reset(apiClient);

            when(
                    apiClient.generateRecommendation(
                            any(RiskRemediationAiRequest.class)
                    )
            ).thenReturn(
                    failedResult
            );

            RiskRemediationAiProviderResult result =
                    provider.generate(
                            validRequest()
                    );

            assertProviderFailure(result);
        }
    }

    @Test
    void nullApiResultMapsToProviderFailure() {
        OpenAiRecommendationApiClient apiClient =
                mock(OpenAiRecommendationApiClient.class);

        when(
                apiClient.generateRecommendation(
                        any(RiskRemediationAiRequest.class)
                )
        ).thenReturn(
                null
        );

        OpenAiRiskRemediationAiProvider provider =
                new OpenAiRiskRemediationAiProvider(
                        readyProperties(),
                        apiClient
                );

        RiskRemediationAiProviderResult result =
                provider.generate(
                        validRequest()
                );

        assertProviderFailure(result);
    }

    @Test
    void unexpectedClientExceptionMapsToProviderFailure() {
        OpenAiRecommendationApiClient apiClient =
                mock(OpenAiRecommendationApiClient.class);

        when(
                apiClient.generateRecommendation(
                        any(RiskRemediationAiRequest.class)
                )
        ).thenThrow(
                new IllegalStateException(
                        "Simulated provider failure"
                )
        );

        OpenAiRiskRemediationAiProvider provider =
                new OpenAiRiskRemediationAiProvider(
                        readyProperties(),
                        apiClient
                );

        RiskRemediationAiProviderResult result =
                provider.generate(
                        validRequest()
                );

        assertProviderFailure(result);
    }

    @Test
    void nullRequestIsRejectedBeforeClientInvocation() {
        OpenAiRecommendationApiClient apiClient =
                mock(OpenAiRecommendationApiClient.class);

        OpenAiRiskRemediationAiProvider provider =
                new OpenAiRiskRemediationAiProvider(
                        readyProperties(),
                        apiClient
                );

        assertThrows(
                NullPointerException.class,
                () -> provider.generate(
                        null
                )
        );

        verify(
                apiClient,
                never()
        ).generateRecommendation(
                any(RiskRemediationAiRequest.class)
        );
    }

    private OpenAiRecommendationProperties readyProperties() {
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

    private RiskRemediationAiOutput validOutput() {
        return new RiskRemediationAiOutput(
                "1",
                "Restore monitored service availability",
                "The monitored endpoint is unavailable.",
                List.of(
                        "Confirm that the application process is running.",
                        "Review recent deployment and configuration changes."
                ),
                List.of(
                        "Repeat the SiteSentinel monitoring run.",
                        "Confirm that the endpoint returns "
                                + "a successful response."
                ),
                true
        );
    }

    private void assertUnavailable(
            RiskRemediationAiProviderResult result
    ) {
        assertAll(
                () -> assertEquals(
                        RiskRemediationAiProviderStatus.UNAVAILABLE,
                        result.getStatus()
                ),
                () -> assertFalse(
                        result.isSuccessful()
                ),
                () -> assertTrue(
                        result.getOutput().isEmpty()
                )
        );
    }

    private void assertProviderFailure(
            RiskRemediationAiProviderResult result
    ) {
        assertAll(
                () -> assertEquals(
                        RiskRemediationAiProviderStatus.FAILURE,
                        result.getStatus()
                ),
                () -> assertFalse(
                        result.isSuccessful()
                ),
                () -> assertTrue(
                        result.getOutput().isEmpty()
                )
        );
    }
}