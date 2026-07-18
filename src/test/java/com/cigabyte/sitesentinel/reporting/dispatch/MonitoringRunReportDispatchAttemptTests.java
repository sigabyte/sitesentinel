package com.cigabyte.sitesentinel.reporting.dispatch;

import com.cigabyte.sitesentinel.notification.NotificationDeliveryChannel;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MonitoringRunReportDispatchAttemptTests {

    private static final OffsetDateTime ATTEMPTED_AT =
            OffsetDateTime.of(
                    2500,
                    1,
                    1,
                    12,
                    0,
                    0,
                    0,
                    ZoneOffset.UTC
            );

    @Test
    void automaticCreatesFirstPendingTelegramAttempt() {
        UUID monitoringRunId =
                UUID.randomUUID();

        UUID pdfArtifactId =
                UUID.randomUUID();

        MonitoringRunReportDispatchAttempt attempt =
                MonitoringRunReportDispatchAttempt
                        .automatic(
                                monitoringRunId,
                                pdfArtifactId,
                                ATTEMPTED_AT
                        );

        assertEquals(
                monitoringRunId,
                attempt.getMonitoringRunId()
        );

        assertEquals(
                pdfArtifactId,
                attempt.getPdfArtifactId()
        );

        assertEquals(
                NotificationDeliveryChannel.TELEGRAM,
                attempt.getChannel()
        );

        assertEquals(
                MonitoringRunReportDispatchType.AUTOMATIC,
                attempt.getDispatchType()
        );

        assertEquals(
                MonitoringRunReportDispatchStatus.PENDING,
                attempt.getStatus()
        );

        assertEquals(
                1,
                attempt.getAttemptNumber()
        );

        assertNull(
                attempt.getRetryOfAttemptId()
        );

        assertEquals(
                ATTEMPTED_AT,
                attempt.getAttemptedAt()
        );

        assertNull(
                attempt.getCompletedAt()
        );

        assertNull(
                attempt.getTelegramMessageId()
        );
    }

    @Test
    void manualRetryCreatesLinkedAttemptAndRejectsInvalidLineage() {
        UUID previousAttemptId =
                UUID.randomUUID();

        MonitoringRunReportDispatchAttempt attempt =
                MonitoringRunReportDispatchAttempt
                        .manualRetry(
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                2,
                                previousAttemptId,
                                ATTEMPTED_AT
                        );

        assertEquals(
                MonitoringRunReportDispatchType.MANUAL_RETRY,
                attempt.getDispatchType()
        );

        assertEquals(
                2,
                attempt.getAttemptNumber()
        );

        assertEquals(
                previousAttemptId,
                attempt.getRetryOfAttemptId()
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> MonitoringRunReportDispatchAttempt
                        .manualRetry(
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                1,
                                previousAttemptId,
                                ATTEMPTED_AT
                        )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> MonitoringRunReportDispatchAttempt
                        .manualRetry(
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                2,
                                null,
                                ATTEMPTED_AT
                        )
        );
    }

    @Test
    void markSentCompletesPendingAttempt() {
        MonitoringRunReportDispatchAttempt attempt =
                automaticAttempt();

        OffsetDateTime completedAt =
                ATTEMPTED_AT.plusSeconds(2);

        attempt.markSent(
                2468L,
                "  Telegram report was sent.  ",
                "  Telegram Bot API accepted the document.  ",
                completedAt
        );

        assertEquals(
                MonitoringRunReportDispatchStatus.SENT,
                attempt.getStatus()
        );

        assertEquals(
                Long.valueOf(2468L),
                attempt.getTelegramMessageId()
        );

        assertEquals(
                "Telegram report was sent.",
                attempt.getResultMessage()
        );

        assertEquals(
                "Telegram Bot API accepted the document.",
                attempt.getTechnicalDetail()
        );

        assertEquals(
                completedAt,
                attempt.getCompletedAt()
        );
    }

    @Test
    void markFailedCompletesPendingAttemptWithoutMessageId() {
        MonitoringRunReportDispatchAttempt attempt =
                automaticAttempt();

        OffsetDateTime completedAt =
                ATTEMPTED_AT.plusSeconds(3);

        attempt.markFailed(
                "Telegram report dispatch failed.",
                "Telegram Bot API returned an unsuccessful response.",
                completedAt
        );

        assertEquals(
                MonitoringRunReportDispatchStatus.FAILED,
                attempt.getStatus()
        );

        assertNull(
                attempt.getTelegramMessageId()
        );

        assertEquals(
                completedAt,
                attempt.getCompletedAt()
        );
    }

    @Test
    void terminalAttemptCannotTransitionAgain() {
        MonitoringRunReportDispatchAttempt sentAttempt =
                automaticAttempt();

        sentAttempt.markSent(
                2468L,
                "Telegram report was sent.",
                "Telegram Bot API accepted the document.",
                ATTEMPTED_AT.plusSeconds(1)
        );

        assertThrows(
                IllegalStateException.class,
                () -> sentAttempt.markFailed(
                        "Later failure.",
                        "Invalid second transition.",
                        ATTEMPTED_AT.plusSeconds(2)
                )
        );

        MonitoringRunReportDispatchAttempt failedAttempt =
                automaticAttempt();

        failedAttempt.markFailed(
                "Telegram report dispatch failed.",
                "Controlled failure.",
                ATTEMPTED_AT.plusSeconds(1)
        );

        assertThrows(
                IllegalStateException.class,
                () -> failedAttempt.markSent(
                        2468L,
                        "Later success.",
                        "Invalid second transition.",
                        ATTEMPTED_AT.plusSeconds(2)
                )
        );
    }

    @Test
    void completionValidationAndTextLimitsProtectPendingState() {
        MonitoringRunReportDispatchAttempt attempt =
                automaticAttempt();

        assertThrows(
                IllegalArgumentException.class,
                () -> attempt.markSent(
                        0L,
                        "Telegram report was sent.",
                        "Telegram response accepted.",
                        ATTEMPTED_AT.plusSeconds(1)
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> attempt.markFailed(
                        "Telegram report dispatch failed.",
                        "Controlled failure.",
                        ATTEMPTED_AT.minusSeconds(1)
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> attempt.markFailed(
                        "a".repeat(501),
                        "Controlled failure.",
                        ATTEMPTED_AT.plusSeconds(1)
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> attempt.markFailed(
                        "Telegram report dispatch failed.",
                        "a".repeat(2001),
                        ATTEMPTED_AT.plusSeconds(1)
                )
        );

        assertEquals(
                MonitoringRunReportDispatchStatus.PENDING,
                attempt.getStatus()
        );

        assertFalse(
                attempt.getStatus()
                        == MonitoringRunReportDispatchStatus.SENT
        );

        assertTrue(
                attempt.getCompletedAt() == null
        );
    }

    private MonitoringRunReportDispatchAttempt
    automaticAttempt() {

        return MonitoringRunReportDispatchAttempt
                .automatic(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        ATTEMPTED_AT
                );
    }
}