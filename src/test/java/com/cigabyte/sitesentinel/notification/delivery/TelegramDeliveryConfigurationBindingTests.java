package com.cigabyte.sitesentinel.notification.delivery;

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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TelegramDeliveryConfigurationBindingTests {

    private static final String TELEGRAM_PROPERTY_PREFIX =
            "sitesentinel.notification.delivery.telegram";

    @Test
    void applicationPropertiesBindTelegramSafetyFlagsToDisabledByDefault()
            throws IOException {

        StandardEnvironment environment =
                applicationPropertiesOnlyEnvironment();

        TelegramDeliveryProperties properties =
                bindTelegramProperties(environment);

        assertAll(
                () -> assertFalse(
                        properties.isEnabled()
                ),
                () -> assertFalse(
                        properties
                                .isAutomaticPdfDispatchEnabled()
                )
        );
    }

    @Test
    void environmentOverridesBindTelegramSafetyFlagsToEnabled()
            throws IOException {

        StandardEnvironment environment =
                applicationPropertiesOnlyEnvironment();

        Map<String, Object> environmentOverrides =
                Map.<String, Object>of(
                        "SITESENTINEL_TELEGRAM_ENABLED",
                        "true",
                        "SITESENTINEL_TELEGRAM_AUTOMATIC_PDF_DISPATCH_ENABLED",
                        "true"
                );

        environment.getPropertySources().addFirst(
                new MapPropertySource(
                        "telegramEnvironmentOverrides",
                        environmentOverrides
                )
        );

        TelegramDeliveryProperties properties =
                bindTelegramProperties(environment);

        assertAll(
                () -> assertTrue(
                        properties.isEnabled()
                ),
                () -> assertTrue(
                        properties
                                .isAutomaticPdfDispatchEnabled()
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

    private TelegramDeliveryProperties bindTelegramProperties(
            StandardEnvironment environment
    ) {

        return Binder.get(environment)
                .bind(
                        TELEGRAM_PROPERTY_PREFIX,
                        Bindable.of(
                                TelegramDeliveryProperties.class
                        )
                )
                .get();
    }
}