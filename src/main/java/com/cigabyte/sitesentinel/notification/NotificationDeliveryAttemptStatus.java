package com.cigabyte.sitesentinel.notification;

public enum NotificationDeliveryAttemptStatus {
    PENDING,

    SIMULATED_SUCCESS,
    SIMULATED_FAILURE,
    SKIPPED,

    SENT,
    FAILED,
    CONFIGURATION_MISSING,
    DISABLED
}