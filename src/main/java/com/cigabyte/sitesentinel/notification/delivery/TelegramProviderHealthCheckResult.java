package com.cigabyte.sitesentinel.notification.delivery;

public class TelegramProviderHealthCheckResult {

    private final NotificationDeliveryProviderCheckStatus status;

    private final String message;

    private final Integer httpStatusCode;

    public TelegramProviderHealthCheckResult(
            NotificationDeliveryProviderCheckStatus status,
            String message
    ) {
        this(
                status,
                message,
                null
        );
    }

    public TelegramProviderHealthCheckResult(
            NotificationDeliveryProviderCheckStatus status,
            String message,
            Integer httpStatusCode
    ) {
        this.status = status;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }

    public NotificationDeliveryProviderCheckStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }
}