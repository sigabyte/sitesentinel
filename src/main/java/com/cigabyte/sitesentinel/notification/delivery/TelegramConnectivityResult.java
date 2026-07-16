package com.cigabyte.sitesentinel.notification.delivery;

import java.util.Objects;

public class TelegramConnectivityResult {

    private final TelegramConnectivityStatus status;

    private final String diagnosticMessage;

    private final Integer httpStatusCode;

    public TelegramConnectivityResult(
            TelegramConnectivityStatus status,
            String diagnosticMessage,
            Integer httpStatusCode
    ) {
        this.status = Objects.requireNonNull(
                status,
                "Telegram connectivity status is required."
        );

        this.diagnosticMessage = normalizeDiagnosticMessage(
                diagnosticMessage
        );

        this.httpStatusCode = httpStatusCode;
    }

    public TelegramConnectivityStatus getStatus() {
        return status;
    }

    public String getDiagnosticMessage() {
        return diagnosticMessage;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public boolean isHealthy() {
        return status == TelegramConnectivityStatus.HEALTHY;
    }

    private String normalizeDiagnosticMessage(String value) {
        if (value == null || value.isBlank()) {
            return "No connectivity diagnostic message was provided.";
        }

        return value.trim();
    }
}