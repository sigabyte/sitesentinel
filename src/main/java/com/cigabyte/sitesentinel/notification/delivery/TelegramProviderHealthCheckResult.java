package com.cigabyte.sitesentinel.notification.delivery;

public class TelegramProviderHealthCheckResult {

    private final NotificationDeliveryProviderCheckStatus status;

    private final String message;

    public TelegramProviderHealthCheckResult(
            NotificationDeliveryProviderCheckStatus status,
            String message
    ) {
        this.status = status;
        this.message = message;
    }

    public NotificationDeliveryProviderCheckStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}