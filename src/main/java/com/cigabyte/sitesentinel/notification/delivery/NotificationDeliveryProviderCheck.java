package com.cigabyte.sitesentinel.notification.delivery;

import com.cigabyte.sitesentinel.notification.NotificationDeliveryChannel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "notification_delivery_provider_checks")
public class NotificationDeliveryProviderCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private NotificationDeliveryChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private NotificationDeliveryProviderCheckStatus status;

    @Column(name = "checked_at", nullable = false)
    private OffsetDateTime checkedAt;

    @Column(name = "result_message", nullable = false, length = 500)
    private String resultMessage;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected NotificationDeliveryProviderCheck() {
    }

    public NotificationDeliveryProviderCheck(
            NotificationDeliveryChannel channel,
            NotificationDeliveryProviderCheckStatus status,
            OffsetDateTime checkedAt,
            String resultMessage
    ) {
        this.channel = channel;
        this.status = status;
        this.checkedAt = checkedAt;
        this.resultMessage = resultMessage;
    }

    @PrePersist
    void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();

        if (this.checkedAt == null) {
            this.checkedAt = now;
        }

        if (this.createdAt == null) {
            this.createdAt = now;
        }
    }

    public UUID getId() {
        return id;
    }

    public NotificationDeliveryChannel getChannel() {
        return channel;
    }

    public NotificationDeliveryProviderCheckStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCheckedAt() {
        return checkedAt;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}