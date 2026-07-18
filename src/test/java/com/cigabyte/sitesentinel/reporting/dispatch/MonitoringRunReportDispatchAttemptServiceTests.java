package com.cigabyte.sitesentinel.reporting.dispatch;

import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
import com.cigabyte.sitesentinel.monitoring.MonitoringRunRepository;
import com.cigabyte.sitesentinel.notification.NotificationDeliveryChannel;
import com.cigabyte.sitesentinel.reporting.pdf.MonitoringRunPdfArtifact;
import com.cigabyte.sitesentinel.reporting.pdf.MonitoringRunPdfArtifactService;
import com.cigabyte.sitesentinel.reporting.pdf.MonitoringRunPdfVersion;
import com.cigabyte.sitesentinel.website.Website;
import com.cigabyte.sitesentinel.website.WebsiteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class MonitoringRunReportDispatchAttemptServiceTests {

    private final WebsiteRepository websiteRepository;

    private final MonitoringRunRepository
            monitoringRunRepository;

    private final MonitoringRunPdfArtifactService
            pdfArtifactService;

    private final MonitoringRunReportDispatchAttemptService
            dispatchAttemptService;

    private final MonitoringRunReportDispatchAttemptRepository
            dispatchAttemptRepository;

    @Autowired
    MonitoringRunReportDispatchAttemptServiceTests(
            WebsiteRepository websiteRepository,
            MonitoringRunRepository monitoringRunRepository,
            MonitoringRunPdfArtifactService pdfArtifactService,
            MonitoringRunReportDispatchAttemptService
                    dispatchAttemptService,
            MonitoringRunReportDispatchAttemptRepository
                    dispatchAttemptRepository
    ) {
        this.websiteRepository =
                websiteRepository;

        this.monitoringRunRepository =
                monitoringRunRepository;

        this.pdfArtifactService =
                pdfArtifactService;

        this.dispatchAttemptService =
                dispatchAttemptService;

        this.dispatchAttemptRepository =
                dispatchAttemptRepository;
    }

    @Test
    void startAutomaticAttemptPersistsPendingTelegramAttempt() {
        MonitoringRun monitoringRun =
                persistCompletedRun(
                        persistWebsite()
                );

        MonitoringRunPdfArtifact artifact =
                persistArtifact(
                        monitoringRun,
                        "automatic-service-test"
                );

        MonitoringRunReportDispatchAttempt attempt =
                dispatchAttemptService
                        .startAutomaticAttempt(
                                monitoringRun.getId(),
                                artifact.getId()
                        );

        assertNotNull(
                attempt.getId()
        );

        assertEquals(
                monitoringRun.getId(),
                attempt.getMonitoringRunId()
        );

        assertEquals(
                artifact.getId(),
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

        assertNotNull(
                attempt.getAttemptedAt()
        );

        assertNull(
                attempt.getCompletedAt()
        );
    }

    @Test
    void startAutomaticAttemptRejectsDuplicateBeforeSecondInsert() {
        MonitoringRun monitoringRun =
                persistCompletedRun(
                        persistWebsite()
                );

        MonitoringRunPdfArtifact artifact =
                persistArtifact(
                        monitoringRun,
                        "automatic-idempotency-test"
                );

        dispatchAttemptService.startAutomaticAttempt(
                monitoringRun.getId(),
                artifact.getId()
        );

        assertThrows(
                IllegalStateException.class,
                () -> dispatchAttemptService
                        .startAutomaticAttempt(
                                monitoringRun.getId(),
                                artifact.getId()
                        )
        );

        assertEquals(
                1,
                dispatchAttemptRepository
                        .countByMonitoringRunId(
                                monitoringRun.getId()
                        )
        );
    }

    @Test
    void markSentPersistsSuccessfulTerminalState() {
        MonitoringRun monitoringRun =
                persistCompletedRun(
                        persistWebsite()
                );

        MonitoringRunPdfArtifact artifact =
                persistArtifact(
                        monitoringRun,
                        "sent-service-test"
                );

        MonitoringRunReportDispatchAttempt pendingAttempt =
                dispatchAttemptService
                        .startAutomaticAttempt(
                                monitoringRun.getId(),
                                artifact.getId()
                        );

        MonitoringRunReportDispatchAttempt sentAttempt =
                dispatchAttemptService.markSent(
                        pendingAttempt.getId(),
                        monitoringRun.getId(),
                        2468L,
                        "Telegram report was sent successfully.",
                        "Telegram Bot API accepted the document."
                );

        assertEquals(
                MonitoringRunReportDispatchStatus.SENT,
                sentAttempt.getStatus()
        );

        assertEquals(
                Long.valueOf(2468L),
                sentAttempt.getTelegramMessageId()
        );

        assertNotNull(
                sentAttempt.getCompletedAt()
        );

        MonitoringRunReportDispatchAttempt reloadedAttempt =
                dispatchAttemptService.findAttempt(
                        pendingAttempt.getId(),
                        monitoringRun.getId()
                );

        assertEquals(
                MonitoringRunReportDispatchStatus.SENT,
                reloadedAttempt.getStatus()
        );

        assertEquals(
                Long.valueOf(2468L),
                reloadedAttempt.getTelegramMessageId()
        );
    }

    @Test
    void markFailedPersistsFailedTerminalState() {
        MonitoringRun monitoringRun =
                persistCompletedRun(
                        persistWebsite()
                );

        MonitoringRunPdfArtifact artifact =
                persistArtifact(
                        monitoringRun,
                        "failed-service-test"
                );

        MonitoringRunReportDispatchAttempt pendingAttempt =
                dispatchAttemptService
                        .startAutomaticAttempt(
                                monitoringRun.getId(),
                                artifact.getId()
                        );

        MonitoringRunReportDispatchAttempt failedAttempt =
                dispatchAttemptService.markFailed(
                        pendingAttempt.getId(),
                        monitoringRun.getId(),
                        "Telegram report dispatch failed.",
                        "Telegram Bot API returned an unsuccessful response."
                );

        assertEquals(
                MonitoringRunReportDispatchStatus.FAILED,
                failedAttempt.getStatus()
        );

        assertNull(
                failedAttempt.getTelegramMessageId()
        );

        assertNotNull(
                failedAttempt.getCompletedAt()
        );

        List<MonitoringRunReportDispatchAttempt> attempts =
                dispatchAttemptService
                        .findAttemptsForMonitoringRun(
                                monitoringRun.getId()
                        );

        assertEquals(
                1,
                attempts.size()
        );

        assertEquals(
                MonitoringRunReportDispatchStatus.FAILED,
                attempts.get(0).getStatus()
        );
    }

    @Test
    void completionRejectsAttemptOwnedByDifferentMonitoringRun() {
        Website website =
                persistWebsite();

        MonitoringRun ownerRun =
                persistCompletedRun(
                        website
                );

        MonitoringRun differentRun =
                persistCompletedRun(
                        website
                );

        MonitoringRunPdfArtifact artifact =
                persistArtifact(
                        ownerRun,
                        "attempt-ownership-service-test"
                );

        MonitoringRunReportDispatchAttempt pendingAttempt =
                dispatchAttemptService
                        .startAutomaticAttempt(
                                ownerRun.getId(),
                                artifact.getId()
                        );

        assertThrows(
                IllegalArgumentException.class,
                () -> dispatchAttemptService.markFailed(
                        pendingAttempt.getId(),
                        differentRun.getId(),
                        "Invalid failure.",
                        "Attempt ownership must be enforced."
                )
        );

        MonitoringRunReportDispatchAttempt unchangedAttempt =
                dispatchAttemptService.findAttempt(
                        pendingAttempt.getId(),
                        ownerRun.getId()
                );

        assertEquals(
                MonitoringRunReportDispatchStatus.PENDING,
                unchangedAttempt.getStatus()
        );
    }

    @Test
    void startManualRetryPersistsLinkedPendingAttempt() {
        MonitoringRun monitoringRun =
                persistCompletedRun(
                        persistWebsite()
                );

        MonitoringRunPdfArtifact artifact =
                persistArtifact(
                        monitoringRun,
                        "manual-retry-service-test"
                );

        MonitoringRunReportDispatchAttempt automaticAttempt =
                dispatchAttemptService
                        .startAutomaticAttempt(
                                monitoringRun.getId(),
                                artifact.getId()
                        );

        MonitoringRunReportDispatchAttempt failedAttempt =
                dispatchAttemptService.markFailed(
                        automaticAttempt.getId(),
                        monitoringRun.getId(),
                        "Automatic Telegram report dispatch failed.",
                        "Controlled failure before manual retry."
                );

        MonitoringRunReportDispatchAttempt retryAttempt =
                dispatchAttemptService.startManualRetry(
                        failedAttempt.getId(),
                        monitoringRun.getId()
                );

        assertNotNull(
                retryAttempt.getId()
        );

        assertEquals(
                monitoringRun.getId(),
                retryAttempt.getMonitoringRunId()
        );

        assertEquals(
                artifact.getId(),
                retryAttempt.getPdfArtifactId()
        );

        assertEquals(
                MonitoringRunReportDispatchType.MANUAL_RETRY,
                retryAttempt.getDispatchType()
        );

        assertEquals(
                MonitoringRunReportDispatchStatus.PENDING,
                retryAttempt.getStatus()
        );

        assertEquals(
                2,
                retryAttempt.getAttemptNumber()
        );

        assertEquals(
                failedAttempt.getId(),
                retryAttempt.getRetryOfAttemptId()
        );

        assertNull(
                retryAttempt.getCompletedAt()
        );

        assertNull(
                retryAttempt.getTelegramMessageId()
        );

        assertEquals(
                2,
                dispatchAttemptRepository
                        .countByMonitoringRunId(
                                monitoringRun.getId()
                        )
        );
    }

    @Test
    void startManualRetryRejectsPendingAndSentAttempts() {
        MonitoringRun pendingAttemptRun =
                persistCompletedRun(
                        persistWebsite()
                );

        MonitoringRunPdfArtifact pendingAttemptArtifact =
                persistArtifact(
                        pendingAttemptRun,
                        "pending-retry-rejection-test"
                );

        MonitoringRunReportDispatchAttempt pendingAttempt =
                dispatchAttemptService
                        .startAutomaticAttempt(
                                pendingAttemptRun.getId(),
                                pendingAttemptArtifact.getId()
                        );

        assertThrows(
                IllegalStateException.class,
                () -> dispatchAttemptService.startManualRetry(
                        pendingAttempt.getId(),
                        pendingAttemptRun.getId()
                )
        );

        MonitoringRun sentAttemptRun =
                persistCompletedRun(
                        persistWebsite()
                );

        MonitoringRunPdfArtifact sentAttemptArtifact =
                persistArtifact(
                        sentAttemptRun,
                        "sent-retry-rejection-test"
                );

        MonitoringRunReportDispatchAttempt secondPendingAttempt =
                dispatchAttemptService
                        .startAutomaticAttempt(
                                sentAttemptRun.getId(),
                                sentAttemptArtifact.getId()
                        );

        MonitoringRunReportDispatchAttempt sentAttempt =
                dispatchAttemptService.markSent(
                        secondPendingAttempt.getId(),
                        sentAttemptRun.getId(),
                        2468L,
                        "Telegram report was sent successfully.",
                        "Telegram Bot API accepted the document."
                );

        assertThrows(
                IllegalStateException.class,
                () -> dispatchAttemptService.startManualRetry(
                        sentAttempt.getId(),
                        sentAttemptRun.getId()
                )
        );

        assertEquals(
                1,
                dispatchAttemptRepository
                        .countByMonitoringRunId(
                                pendingAttemptRun.getId()
                        )
        );

        assertEquals(
                1,
                dispatchAttemptRepository
                        .countByMonitoringRunId(
                                sentAttemptRun.getId()
                        )
        );
    }

    @Test
    void startManualRetryRejectsFailedAttemptThatIsNotLatest() {
        MonitoringRun monitoringRun =
                persistCompletedRun(
                        persistWebsite()
                );

        MonitoringRunPdfArtifact artifact =
                persistArtifact(
                        monitoringRun,
                        "stale-retry-attempt-test"
                );

        MonitoringRunReportDispatchAttempt automaticAttempt =
                dispatchAttemptService
                        .startAutomaticAttempt(
                                monitoringRun.getId(),
                                artifact.getId()
                        );

        MonitoringRunReportDispatchAttempt failedAutomaticAttempt =
                dispatchAttemptService.markFailed(
                        automaticAttempt.getId(),
                        monitoringRun.getId(),
                        "Automatic dispatch failed.",
                        "Controlled initial failure."
                );

        MonitoringRunReportDispatchAttempt firstRetry =
                dispatchAttemptService.startManualRetry(
                        failedAutomaticAttempt.getId(),
                        monitoringRun.getId()
                );

        dispatchAttemptService.markFailed(
                firstRetry.getId(),
                monitoringRun.getId(),
                "First manual retry failed.",
                "Controlled retry failure."
        );

        assertThrows(
                IllegalStateException.class,
                () -> dispatchAttemptService.startManualRetry(
                        failedAutomaticAttempt.getId(),
                        monitoringRun.getId()
                )
        );

        assertEquals(
                2,
                dispatchAttemptRepository
                        .countByMonitoringRunId(
                                monitoringRun.getId()
                        )
        );
    }

    @Test
    void startManualRetryRejectsAttemptOwnedByDifferentMonitoringRun() {
        Website website =
                persistWebsite();

        MonitoringRun ownerRun =
                persistCompletedRun(
                        website
                );

        MonitoringRun differentRun =
                persistCompletedRun(
                        website
                );

        MonitoringRunPdfArtifact artifact =
                persistArtifact(
                        ownerRun,
                        "manual-retry-ownership-test"
                );

        MonitoringRunReportDispatchAttempt automaticAttempt =
                dispatchAttemptService
                        .startAutomaticAttempt(
                                ownerRun.getId(),
                                artifact.getId()
                        );

        MonitoringRunReportDispatchAttempt failedAttempt =
                dispatchAttemptService.markFailed(
                        automaticAttempt.getId(),
                        ownerRun.getId(),
                        "Automatic dispatch failed.",
                        "Controlled ownership test failure."
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> dispatchAttemptService.startManualRetry(
                        failedAttempt.getId(),
                        differentRun.getId()
                )
        );

        assertEquals(
                1,
                dispatchAttemptRepository
                        .countByMonitoringRunId(
                                ownerRun.getId()
                        )
        );
    }

    private Website persistWebsite() {
        String uniqueValue =
                UUID.randomUUID().toString();

        return websiteRepository.saveAndFlush(
                new Website(
                        "Report Dispatch Service Test",
                        "report-dispatch-service-"
                                + uniqueValue
                                + ".example.test"
                )
        );
    }

    private MonitoringRun persistCompletedRun(
            Website website
    ) {
        MonitoringRun monitoringRun =
                new MonitoringRun(
                        website.getId()
                );

        monitoringRun.markRunning();
        monitoringRun.markCompleted();

        return monitoringRunRepository.saveAndFlush(
                monitoringRun
        );
    }

    private MonitoringRunPdfArtifact persistArtifact(
            MonitoringRun monitoringRun,
            String content
    ) {
        byte[] artifactBytes =
                pdfBytes(
                        content
                );

        MonitoringRunPdfArtifact artifact =
                MonitoringRunPdfArtifact.create(
                        monitoringRun.getId(),
                        MonitoringRunPdfVersion.V1,
                        "monitoring-run-"
                                + monitoringRun.getId()
                                + ".pdf",
                        artifactBytes,
                        calculateSha256Fingerprint(
                                artifactBytes
                        ),
                        java.time.OffsetDateTime.now(
                                java.time.ZoneOffset.UTC
                        )
                );

        return pdfArtifactService.saveValidated(
                artifact
        );
    }

    private byte[] pdfBytes(
            String content
    ) {
        return (
                "%PDF-1.7\n"
                        + content
                        + "\n%%EOF"
        ).getBytes(
                StandardCharsets.UTF_8
        );
    }

    private String calculateSha256Fingerprint(
            byte[] artifactBytes
    ) {
        try {
            MessageDigest messageDigest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );

            return HexFormat.of().formatHex(
                    messageDigest.digest(
                            artifactBytes
                    )
            );
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 algorithm is unavailable.",
                    exception
            );
        }
    }
}