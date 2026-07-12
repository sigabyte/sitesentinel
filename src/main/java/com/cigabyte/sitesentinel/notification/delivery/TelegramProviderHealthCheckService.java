package com.cigabyte.sitesentinel.notification.delivery;

import org.springframework.stereotype.Service;

@Service
public class TelegramProviderHealthCheckService {

    private final TelegramDeliveryReadinessService readinessService;

    private final NotificationDeliveryProviderCheckService checkService;

    private final TelegramNotificationDeliveryProvider provider;

    public TelegramProviderHealthCheckService(
            TelegramDeliveryReadinessService readinessService,
            NotificationDeliveryProviderCheckService checkService,
            TelegramNotificationDeliveryProvider provider
    ) {
        this.readinessService = readinessService;
        this.checkService = checkService;
        this.provider = provider;
    }

    public TelegramProviderHealthCheckResult performHealthCheck() {

        NotificationDeliveryProviderStatus status =
                readinessService.evaluate();

        if (!status.isEnabled()) {

            checkService.record(
                    status.getChannel(),
                    NotificationDeliveryProviderCheckStatus.DISABLED,
                    "Telegram delivery provider is disabled."
            );

            return new TelegramProviderHealthCheckResult(
                    NotificationDeliveryProviderCheckStatus.DISABLED,
                    "Telegram delivery provider is disabled."
            );
        }

        if (!status.isConfigurationComplete()) {

            checkService.record(
                    status.getChannel(),
                    NotificationDeliveryProviderCheckStatus.CONFIGURATION_MISSING,
                    "Telegram delivery provider configuration is incomplete."
            );

            return new TelegramProviderHealthCheckResult(
                    NotificationDeliveryProviderCheckStatus.CONFIGURATION_MISSING,
                    "Telegram delivery provider configuration is incomplete."
            );
        }

        // Telegram API validation will be added below.

        boolean connected = provider.verifyConnection();

        if (connected) {

            checkService.record(
                    status.getChannel(),
                    NotificationDeliveryProviderCheckStatus.HEALTHY,
                    "Telegram provider connection verified."
            );

            return new TelegramProviderHealthCheckResult(
                    NotificationDeliveryProviderCheckStatus.HEALTHY,
                    "Telegram provider connection verified."
            );
        }

        checkService.record(
                status.getChannel(),
                NotificationDeliveryProviderCheckStatus.AUTHENTICATION_FAILED,
                "Telegram provider authentication failed."
        );

        return new TelegramProviderHealthCheckResult(
                NotificationDeliveryProviderCheckStatus.AUTHENTICATION_FAILED,
                "Telegram provider authentication failed."
        );
    }

}