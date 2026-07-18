package com.cigabyte.sitesentinel.reporting.dispatch;

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
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "monitoring_run_report_dispatch_attempts")
public class MonitoringRunReportDispatchAttempt {

    private static final int RESULT_MESSAGE_MAX_LENGTH =
            500;

    private static final int TECHNICAL_DETAIL_MAX_LENGTH =
            2000;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "monitoring_run_id", nullable = false)
    private UUID monitoringRunId;

    @Column(name = "pdf_artifact_id", nullable = false)
    private UUID pdfArtifactId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private NotificationDeliveryChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "dispatch_type",
            nullable = false,
            length = 40
    )
    private MonitoringRunReportDispatchType dispatchType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private MonitoringRunReportDispatchStatus status;

    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber;

    @Column(name = "retry_of_attempt_id")
    private UUID retryOfAttemptId;

    @Column(name = "attempted_at", nullable = false)
    private OffsetDateTime attemptedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(name = "result_message", length = 500)
    private String resultMessage;

    @Column(
            name = "technical_detail",
            columnDefinition = "TEXT"
    )
    private String technicalDetail;

    @Column(name = "telegram_message_id")
    private Long telegramMessageId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected MonitoringRunReportDispatchAttempt() {
    }

    private MonitoringRunReportDispatchAttempt(
            UUID monitoringRunId,
            UUID pdfArtifactId,
            MonitoringRunReportDispatchType dispatchType,
            Integer attemptNumber,
            UUID retryOfAttemptId,
            OffsetDateTime attemptedAt
    ) {
        this.monitoringRunId =
                Objects.requireNonNull(
                        monitoringRunId,
                        "Monitoring run ID is required."
                );

        this.pdfArtifactId =
                Objects.requireNonNull(
                        pdfArtifactId,
                        "PDF artifact ID is required."
                );

        this.channel =
                NotificationDeliveryChannel.TELEGRAM;

        this.dispatchType =
                Objects.requireNonNull(
                        dispatchType,
                        "Report dispatch type is required."
                );

        this.attemptNumber =
                validateAttemptNumber(
                        attemptNumber
                );

        this.retryOfAttemptId =
                retryOfAttemptId;

        this.attemptedAt =
                Objects.requireNonNull(
                        attemptedAt,
                        "Report dispatch attempt timestamp is required."
                );

        this.status =
                MonitoringRunReportDispatchStatus.PENDING;

        validateDispatchLineage();
    }

    public static MonitoringRunReportDispatchAttempt
    automatic(
            UUID monitoringRunId,
            UUID pdfArtifactId,
            OffsetDateTime attemptedAt
    ) {
        return new MonitoringRunReportDispatchAttempt(
                monitoringRunId,
                pdfArtifactId,
                MonitoringRunReportDispatchType.AUTOMATIC,
                1,
                null,
                attemptedAt
        );
    }

    public static MonitoringRunReportDispatchAttempt
    manualRetry(
            UUID monitoringRunId,
            UUID pdfArtifactId,
            Integer attemptNumber,
            UUID retryOfAttemptId,
            OffsetDateTime attemptedAt
    ) {
        return new MonitoringRunReportDispatchAttempt(
                monitoringRunId,
                pdfArtifactId,
                MonitoringRunReportDispatchType.MANUAL_RETRY,
                attemptNumber,
                retryOfAttemptId,
                attemptedAt
        );
    }

    public void markSent(
            Long telegramMessageId,
            String resultMessage,
            String technicalDetail,
            OffsetDateTime completedAt
    ) {
        ensurePending();

        Long validatedTelegramMessageId =
                validateTelegramMessageId(
                        telegramMessageId
                );

        String normalizedResultMessage =
                normalizeRequiredText(
                        resultMessage,
                        "Report dispatch result message",
                        RESULT_MESSAGE_MAX_LENGTH
                );

        String normalizedTechnicalDetail =
                normalizeRequiredText(
                        technicalDetail,
                        "Report dispatch technical detail",
                        TECHNICAL_DETAIL_MAX_LENGTH
                );

        OffsetDateTime validatedCompletedAt =
                validateCompletedAt(
                        completedAt
                );

        this.telegramMessageId =
                validatedTelegramMessageId;

        this.resultMessage =
                normalizedResultMessage;

        this.technicalDetail =
                normalizedTechnicalDetail;

        this.completedAt =
                validatedCompletedAt;

        this.status =
                MonitoringRunReportDispatchStatus.SENT;
    }

    public void markFailed(
            String resultMessage,
            String technicalDetail,
            OffsetDateTime completedAt
    ) {
        ensurePending();

        String normalizedResultMessage =
                normalizeRequiredText(
                        resultMessage,
                        "Report dispatch result message",
                        RESULT_MESSAGE_MAX_LENGTH
                );

        String normalizedTechnicalDetail =
                normalizeRequiredText(
                        technicalDetail,
                        "Report dispatch technical detail",
                        TECHNICAL_DETAIL_MAX_LENGTH
                );

        OffsetDateTime validatedCompletedAt =
                validateCompletedAt(
                        completedAt
                );

        this.telegramMessageId =
                null;

        this.resultMessage =
                normalizedResultMessage;

        this.technicalDetail =
                normalizedTechnicalDetail;

        this.completedAt =
                validatedCompletedAt;

        this.status =
                MonitoringRunReportDispatchStatus.FAILED;
    }

    private Integer validateAttemptNumber(
            Integer value
    ) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(
                    "Report dispatch attempt number "
                            + "must be positive."
            );
        }

        return value;
    }

    private void validateDispatchLineage() {
        if (dispatchType
                == MonitoringRunReportDispatchType.AUTOMATIC) {

            if (attemptNumber != 1) {
                throw new IllegalArgumentException(
                        "Automatic report dispatch "
                                + "must use attempt number 1."
                );
            }

            if (retryOfAttemptId != null) {
                throw new IllegalArgumentException(
                        "Automatic report dispatch "
                                + "must not reference a previous attempt."
                );
            }

            return;
        }

        if (dispatchType
                == MonitoringRunReportDispatchType.MANUAL_RETRY) {

            if (attemptNumber < 2) {
                throw new IllegalArgumentException(
                        "Manual report dispatch retry "
                                + "must use attempt number 2 or greater."
                );
            }

            if (retryOfAttemptId == null) {
                throw new IllegalArgumentException(
                        "Manual report dispatch retry "
                                + "requires the previous attempt ID."
                );
            }
        }
    }

    private Long validateTelegramMessageId(
            Long value
    ) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(
                    "Successful Telegram report dispatch "
                            + "requires a positive message ID."
            );
        }

        return value;
    }

    private OffsetDateTime validateCompletedAt(
            OffsetDateTime value
    ) {
        OffsetDateTime requiredValue =
                Objects.requireNonNull(
                        value,
                        "Report dispatch completion timestamp is required."
                );

        if (requiredValue.isBefore(attemptedAt)) {
            throw new IllegalArgumentException(
                    "Report dispatch completion timestamp "
                            + "must not precede the attempt timestamp."
            );
        }

        return requiredValue;
    }

    private String normalizeRequiredText(
            String value,
            String fieldName,
            int maximumLength
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " is required."
            );
        }

        String normalizedValue =
                value.trim();

        if (normalizedValue.length()
                > maximumLength) {

            throw new IllegalArgumentException(
                    fieldName
                            + " must not exceed "
                            + maximumLength
                            + " characters."
            );
        }

        return normalizedValue;
    }

    private void ensurePending() {
        if (status
                != MonitoringRunReportDispatchStatus.PENDING) {

            throw new IllegalStateException(
                    "Only a pending report dispatch attempt "
                            + "can be completed."
            );
        }
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getMonitoringRunId() {
        return monitoringRunId;
    }

    public UUID getPdfArtifactId() {
        return pdfArtifactId;
    }

    public NotificationDeliveryChannel getChannel() {
        return channel;
    }

    public MonitoringRunReportDispatchType getDispatchType() {
        return dispatchType;
    }

    public MonitoringRunReportDispatchStatus getStatus() {
        return status;
    }

    public Integer getAttemptNumber() {
        return attemptNumber;
    }

    public UUID getRetryOfAttemptId() {
        return retryOfAttemptId;
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

    public Long getTelegramMessageId() {
        return telegramMessageId;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}