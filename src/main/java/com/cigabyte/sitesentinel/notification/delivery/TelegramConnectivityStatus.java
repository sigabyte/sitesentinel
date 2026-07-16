package com.cigabyte.sitesentinel.notification.delivery;

public enum TelegramConnectivityStatus {

    HEALTHY,

    AUTHENTICATION_FAILED,

    TIMEOUT,

    UNREACHABLE,

    INVALID_RESPONSE,

    INTERRUPTED,

    FAILED
}