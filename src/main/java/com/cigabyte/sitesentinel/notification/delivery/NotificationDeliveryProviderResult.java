package com.cigabyte.sitesentinel.notification.delivery;

import com.cigabyte.sitesentinel.notification.NotificationDeliveryAttemptStatus;

public class NotificationDeliveryProviderResult {

    private final NotificationDeliveryAttemptStatus attemptStatus;

    private final boolean deliveryAttempted;

    private final boolean successful;

    private final String resultMessage;

    private final String technicalDetail;

    private NotificationDeliveryProviderResult(
            NotificationDeliveryAttemptStatus attemptStatus,
            boolean deliveryAttempted,
            boolean successful,
            String resultMessage,
            String technicalDetail
    ) {
        this.attemptStatus = attemptStatus;
        this.deliveryAttempted = deliveryAttempted;
        this.successful = successful;
        this.resultMessage = resultMessage;
        this.technicalDetail = technicalDetail;
    }

    public static NotificationDeliveryProviderResult success(
            String resultMessage,
            String technicalDetail
    ) {
        return new NotificationDeliveryProviderResult(
                NotificationDeliveryAttemptStatus.SENT,
                true,
                true,
                resultMessage,
                technicalDetail
        );
    }

    public static NotificationDeliveryProviderResult failure(
            String resultMessage,
            String technicalDetail
    ) {
        return new NotificationDeliveryProviderResult(
                NotificationDeliveryAttemptStatus.FAILED,
                true,
                false,
                resultMessage,
                technicalDetail
        );
    }

    public static NotificationDeliveryProviderResult disabled(
            String resultMessage,
            String technicalDetail
    ) {
        return new NotificationDeliveryProviderResult(
                NotificationDeliveryAttemptStatus.DISABLED,
                false,
                false,
                resultMessage,
                technicalDetail
        );
    }

    public static NotificationDeliveryProviderResult configurationMissing(
            String resultMessage,
            String technicalDetail
    ) {
        return new NotificationDeliveryProviderResult(
                NotificationDeliveryAttemptStatus.CONFIGURATION_MISSING,
                false,
                false,
                resultMessage,
                technicalDetail
        );
    }

    public NotificationDeliveryAttemptStatus getAttemptStatus() {
        return attemptStatus;
    }

    public boolean isDeliveryAttempted() {
        return deliveryAttempted;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public String getTechnicalDetail() {
        return technicalDetail;
    }
}