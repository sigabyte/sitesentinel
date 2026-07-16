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

        TelegramConnectivityResult connectivityResult =
                provider.checkConnectivity();

        NotificationDeliveryProviderCheckStatus checkStatus =
                mapConnectivityStatus(
                        connectivityResult.getStatus()
                );

        String resultMessage =
                connectivityResult.getDiagnosticMessage();

        Integer httpStatusCode =
                connectivityResult.getHttpStatusCode();

        checkService.record(
                status.getChannel(),
                checkStatus,
                resultMessage,
                httpStatusCode
        );

        return new TelegramProviderHealthCheckResult(
                checkStatus,
                resultMessage,
                httpStatusCode
        );
    }

    private NotificationDeliveryProviderCheckStatus mapConnectivityStatus(
            TelegramConnectivityStatus connectivityStatus
    ) {
        return switch (connectivityStatus) {

            case HEALTHY ->
                    NotificationDeliveryProviderCheckStatus.HEALTHY;

            case AUTHENTICATION_FAILED ->
                    NotificationDeliveryProviderCheckStatus.AUTHENTICATION_FAILED;

            case TIMEOUT ->
                    NotificationDeliveryProviderCheckStatus.TIMEOUT;

            case UNREACHABLE ->
                    NotificationDeliveryProviderCheckStatus.UNREACHABLE;

            case INVALID_RESPONSE ->
                    NotificationDeliveryProviderCheckStatus.INVALID_RESPONSE;

            case INTERRUPTED ->
                    NotificationDeliveryProviderCheckStatus.INTERRUPTED;

            case FAILED ->
                    NotificationDeliveryProviderCheckStatus.FAILED;
        };
    }

}