package com.cigabyte.sitesentinel.reporting.pdf;

import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
import com.cigabyte.sitesentinel.monitoring.MonitoringRunRepository;
import com.cigabyte.sitesentinel.website.Website;
import com.cigabyte.sitesentinel.website.WebsiteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class MonitoringRunPdfArtifactRepositoryTests {

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

    private final MonitoringRunPdfArtifactRepository
            artifactRepository;

    private final MonitoringRunPdfArtifactService
            artifactService;

    @Autowired
    MonitoringRunPdfArtifactRepositoryTests(
            WebsiteRepository websiteRepository,
            MonitoringRunRepository monitoringRunRepository,
            MonitoringRunPdfArtifactRepository
                    artifactRepository,
            MonitoringRunPdfArtifactService artifactService
    ) {
        this.websiteRepository = websiteRepository;
        this.monitoringRunRepository =
                monitoringRunRepository;
        this.artifactRepository = artifactRepository;
        this.artifactService = artifactService;
    }

    @Test
    void persistenceServiceSavesCompleteArtifactAndSupportsVersionLookup() {
        Website website = persistWebsite();

        MonitoringRun monitoringRun =
                persistCompletedRun(website);

        byte[] artifactBytes =
                pdfBytes("complete-artifact");

        MonitoringRunPdfArtifact artifact =
                createArtifact(
                        monitoringRun.getId(),
                        artifactBytes,
                        FUTURE_BASE_TIME
                );

        MonitoringRunPdfArtifact saved =
                artifactService.saveValidated(
                        artifact
                );

        assertNotNull(saved.getId());

        assertEquals(
                monitoringRun.getId(),
                saved.getMonitoringRunId()
        );

        assertEquals(
                MonitoringRunPdfVersion.V1.getValue(),
                saved.getReportVersion()
        );

        assertEquals(
                expectedFileName(monitoringRun.getId()),
                saved.getFileName()
        );

        assertEquals(
                "application/pdf",
                saved.getContentType()
        );

        assertArrayEquals(
                artifactBytes,
                saved.getArtifactBytes()
        );

        assertEquals(
                (long) artifactBytes.length,
                saved.getSizeBytes()
        );

        assertEquals(
                calculateSha256Fingerprint(artifactBytes),
                saved.getSha256Fingerprint()
        );

        assertEquals(
                FUTURE_BASE_TIME,
                saved.getGeneratedAt()
        );

        assertNotNull(saved.getCreatedAt());

        MonitoringRunPdfArtifact found =
                artifactService
                        .findByMonitoringRunIdAndReportVersion(
                                monitoringRun.getId(),
                                MonitoringRunPdfVersion.V1
                        )
                        .orElseThrow();

        assertEquals(
                saved.getId(),
                found.getId()
        );
    }

    @Test
    void repositoryFiltersArtifactsByMonitoringRunAndCountsCorrectly() {
        Website website = persistWebsite();

        MonitoringRun firstRun =
                persistCompletedRun(website);

        MonitoringRun secondRun =
                persistCompletedRun(website);

        MonitoringRunPdfArtifact firstArtifact =
                artifactService.saveValidated(
                        createArtifact(
                                firstRun.getId(),
                                pdfBytes("first-run-artifact"),
                                FUTURE_BASE_TIME.plusMinutes(1)
                        )
                );

        MonitoringRunPdfArtifact secondArtifact =
                artifactService.saveValidated(
                        createArtifact(
                                secondRun.getId(),
                                pdfBytes("second-run-artifact"),
                                FUTURE_BASE_TIME.plusMinutes(2)
                        )
                );

        List<MonitoringRunPdfArtifact> firstRunArtifacts =
                artifactRepository
                        .findByMonitoringRunIdOrderByGeneratedAtDescCreatedAtDesc(
                                firstRun.getId()
                        );

        assertEquals(
                1,
                firstRunArtifacts.size()
        );

        assertEquals(
                firstArtifact.getId(),
                firstRunArtifacts.get(0).getId()
        );

        assertTrue(
                firstRunArtifacts.stream()
                        .noneMatch(
                                artifact ->
                                        secondArtifact.getId()
                                                .equals(
                                                        artifact.getId()
                                                )
                        )
        );

        assertEquals(
                1,
                artifactRepository.countByMonitoringRunId(
                        firstRun.getId()
                )
        );

        assertEquals(
                1,
                artifactRepository.countByMonitoringRunId(
                        secondRun.getId()
                )
        );
    }

    @Test
    void persistenceServiceRejectsArtifactForPendingMonitoringRun() {
        Website website = persistWebsite();

        MonitoringRun pendingRun =
                persistPendingRun(website);

        byte[] artifactBytes =
                pdfBytes("pending-run-artifact");

        MonitoringRunPdfArtifact artifact =
                createArtifact(
                        pendingRun.getId(),
                        artifactBytes,
                        FUTURE_BASE_TIME
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> artifactService.saveValidated(
                        artifact
                )
        );

        assertEquals(
                0,
                artifactRepository.countByMonitoringRunId(
                        pendingRun.getId()
                )
        );
    }

    @Test
    void persistenceServiceRejectsUnknownMonitoringRun() {
        UUID unknownMonitoringRunId =
                UUID.randomUUID();

        byte[] artifactBytes =
                pdfBytes("unknown-run-artifact");

        MonitoringRunPdfArtifact artifact =
                createArtifact(
                        unknownMonitoringRunId,
                        artifactBytes,
                        FUTURE_BASE_TIME
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> artifactService.saveValidated(
                        artifact
                )
        );

        assertEquals(
                0,
                artifactRepository.countByMonitoringRunId(
                        unknownMonitoringRunId
                )
        );
    }

    @Test
    void persistenceServiceRejectsFingerprintThatDoesNotMatchBinaryContent() {
        Website website = persistWebsite();

        MonitoringRun monitoringRun =
                persistCompletedRun(website);

        byte[] artifactBytes =
                pdfBytes("fingerprint-mismatch");

        MonitoringRunPdfArtifact artifact =
                MonitoringRunPdfArtifact.create(
                        monitoringRun.getId(),
                        MonitoringRunPdfVersion.V1,
                        expectedFileName(
                                monitoringRun.getId()
                        ),
                        artifactBytes,
                        "a".repeat(64),
                        FUTURE_BASE_TIME
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> artifactService.saveValidated(
                        artifact
                )
        );

        assertEquals(
                0,
                artifactRepository.countByMonitoringRunId(
                        monitoringRun.getId()
                )
        );
    }

    @Test
    void persistenceServiceRejectsDuplicateRunAndReportVersion() {
        Website website = persistWebsite();

        MonitoringRun monitoringRun =
                persistCompletedRun(website);

        byte[] firstArtifactBytes =
                pdfBytes("first-version-artifact");

        artifactService.saveValidated(
                createArtifact(
                        monitoringRun.getId(),
                        firstArtifactBytes,
                        FUTURE_BASE_TIME
                )
        );

        byte[] duplicateArtifactBytes =
                pdfBytes("duplicate-version-artifact");

        MonitoringRunPdfArtifact duplicateArtifact =
                createArtifact(
                        monitoringRun.getId(),
                        duplicateArtifactBytes,
                        FUTURE_BASE_TIME.plusMinutes(1)
                );

        assertThrows(
                IllegalStateException.class,
                () -> artifactService.saveValidated(
                        duplicateArtifact
                )
        );

        assertEquals(
                1,
                artifactRepository.countByMonitoringRunId(
                        monitoringRun.getId()
                )
        );
    }

    @Test
    void ownershipSafeRetrievalRejectsArtifactFromDifferentMonitoringRun() {
        Website website = persistWebsite();

        MonitoringRun artifactMonitoringRun =
                persistCompletedRun(website);

        MonitoringRun requestedMonitoringRun =
                persistCompletedRun(website);

        MonitoringRunPdfArtifact saved =
                artifactService.saveValidated(
                        createArtifact(
                                artifactMonitoringRun.getId(),
                                pdfBytes("ownership-artifact"),
                                FUTURE_BASE_TIME
                        )
                );

        MonitoringRunPdfArtifact correctlyOwnedArtifact =
                artifactService.findByIdAndMonitoringRunId(
                        saved.getId(),
                        artifactMonitoringRun.getId()
                );

        assertEquals(
                saved.getId(),
                correctlyOwnedArtifact.getId()
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> artifactService
                        .findByIdAndMonitoringRunId(
                                saved.getId(),
                                requestedMonitoringRun.getId()
                        )
        );
    }

    @Test
    void artifactDefensivelyCopiesBinaryContent() {
        UUID monitoringRunId =
                UUID.randomUUID();

        byte[] originalBytes =
                pdfBytes("defensive-copy");

        MonitoringRunPdfArtifact artifact =
                MonitoringRunPdfArtifact.create(
                        monitoringRunId,
                        MonitoringRunPdfVersion.V1,
                        expectedFileName(monitoringRunId),
                        originalBytes,
                        calculateSha256Fingerprint(
                                originalBytes
                        ),
                        FUTURE_BASE_TIME
                );

        byte expectedFirstByte =
                artifact.getArtifactBytes()[0];

        originalBytes[0] =
                (byte) 'X';

        assertEquals(
                expectedFirstByte,
                artifact.getArtifactBytes()[0]
        );

        byte[] returnedBytes =
                artifact.getArtifactBytes();

        returnedBytes[0] =
                (byte) 'Y';

        assertEquals(
                expectedFirstByte,
                artifact.getArtifactBytes()[0]
        );
    }

    private Website persistWebsite() {
        String uniqueValue =
                UUID.randomUUID().toString();

        return websiteRepository.saveAndFlush(
                new Website(
                        "PDF Artifact Repository Test",
                        "pdf-artifact-"
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

    private MonitoringRun persistPendingRun(
            Website website
    ) {
        return monitoringRunRepository.saveAndFlush(
                new MonitoringRun(
                        website.getId()
                )
        );
    }

    private MonitoringRunPdfArtifact createArtifact(
            UUID monitoringRunId,
            byte[] artifactBytes,
            OffsetDateTime generatedAt
    ) {
        return MonitoringRunPdfArtifact.create(
                monitoringRunId,
                MonitoringRunPdfVersion.V1,
                expectedFileName(monitoringRunId),
                artifactBytes,
                calculateSha256Fingerprint(
                        artifactBytes
                ),
                generatedAt
        );
    }

    private String expectedFileName(
            UUID monitoringRunId
    ) {
        return "monitoring-run-"
                + monitoringRunId
                + ".pdf";
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