package com.cigabyte.sitesentinel.reporting.pdf;

import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
import com.cigabyte.sitesentinel.monitoring.MonitoringRunRepository;
import com.cigabyte.sitesentinel.website.Website;
import com.cigabyte.sitesentinel.website.WebsiteRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class MonitoringRunPdfArtifactGenerationIntegrationTests {

    private final WebsiteRepository websiteRepository;

    private final MonitoringRunRepository
            monitoringRunRepository;

    private final MonitoringRunPdfArtifactRepository
            artifactRepository;

    private final MonitoringRunPdfArtifactGenerationService
            generationService;

    @Autowired
    MonitoringRunPdfArtifactGenerationIntegrationTests(
            WebsiteRepository websiteRepository,
            MonitoringRunRepository monitoringRunRepository,
            MonitoringRunPdfArtifactRepository
                    artifactRepository,
            MonitoringRunPdfArtifactGenerationService
                    generationService
    ) {
        this.websiteRepository = websiteRepository;
        this.monitoringRunRepository =
                monitoringRunRepository;
        this.artifactRepository = artifactRepository;
        this.generationService = generationService;
    }

    @Test
    void completedRunGeneratesAndPersistsParseablePdf()
            throws IOException {

        Website website =
                persistWebsite(
                        "End-to-End PDF Website"
                );

        MonitoringRun monitoringRun =
                persistCompletedRun(
                        website
                );

        MonitoringRunPdfArtifact artifact =
                generationService.generate(
                        website.getId(),
                        monitoringRun.getId()
                );

        assertNotNull(
                artifact.getId()
        );

        assertEquals(
                monitoringRun.getId(),
                artifact.getMonitoringRunId()
        );

        assertEquals(
                MonitoringRunPdfVersion.V1.getValue(),
                artifact.getReportVersion()
        );

        assertEquals(
                "sitesentinel-monitoring-run-"
                        + monitoringRun.getId()
                        + "-v1.pdf",
                artifact.getFileName()
        );

        assertEquals(
                "application/pdf",
                artifact.getContentType()
        );

        assertTrue(
                artifact.getSizeBytes() > 0
        );

        assertEquals(
                (long) artifact.getArtifactBytes().length,
                artifact.getSizeBytes()
        );

        assertEquals(
                calculateSha256Fingerprint(
                        artifact.getArtifactBytes()
                ),
                artifact.getSha256Fingerprint()
        );

        assertNotNull(
                artifact.getGeneratedAt()
        );

        assertNotNull(
                artifact.getCreatedAt()
        );

        assertArrayEquals(
                "%PDF-".getBytes(
                        StandardCharsets.US_ASCII
                ),
                Arrays.copyOf(
                        artifact.getArtifactBytes(),
                        5
                )
        );

        assertEquals(
                1,
                artifactRepository.countByMonitoringRunId(
                        monitoringRun.getId()
                )
        );

        MonitoringRunPdfArtifact persistedArtifact =
                artifactRepository
                        .findByMonitoringRunIdAndReportVersion(
                                monitoringRun.getId(),
                                MonitoringRunPdfVersion.V1
                                        .getValue()
                        )
                        .orElseThrow();

        assertEquals(
                artifact.getId(),
                persistedArtifact.getId()
        );

        assertEquals(
                artifact.getSha256Fingerprint(),
                persistedArtifact
                        .getSha256Fingerprint()
        );

        try (
                PDDocument document =
                        Loader.loadPDF(
                                persistedArtifact
                                        .getArtifactBytes()
                        )
        ) {
            assertTrue(
                    document.getNumberOfPages() >= 1
            );

            String extractedText =
                    new PDFTextStripper()
                            .getText(document);

            assertTrue(
                    extractedText.contains(
                            "Monitoring Run Report"
                    )
            );

            assertTrue(
                    extractedText.contains(
                            "End-to-End PDF Website"
                    )
            );

            assertTrue(
                    extractedText.contains(
                            "Monitoring Run"
                    )
            );

            assertTrue(
                    extractedText.contains(
                            monitoringRun.getId()
                                    .toString()
                    )
            );

            assertTrue(
                    extractedText.contains(
                            "Lifecycle Output Counts"
                    )
            );

            assertTrue(
                    extractedText.contains(
                            "Report Boundary"
                    )
            );

            assertTrue(
                    extractedText.contains(
                            "SiteSentinel | Page 1"
                    )
            );
        }
    }

    @Test
    void duplicateGenerationIsRejectedAndSingleArtifactRemains() {
        Website website =
                persistWebsite(
                        "Duplicate PDF Website"
                );

        MonitoringRun monitoringRun =
                persistCompletedRun(
                        website
                );

        MonitoringRunPdfArtifact firstArtifact =
                generationService.generate(
                        website.getId(),
                        monitoringRun.getId()
                );

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> generationService.generate(
                                website.getId(),
                                monitoringRun.getId()
                        )
                );

        assertTrue(
                exception.getMessage()
                        .contains(
                                "already exists"
                        )
        );

        assertEquals(
                1,
                artifactRepository.countByMonitoringRunId(
                        monitoringRun.getId()
                )
        );

        MonitoringRunPdfArtifact persistedArtifact =
                artifactRepository
                        .findByMonitoringRunIdAndReportVersion(
                                monitoringRun.getId(),
                                MonitoringRunPdfVersion.V1
                                        .getValue()
                        )
                        .orElseThrow();

        assertEquals(
                firstArtifact.getId(),
                persistedArtifact.getId()
        );
    }

    @Test
    void pendingRunDoesNotGenerateOrPersistArtifact() {
        Website website =
                persistWebsite(
                        "Pending PDF Website"
                );

        MonitoringRun pendingRun =
                persistPendingRun(
                        website
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> generationService.generate(
                        website.getId(),
                        pendingRun.getId()
                )
        );

        assertEquals(
                0,
                artifactRepository.countByMonitoringRunId(
                        pendingRun.getId()
                )
        );

        assertTrue(
                artifactRepository
                        .findByMonitoringRunIdAndReportVersion(
                                pendingRun.getId(),
                                MonitoringRunPdfVersion.V1
                                        .getValue()
                        )
                        .isEmpty()
        );
    }

    private Website persistWebsite(
            String name
    ) {
        String uniqueDomain =
                "pdf-integration-"
                        + UUID.randomUUID()
                        + ".example.test";

        return websiteRepository.saveAndFlush(
                new Website(
                        name,
                        uniqueDomain
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