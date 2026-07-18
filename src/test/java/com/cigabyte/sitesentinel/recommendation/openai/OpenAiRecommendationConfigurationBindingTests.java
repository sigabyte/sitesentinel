package com.cigabyte.sitesentinel.recommendation.openai;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenAiRecommendationConfigurationBindingTests {

    private static final String OPENAI_PROPERTY_PREFIX =
            "sitesentinel.recommendation.ai.openai";

    @Test
    void applicationPropertiesBindSafeOpenAiDefaults()
            throws IOException {

        StandardEnvironment environment =
                applicationPropertiesOnlyEnvironment();

        OpenAiRecommendationProperties properties =
                bindOpenAiProperties(environment);

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
                        "",
                        properties.getApiKey()
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
    void environmentOverridesBindReadyOpenAiConfiguration()
            throws IOException {

        StandardEnvironment environment =
                applicationPropertiesOnlyEnvironment();

        Map<String, Object> environmentOverrides =
                Map.<String, Object>of(
                        "SITESENTINEL_OPENAI_ENABLED",
                        "true",
                        "SITESENTINEL_OPENAI_API_KEY",
                        "test-openai-api-key",
                        "SITESENTINEL_OPENAI_API_BASE_URL",
                        "https://openai.example.test/v1/",
                        "SITESENTINEL_OPENAI_MODEL",
                        "test-openai-model",
                        "SITESENTINEL_OPENAI_CONNECT_TIMEOUT_SECONDS",
                        "15",
                        "SITESENTINEL_OPENAI_REQUEST_TIMEOUT_SECONDS",
                        "90",
                        "SITESENTINEL_OPENAI_MAX_OUTPUT_TOKENS",
                        "2500"
                );

        environment.getPropertySources().addFirst(
                new MapPropertySource(
                        "openAiEnvironmentOverrides",
                        environmentOverrides
                )
        );

        OpenAiRecommendationProperties properties =
                bindOpenAiProperties(environment);

        assertAll(
                () -> assertTrue(
                        properties.isEnabled()
                ),
                () -> assertTrue(
                        properties.hasRequiredConfiguration()
                ),
                () -> assertTrue(
                        properties.isReady()
                ),
                () -> assertEquals(
                        "test-openai-api-key",
                        properties.getApiKey()
                ),
                () -> assertEquals(
                        "https://openai.example.test/v1/",
                        properties.getApiBaseUrl()
                ),
                () -> assertEquals(
                        "https://openai.example.test/v1",
                        properties.getNormalizedApiBaseUrl()
                ),
                () -> assertEquals(
                        "test-openai-model",
                        properties.getModel()
                ),
                () -> assertEquals(
                        15,
                        properties.getConnectTimeoutSeconds()
                ),
                () -> assertEquals(
                        90,
                        properties.getRequestTimeoutSeconds()
                ),
                () -> assertEquals(
                        2500,
                        properties.getMaxOutputTokens()
                )
        );
    }

    private StandardEnvironment
    applicationPropertiesOnlyEnvironment()
            throws IOException {

        StandardEnvironment environment =
                new StandardEnvironment();

        environment.getPropertySources().remove(
                StandardEnvironment
                        .SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME
        );

        environment.getPropertySources().remove(
                StandardEnvironment
                        .SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME
        );

        environment.getPropertySources().addLast(
                new ResourcePropertySource(
                        new ClassPathResource(
                                "application.properties"
                        )
                )
        );

        return environment;
    }

    private OpenAiRecommendationProperties bindOpenAiProperties(
            StandardEnvironment environment
    ) {
        return Binder.get(environment)
                .bind(
                        OPENAI_PROPERTY_PREFIX,
                        Bindable.of(
                                OpenAiRecommendationProperties.class
                        )
                )
                .get();
    }
}