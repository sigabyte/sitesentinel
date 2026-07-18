package com.cigabyte.sitesentinel.notification.delivery;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TelegramDeliveryPropertiesTests {

    @Test
    void automaticPdfDispatchIsDisabledByDefault() {
        TelegramDeliveryProperties properties =
                new TelegramDeliveryProperties();

        assertFalse(
                properties
                        .isAutomaticPdfDispatchEnabled()
        );
    }

    @Test
    void automaticPdfDispatchRequiresExplicitEnablement() {
        TelegramDeliveryProperties properties =
                new TelegramDeliveryProperties();

        properties.setAutomaticPdfDispatchEnabled(
                true
        );

        assertTrue(
                properties
                        .isAutomaticPdfDispatchEnabled()
        );
    }
}