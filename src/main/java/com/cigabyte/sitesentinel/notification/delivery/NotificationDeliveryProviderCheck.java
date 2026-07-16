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
import java.util.Objects;

@Entity
@Table(name = "notification_delivery_provider_checks")
public class NotificationDeliveryProviderCheck {

    private static final int RESULT_MESSAGE_MAX_LENGTH = 500;

    private static final String RESULT_MESSAGE_TRUNCATION_SUFFIX =
            "...";

    private static final String DEFAULT_RESULT_MESSAGE =
            "No provider-check result message was provided.";

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

    @Column(name = "http_status_code")
    private Integer httpStatusCode;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected NotificationDeliveryProviderCheck() {
    }

    public NotificationDeliveryProviderCheck(
            NotificationDeliveryChannel channel,
            NotificationDeliveryProviderCheckStatus status,
            OffsetDateTime checkedAt,
            String resultMessage,
            Integer httpStatusCode
    ) {
        this.channel = Objects.requireNonNull(
                channel,
                "Provider-check channel is required."
        );

        this.status = Objects.requireNonNull(
                status,
                "Provider-check status is required."
        );

        this.checkedAt = Objects.requireNonNull(
                checkedAt,
                "Provider-check timestamp is required."
        );

        this.resultMessage =
                normalizeResultMessage(resultMessage);

        this.httpStatusCode =
                validateHttpStatusCode(httpStatusCode);
    }

    private String normalizeResultMessage(String value) {
        if (value == null || value.isBlank()) {
            return DEFAULT_RESULT_MESSAGE;
        }

        String normalizedValue =
                value.trim();

        return truncateResultMessage(normalizedValue);
    }

    private String truncateResultMessage(String value) {
        if (value.length() <= RESULT_MESSAGE_MAX_LENGTH) {
            return value;
        }

        int maximumContentLength =
                RESULT_MESSAGE_MAX_LENGTH
                        - RESULT_MESSAGE_TRUNCATION_SUFFIX.length();

        int endIndex =
                maximumContentLength;

        if (endIndex > 0
                && endIndex < value.length()
                && Character.isHighSurrogate(
                value.charAt(endIndex - 1)
        )
                && Character.isLowSurrogate(
                value.charAt(endIndex)
        )) {

            endIndex--;
        }

        return value.substring(
                0,
                endIndex
        ) + RESULT_MESSAGE_TRUNCATION_SUFFIX;
    }

    private Integer validateHttpStatusCode(Integer value) {
        if (value == null) {
            return null;
        }

        if (value < 100 || value > 599) {
            throw new IllegalArgumentException(
                    "HTTP status code must be between 100 and 599."
            );
        }

        return value;
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

    public Integer getHttpStatusCode() { return httpStatusCode; }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}