package com.cigabyte.sitesentinel.notification.delivery;

import org.springframework.stereotype.Service;
import com.cigabyte.sitesentinel.notification.NotificationDeliveryChannel;

@Service
public class TelegramDeliveryReadinessService {

    private final TelegramDeliveryProperties properties;

    public TelegramDeliveryReadinessService(
            TelegramDeliveryProperties properties
    ) {
        this.properties = properties;
    }

    private boolean hasCredential() {
        return hasText(properties.getBotToken());
    }

    private boolean hasDestination() {
        return hasText(properties.getChatId());
    }

    private boolean hasEndpoint() {
        return hasText(properties.getApiBaseUrl());
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    public NotificationDeliveryProviderStatus evaluate() {

        boolean enabled = properties.isEnabled();

        boolean credentialConfigured = hasCredential();

        boolean destinationConfigured = hasDestination();

        boolean endpointConfigured = hasEndpoint();

        boolean configurationComplete =
                credentialConfigured
                        && destinationConfigured
                        && endpointConfigured;

        NotificationDeliveryProviderReadinessStatus readinessStatus;

        String statusMessage;

        if (!enabled) {

            readinessStatus =
                    NotificationDeliveryProviderReadinessStatus.DISABLED;

            statusMessage =
                    "Telegram delivery provider is disabled.";

        } else if (!configurationComplete) {

            readinessStatus =
                    NotificationDeliveryProviderReadinessStatus.CONFIGURATION_MISSING;

            statusMessage =
                    "Telegram delivery provider configuration is incomplete.";

        } else {

            readinessStatus =
                    NotificationDeliveryProviderReadinessStatus.READY;

            statusMessage =
                    "Telegram delivery provider configuration is ready.";
        }

        return new NotificationDeliveryProviderStatus(

                NotificationDeliveryChannel.TELEGRAM,

                enabled,

                credentialConfigured,

                destinationConfigured,

                endpointConfigured,

                configurationComplete,

                enabled && configurationComplete,

                readinessStatus,

                NotificationDeliveryMode.MANUAL_ONLY,

                statusMessage
        );
    }

}