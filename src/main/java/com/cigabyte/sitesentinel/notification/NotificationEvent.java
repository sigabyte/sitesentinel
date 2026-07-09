package com.cigabyte.sitesentinel.notification;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "notification_events",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_notification_events_deduplication_key",
                        columnNames = "deduplication_key"
                )
        }
)
public class NotificationEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "website_id", nullable = false)
    private UUID websiteId;

    @Column(name = "monitoring_run_id", nullable = false)
    private UUID monitoringRunId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 80)
    private NotificationEventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationEventSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationEventStatus status = NotificationEventStatus.UNREAD;

    @Column(nullable = false, length = 220)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "deduplication_key", nullable = false, length = 300)
    private String deduplicationKey;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected NotificationEvent() {
    }

    public NotificationEvent(
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
        this.status = NotificationEventStatus.UNREAD;
    }

    @PrePersist
    void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.status == null) {
            this.status = NotificationEventStatus.UNREAD;
        }

        if (this.severity == null) {
            this.severity = NotificationEventSeverity.INFO;
        }
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
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

    public NotificationEventStatus getStatus() {
        return status;
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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void markRead() {
        this.status = NotificationEventStatus.READ;
    }

    public void markUnread() {
        this.status = NotificationEventStatus.UNREAD;
    }
}