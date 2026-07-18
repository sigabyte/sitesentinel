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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class MonitoringRunReportDispatchAttemptRepositoryTests {

    private static final OffsetDateTime FUTURE_BASE_TIME =
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

    private final WebsiteRepository websiteRepository;

    private final MonitoringRunRepository
            monitoringRunRepository;

    private final MonitoringRunPdfArtifactService
            artifactService;

    private final MonitoringRunReportDispatchAttemptRepository
            dispatchAttemptRepository;

    @Autowired
    MonitoringRunReportDispatchAttemptRepositoryTests(
            WebsiteRepository websiteRepository,
            MonitoringRunRepository monitoringRunRepository,
            MonitoringRunPdfArtifactService artifactService,
            MonitoringRunReportDispatchAttemptRepository
                    dispatchAttemptRepository
    ) {
        this.websiteRepository =
                websiteRepository;

        this.monitoringRunRepository =
                monitoringRunRepository;

        this.artifactService =
                artifactService;

        this.dispatchAttemptRepository =
                dispatchAttemptRepository;
    }

    @Test
    void repositoryPersistsAutomaticAttemptAndSupportsQueries() {
        MonitoringRun monitoringRun =
                persistCompletedRun(
                        persistWebsite()
                );

        MonitoringRunPdfArtifact artifact =
                persistArtifact(
                        monitoringRun,
                        "automatic-query-test"
                );

        MonitoringRunReportDispatchAttempt attempt =
                MonitoringRunReportDispatchAttempt
                        .automatic(
                                monitoringRun.getId(),
                                artifact.getId(),
                                FUTURE_BASE_TIME
                        );

        MonitoringRunReportDispatchAttempt saved =
                dispatchAttemptRepository.saveAndFlush(
                        attempt
                );

        assertNotNull(
                saved.getId()
        );

        assertNotNull(
                saved.getCreatedAt()
        );

        assertEquals(
                MonitoringRunReportDispatchStatus.PENDING,
                saved.getStatus()
        );

        assertEquals(
                NotificationDeliveryChannel.TELEGRAM,
                saved.getChannel()
        );

        assertTrue(
                dispatchAttemptRepository
                        .findByIdAndMonitoringRunId(
                                saved.getId(),
                                monitoringRun.getId()
                        )
                        .isPresent()
        );

        List<MonitoringRunReportDispatchAttempt>
                runAttempts =
                dispatchAttemptRepository
                        .findByMonitoringRunIdOrderByAttemptedAtDescCreatedAtDesc(
                                monitoringRun.getId()
                        );

        assertEquals(
                1,
                runAttempts.size()
        );

        assertEquals(
                saved.getId(),
                runAttempts.get(0).getId()
        );

        List<MonitoringRunReportDispatchAttempt>
                artifactAttempts =
                dispatchAttemptRepository
                        .findByPdfArtifactIdAndChannelOrderByAttemptNumberAsc(
                                artifact.getId(),
                                NotificationDeliveryChannel.TELEGRAM
                        );

        assertEquals(
                1,
                artifactAttempts.size()
        );

        assertEquals(
                saved.getId(),
                artifactAttempts.get(0).getId()
        );

        assertTrue(
                dispatchAttemptRepository
                        .existsByMonitoringRunIdAndPdfArtifactIdAndChannelAndDispatchType(
                                monitoringRun.getId(),
                                artifact.getId(),
                                NotificationDeliveryChannel.TELEGRAM,
                                MonitoringRunReportDispatchType.AUTOMATIC
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
    void repositoryPersistsFailedAutomaticAttemptAndSuccessfulRetry() {
        MonitoringRun monitoringRun =
                persistCompletedRun(
                        persistWebsite()
                );

        MonitoringRunPdfArtifact artifact =
                persistArtifact(
                        monitoringRun,
                        "retry-lifecycle-test"
                );

        MonitoringRunReportDispatchAttempt automaticAttempt =
                MonitoringRunReportDispatchAttempt
                        .automatic(
                                monitoringRun.getId(),
                                artifact.getId(),
                                FUTURE_BASE_TIME
                        );

        automaticAttempt.markFailed(
                "Automatic Telegram report dispatch failed.",
                "Controlled repository integration-test failure.",
                FUTURE_BASE_TIME.plusSeconds(1)
        );

        MonitoringRunReportDispatchAttempt
                savedAutomaticAttempt =
                dispatchAttemptRepository.saveAndFlush(
                        automaticAttempt
                );

        MonitoringRunReportDispatchAttempt retryAttempt =
                MonitoringRunReportDispatchAttempt
                        .manualRetry(
                                monitoringRun.getId(),
                                artifact.getId(),
                                2,
                                savedAutomaticAttempt.getId(),
                                FUTURE_BASE_TIME.plusSeconds(2)
                        );

        retryAttempt.markSent(
                2468L,
                "Telegram report was sent successfully.",
                "Telegram Bot API accepted the retry.",
                FUTURE_BASE_TIME.plusSeconds(3)
        );

        MonitoringRunReportDispatchAttempt savedRetryAttempt =
                dispatchAttemptRepository.saveAndFlush(
                        retryAttempt
                );

        assertEquals(
                MonitoringRunReportDispatchStatus.FAILED,
                savedAutomaticAttempt.getStatus()
        );

        assertEquals(
                MonitoringRunReportDispatchStatus.SENT,
                savedRetryAttempt.getStatus()
        );

        assertEquals(
                savedAutomaticAttempt.getId(),
                savedRetryAttempt.getRetryOfAttemptId()
        );

        assertEquals(
                Long.valueOf(2468L),
                savedRetryAttempt.getTelegramMessageId()
        );

        List<MonitoringRunReportDispatchAttempt> attempts =
                dispatchAttemptRepository
                        .findByPdfArtifactIdAndChannelOrderByAttemptNumberAsc(
                                artifact.getId(),
                                NotificationDeliveryChannel.TELEGRAM
                        );

        assertEquals(
                2,
                attempts.size()
        );

        assertEquals(
                1,
                attempts.get(0).getAttemptNumber()
        );

        assertEquals(
                2,
                attempts.get(1).getAttemptNumber()
        );

        MonitoringRunReportDispatchAttempt latestAttempt =
                dispatchAttemptRepository
                        .findFirstByPdfArtifactIdAndChannelOrderByAttemptNumberDescCreatedAtDesc(
                                artifact.getId(),
                                NotificationDeliveryChannel.TELEGRAM
                        )
                        .orElseThrow();

        assertEquals(
                savedRetryAttempt.getId(),
                latestAttempt.getId()
        );
    }

    @Test
    void databaseRejectsDuplicateAutomaticAttemptForSameArtifact() {
        MonitoringRun monitoringRun =
                persistCompletedRun(
                        persistWebsite()
                );

        MonitoringRunPdfArtifact artifact =
                persistArtifact(
                        monitoringRun,
                        "duplicate-automatic-test"
                );

        dispatchAttemptRepository.saveAndFlush(
                MonitoringRunReportDispatchAttempt
                        .automatic(
                                monitoringRun.getId(),
                                artifact.getId(),
                                FUTURE_BASE_TIME
                        )
        );

        MonitoringRunReportDispatchAttempt duplicateAttempt =
                MonitoringRunReportDispatchAttempt
                        .automatic(
                                monitoringRun.getId(),
                                artifact.getId(),
                                FUTURE_BASE_TIME.plusSeconds(1)
                        );

        assertThrows(
                DataIntegrityViolationException.class,
                () -> dispatchAttemptRepository.saveAndFlush(
                        duplicateAttempt
                )
        );
    }

    @Test
    void databaseRejectsArtifactOwnedByDifferentMonitoringRun() {
        Website website =
                persistWebsite();

        MonitoringRun artifactOwnerRun =
                persistCompletedRun(
                        website
                );

        MonitoringRun differentRun =
                persistCompletedRun(
                        website
                );

        MonitoringRunPdfArtifact artifact =
                persistArtifact(
                        artifactOwnerRun,
                        "ownership-boundary-test"
                );

        MonitoringRunReportDispatchAttempt invalidAttempt =
                MonitoringRunReportDispatchAttempt
                        .automatic(
                                differentRun.getId(),
                                artifact.getId(),
                                FUTURE_BASE_TIME
                        );

        assertThrows(
                DataIntegrityViolationException.class,
                () -> dispatchAttemptRepository.saveAndFlush(
                        invalidAttempt
                )
        );
    }

    @Test
    void databaseRejectsRetryLinkedToDifferentArtifact() {
        Website website =
                persistWebsite();

        MonitoringRun firstRun =
                persistCompletedRun(
                        website
                );

        MonitoringRun secondRun =
                persistCompletedRun(
                        website
                );

        MonitoringRunPdfArtifact firstArtifact =
                persistArtifact(
                        firstRun,
                        "first-retry-lineage-artifact"
                );

        MonitoringRunPdfArtifact secondArtifact =
                persistArtifact(
                        secondRun,
                        "second-retry-lineage-artifact"
                );

        MonitoringRunReportDispatchAttempt
                firstAutomaticAttempt =
                MonitoringRunReportDispatchAttempt
                        .automatic(
                                firstRun.getId(),
                                firstArtifact.getId(),
                                FUTURE_BASE_TIME
                        );

        firstAutomaticAttempt.markFailed(
                "First automatic dispatch failed.",
                "Controlled retry-lineage setup.",
                FUTURE_BASE_TIME.plusSeconds(1)
        );

        MonitoringRunReportDispatchAttempt
                savedFirstAttempt =
                dispatchAttemptRepository.saveAndFlush(
                        firstAutomaticAttempt
                );

        MonitoringRunReportDispatchAttempt invalidRetry =
                MonitoringRunReportDispatchAttempt
                        .manualRetry(
                                secondRun.getId(),
                                secondArtifact.getId(),
                                2,
                                savedFirstAttempt.getId(),
                                FUTURE_BASE_TIME.plusSeconds(2)
                        );

        assertThrows(
                DataIntegrityViolationException.class,
                () -> dispatchAttemptRepository.saveAndFlush(
                        invalidRetry
                )
        );
    }

    private Website persistWebsite() {
        String uniqueValue =
                UUID.randomUUID().toString();

        return websiteRepository.saveAndFlush(
                new Website(
                        "Report Dispatch Repository Test",
                        "report-dispatch-"
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
                        FUTURE_BASE_TIME.minusMinutes(1)
                );

        return artifactService.saveValidated(
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