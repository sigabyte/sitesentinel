package com.cigabyte.sitesentinel.recommendation.openai;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenAiRecommendationPropertiesTests {

    @Test
    void providerIsDisabledAndNotReadyByDefault() {
        OpenAiRecommendationProperties properties =
                new OpenAiRecommendationProperties();

        assertAll(
                () -> assertFalse(
                        properties.isEnabled()
                ),
                () -> assertFalse(
                        properties.hasRequiredConfiguration()
                ),
                () -> assertFalse(
                        properties.isReady()
                ),
                () -> assertEquals(
                        "https://api.openai.com/v1",
                        properties.getApiBaseUrl()
                ),
                () -> assertEquals(
                        "gpt-5.6-terra",
                        properties.getModel()
                ),
                () -> assertEquals(
                        10,
                        properties.getConnectTimeoutSeconds()
                ),
                () -> assertEquals(
                        60,
                        properties.getRequestTimeoutSeconds()
                ),
                () -> assertEquals(
                        2000,
                        properties.getMaxOutputTokens()
                )
        );
    }

    @Test
    void providerBecomesReadyWhenExplicitlyEnabledAndConfigured() {
        OpenAiRecommendationProperties properties =
                new OpenAiRecommendationProperties();

        properties.setEnabled(true);
        properties.setApiKey(
                "  test-api-key  "
        );

        assertAll(
                () -> assertTrue(
                        properties.isEnabled()
                ),
                () -> assertEquals(
                        "test-api-key",
                        properties.getApiKey()
                ),
                () -> assertTrue(
                        properties.hasRequiredConfiguration()
                ),
                () -> assertTrue(
                        properties.isReady()
                )
        );
    }

    @Test
    void providerRemainsNotReadyWhenDisabled() {
        OpenAiRecommendationProperties properties =
                new OpenAiRecommendationProperties();

        properties.setApiKey(
                "test-api-key"
        );

        assertAll(
                () -> assertTrue(
                        properties.hasRequiredConfiguration()
                ),
                () -> assertFalse(
                        properties.isReady()
                )
        );
    }

    @Test
    void blankApiKeyPreventsProviderReadiness() {
        OpenAiRecommendationProperties properties =
                new OpenAiRecommendationProperties();

        properties.setEnabled(true);
        properties.setApiKey(
                "   "
        );

        assertAll(
                () -> assertEquals(
                        "",
                        properties.getApiKey()
                ),
                () -> assertFalse(
                        properties.hasRequiredConfiguration()
                ),
                () -> assertFalse(
                        properties.isReady()
                )
        );
    }

    @Test
    void blankApiBaseUrlAndModelRestoreDefaults() {
        OpenAiRecommendationProperties properties =
                new OpenAiRecommendationProperties();

        properties.setApiBaseUrl(
                "   "
        );

        properties.setModel(
                null
        );

        assertAll(
                () -> assertEquals(
                        "https://api.openai.com/v1",
                        properties.getApiBaseUrl()
                ),
                () -> assertEquals(
                        "gpt-5.6-terra",
                        properties.getModel()
                )
        );
    }

    @Test
    void apiBaseUrlIsTrimmedAndNormalized() {
        OpenAiRecommendationProperties properties =
                new OpenAiRecommendationProperties();

        properties.setApiBaseUrl(
                "  https://openai.example.test/v1/  "
        );

        assertAll(
                () -> assertEquals(
                        "https://openai.example.test/v1/",
                        properties.getApiBaseUrl()
                ),
                () -> assertEquals(
                        "https://openai.example.test/v1",
                        properties.getNormalizedApiBaseUrl()
                )
        );
    }

    @Test
    void timeoutValuesAreClampedToMinimum() {
        OpenAiRecommendationProperties properties =
                new OpenAiRecommendationProperties();

        properties.setConnectTimeoutSeconds(
                0
        );

        properties.setRequestTimeoutSeconds(
                -10
        );

        assertAll(
                () -> assertEquals(
                        1,
                        properties.getConnectTimeoutSeconds()
                ),
                () -> assertEquals(
                        1,
                        properties.getRequestTimeoutSeconds()
                )
        );
    }

    @Test
    void maxOutputTokensAreClampedToMinimum() {
        OpenAiRecommendationProperties properties =
                new OpenAiRecommendationProperties();

        properties.setMaxOutputTokens(
                100
        );

        assertEquals(
                256,
                properties.getMaxOutputTokens()
        );
    }
}