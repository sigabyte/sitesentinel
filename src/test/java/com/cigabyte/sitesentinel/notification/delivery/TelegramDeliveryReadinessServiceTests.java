package com.cigabyte.sitesentinel.notification.delivery;

import com.cigabyte.sitesentinel.notification.NotificationDeliveryChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TelegramDeliveryReadinessServiceTests {

    private static final String DEFAULT_API_BASE_URL =
            "https://api.telegram.org";

    @Test
    void defaultPropertiesUseSafeDisabledConfiguration() {
        TelegramDeliveryProperties properties =
                new TelegramDeliveryProperties();

        assertFalse(properties.isEnabled());

        assertEquals(
                "",
                properties.getBotToken()
        );

        assertEquals(
                "",
                properties.getChatId()
        );

        assertEquals(
                DEFAULT_API_BASE_URL,
                properties.getApiBaseUrl()
        );

        assertEquals(
                DEFAULT_API_BASE_URL,
                properties.getNormalizedApiBaseUrl()
        );

        assertEquals(
                5L,
                properties.getConnectTimeoutSeconds()
        );

        assertEquals(
                10L,
                properties.getRequestTimeoutSeconds()
        );

        assertFalse(
                properties.hasRequiredConfiguration()
        );
    }

    @Test
    void credentialDestinationAndEndpointSettersTrimValues() {
        TelegramDeliveryProperties properties =
                new TelegramDeliveryProperties();

        properties.setBotToken(
                "  test-bot-token  "
        );

        properties.setChatId(
                "  test-chat-id  "
        );

        properties.setApiBaseUrl(
                "  https://telegram.example.test  "
        );

        assertEquals(
                "test-bot-token",
                properties.getBotToken()
        );

        assertEquals(
                "test-chat-id",
                properties.getChatId()
        );

        assertEquals(
                "https://telegram.example.test",
                properties.getApiBaseUrl()
        );

        assertTrue(
                properties.hasRequiredConfiguration()
        );
    }

    @Test
    void blankApiBaseUrlRestoresDefaultTelegramEndpoint() {
        TelegramDeliveryProperties properties =
                new TelegramDeliveryProperties();

        properties.setApiBaseUrl("   ");

        assertEquals(
                DEFAULT_API_BASE_URL,
                properties.getApiBaseUrl()
        );

        assertEquals(
                DEFAULT_API_BASE_URL,
                properties.getNormalizedApiBaseUrl()
        );
    }

    @Test
    void normalizedApiBaseUrlRemovesTrailingSlash() {
        TelegramDeliveryProperties properties =
                new TelegramDeliveryProperties();

        properties.setApiBaseUrl(
                "https://telegram.example.test/"
        );

        assertEquals(
                "https://telegram.example.test/",
                properties.getApiBaseUrl()
        );

        assertEquals(
                "https://telegram.example.test",
                properties.getNormalizedApiBaseUrl()
        );
    }

    @Test
    void timeoutSettersEnforceMinimumOneSecond() {
        TelegramDeliveryProperties properties =
                new TelegramDeliveryProperties();

        properties.setConnectTimeoutSeconds(0);
        properties.setRequestTimeoutSeconds(-10);

        assertEquals(
                1L,
                properties.getConnectTimeoutSeconds()
        );

        assertEquals(
                1L,
                properties.getRequestTimeoutSeconds()
        );

        properties.setConnectTimeoutSeconds(7);
        properties.setRequestTimeoutSeconds(12);

        assertEquals(
                7L,
                properties.getConnectTimeoutSeconds()
        );

        assertEquals(
                12L,
                properties.getRequestTimeoutSeconds()
        );
    }

    @Test
    void evaluateReturnsDisabledWhenProviderIsDisabled() {
        TelegramDeliveryProperties properties =
                readyTelegramProperties();

        properties.setEnabled(false);

        NotificationDeliveryProviderStatus status =
                evaluate(properties);

        assertEquals(
                NotificationDeliveryChannel.TELEGRAM,
                status.getChannel()
        );

        assertFalse(status.isEnabled());

        assertTrue(
                status.isCredentialConfigured()
        );

        assertTrue(
                status.isDestinationConfigured()
        );

        assertTrue(
                status.isEndpointConfigured()
        );

        assertTrue(
                status.isConfigurationComplete()
        );

        assertFalse(status.isReady());

        assertEquals(
                NotificationDeliveryProviderReadinessStatus.DISABLED,
                status.getReadinessStatus()
        );

        assertEquals(
                NotificationDeliveryMode.MANUAL_ONLY,
                status.getDeliveryMode()
        );

        assertEquals(
                "Telegram delivery provider is disabled.",
                status.getStatusMessage()
        );

        assertSecretSafe(status.getStatusMessage());
    }

    @Test
    void evaluateReturnsConfigurationMissingWhenCredentialIsMissing() {
        TelegramDeliveryProperties properties =
                readyTelegramProperties();

        properties.setBotToken("");

        NotificationDeliveryProviderStatus status =
                evaluate(properties);

        assertTrue(status.isEnabled());

        assertFalse(
                status.isCredentialConfigured()
        );

        assertTrue(
                status.isDestinationConfigured()
        );

        assertTrue(
                status.isEndpointConfigured()
        );

        assertFalse(
                status.isConfigurationComplete()
        );

        assertFalse(status.isReady());

        assertEquals(
                NotificationDeliveryProviderReadinessStatus.CONFIGURATION_MISSING,
                status.getReadinessStatus()
        );

        assertEquals(
                "Telegram delivery provider configuration is incomplete.",
                status.getStatusMessage()
        );

        assertSecretSafe(status.getStatusMessage());
    }

    @Test
    void evaluateReturnsConfigurationMissingWhenDestinationIsMissing() {
        TelegramDeliveryProperties properties =
                readyTelegramProperties();

        properties.setChatId("");

        NotificationDeliveryProviderStatus status =
                evaluate(properties);

        assertTrue(status.isEnabled());

        assertTrue(
                status.isCredentialConfigured()
        );

        assertFalse(
                status.isDestinationConfigured()
        );

        assertTrue(
                status.isEndpointConfigured()
        );

        assertFalse(
                status.isConfigurationComplete()
        );

        assertFalse(status.isReady());

        assertEquals(
                NotificationDeliveryProviderReadinessStatus.CONFIGURATION_MISSING,
                status.getReadinessStatus()
        );

        assertSecretSafe(status.getStatusMessage());
    }

    @Test
    void evaluateReturnsReadyForCompleteEnabledConfiguration() {
        TelegramDeliveryProperties properties =
                readyTelegramProperties();

        NotificationDeliveryProviderStatus status =
                evaluate(properties);

        assertEquals(
                NotificationDeliveryChannel.TELEGRAM,
                status.getChannel()
        );

        assertTrue(status.isEnabled());

        assertTrue(
                status.isCredentialConfigured()
        );

        assertTrue(
                status.isDestinationConfigured()
        );

        assertTrue(
                status.isEndpointConfigured()
        );

        assertTrue(
                status.isConfigurationComplete()
        );

        assertTrue(status.isReady());

        assertEquals(
                NotificationDeliveryProviderReadinessStatus.READY,
                status.getReadinessStatus()
        );

        assertEquals(
                NotificationDeliveryMode.MANUAL_ONLY,
                status.getDeliveryMode()
        );

        assertEquals(
                "Telegram delivery provider configuration is ready.",
                status.getStatusMessage()
        );

        assertSecretSafe(status.getStatusMessage());
    }

    private NotificationDeliveryProviderStatus evaluate(
            TelegramDeliveryProperties properties
    ) {
        TelegramDeliveryReadinessService service =
                new TelegramDeliveryReadinessService(
                        properties
                );

        return service.evaluate();
    }

    private TelegramDeliveryProperties readyTelegramProperties() {
        TelegramDeliveryProperties properties =
                new TelegramDeliveryProperties();

        properties.setEnabled(true);
        properties.setBotToken(
                "test-bot-token"
        );

        properties.setChatId(
                "test-chat-id"
        );

        properties.setApiBaseUrl(
                DEFAULT_API_BASE_URL
        );

        return properties;
    }

    private void assertSecretSafe(String statusMessage) {
        assertFalse(
                statusMessage.contains(
                        "test-bot-token"
                ),
                "Readiness status must not expose the bot token."
        );

        assertFalse(
                statusMessage.contains(
                        "test-chat-id"
                ),
                "Readiness status must not expose the chat ID."
        );

        assertFalse(
                statusMessage.contains(
                        DEFAULT_API_BASE_URL
                ),
                "Readiness status must not expose endpoint details."
        );
    }
}