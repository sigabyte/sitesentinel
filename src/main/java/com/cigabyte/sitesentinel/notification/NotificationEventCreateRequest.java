package com.cigabyte.sitesentinel.notification;

import java.util.UUID;

public class NotificationEventCreateRequest {

    private final UUID websiteId;
    private final UUID monitoringRunId;
    private final NotificationEventType eventType;
    private final NotificationEventSeverity severity;
    private final String title;
    private final String message;
    private final String deduplicationKey;

    public NotificationEventCreateRequest(
            UUID websiteId,
            UUID monitoringRunId,
            NotificationEventType eventType,
            NotificationEventSeverity severity,
            String title,
            String message,
            String deduplicationKey
    ) {
        this.websiteId = websiteId;
        this.monitoringRunId = monitoringRunId;
        this.eventType = eventType;
        this.severity = severity;
        this.title = title;
        this.message = message;
        this.deduplicationKey = deduplicationKey;
    }

    public UUID getWebsiteId() {
        return websiteId;
    }

    public UUID getMonitoringRunId() {
        return monitoringRunId;
    }

    public NotificationEventType getEventType() {
        return eventType;
    }

    public NotificationEventSeverity getSeverity() {
        return severity;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getDeduplicationKey() {
        return deduplicationKey;
    }
}