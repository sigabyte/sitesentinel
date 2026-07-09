package com.cigabyte.sitesentinel.notification;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "notification_delivery_attempts")
public class NotificationDeliveryAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "notification_event_id", nullable = false)
    private UUID notificationEventId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private NotificationDeliveryChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private NotificationDeliveryAttemptStatus status;

    @Column(name = "attempted_at", nullable = false)
    private OffsetDateTime attemptedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(name = "result_message", length = 500)
    private String resultMessage;

    @Column(name = "technical_detail", columnDefinition = "TEXT")
    private String technicalDetail;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected NotificationDeliveryAttempt() {
    }

    public NotificationDeliveryAttempt(
            UUID notificationEventId,
            NotificationDeliveryChannel channel,
            NotificationDeliveryAttemptStatus status,
            OffsetDateTime attemptedAt,
            OffsetDateTime completedAt,
            String resultMessage,
            String technicalDetail
    ) {
        this.notificationEventId = notificationEventId;
        this.channel = channel;
        this.status = status;
        this.attemptedAt = attemptedAt;
        this.completedAt = completedAt;
        this.resultMessage = resultMessage;
        this.technicalDetail = technicalDetail;
    }

    @PrePersist
    void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = OffsetDateTime.now();
        }

        if (this.attemptedAt == null) {
            this.attemptedAt = OffsetDateTime.now();
        }

        if (this.status == null) {
            this.status = NotificationDeliveryAttemptStatus.PENDING;
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getNotificationEventId() {
        return notificationEventId;
    }

    public NotificationDeliveryChannel getChannel() {
        return channel;
    }

    public NotificationDeliveryAttemptStatus getStatus() {
        return status;
    }

    public OffsetDateTime getAttemptedAt() {
        return attemptedAt;
    }

    public OffsetDateTime getCompletedAt() {
        return completedAt;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public String getTechnicalDetail() {
        return technicalDetail;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}