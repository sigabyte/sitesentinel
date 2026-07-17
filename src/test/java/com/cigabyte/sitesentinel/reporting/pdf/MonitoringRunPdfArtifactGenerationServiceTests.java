package com.cigabyte.sitesentinel.reporting.pdf;

import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
import com.cigabyte.sitesentinel.reporting.MonitoringRunReportService;
import com.cigabyte.sitesentinel.reporting.MonitoringRunReportView;
import com.cigabyte.sitesentinel.website.Website;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneOffset;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class MonitoringRunPdfArtifactGenerationServiceTests {

    private static final int MAX_ARTIFACT_SIZE_BYTES =
            10 * 1024 * 1024;

    private final MonitoringRunReportService
            reportService =
            mock(MonitoringRunReportService.class);

    private final MonitoringRunPdfRenderer
            pdfRenderer =
            mock(MonitoringRunPdfRenderer.class);

    private final MonitoringRunPdfFileNameFactory
            fileNameFactory =
            new MonitoringRunPdfFileNameFactory();

    private final MonitoringRunPdfArtifactService
            artifactService =
            mock(MonitoringRunPdfArtifactService.class);

    private final MonitoringRunPdfArtifactGenerationService
            generationService =
            new MonitoringRunPdfArtifactGenerationService(
                    reportService,
                    pdfRenderer,
                    fileNameFactory,
                    artifactService
            );

    @Test
    void generatesIntegrityMetadataAndPersistsArtifact() {
        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        MonitoringRunReportView reportView =
                completedReportView(
                        websiteId,
                        monitoringRunId
                );

        byte[] renderedBytes =
                pdfBytes(
                        "generated monitoring report"
                );

        when(
                reportService.buildReport(
                        websiteId,
                        monitoringRunId
                )
        ).thenReturn(reportView);

        when(
                pdfRenderer.getReportVersion()
        ).thenReturn(
                MonitoringRunPdfVersion.V1
        );

        when(
                artifactService
                        .findByMonitoringRunIdAndReportVersion(
                                monitoringRunId,
                                MonitoringRunPdfVersion.V1
                        )
        ).thenReturn(Optional.empty());

        when(
                pdfRenderer.render(reportView)
        ).thenReturn(renderedBytes);

        when(
                artifactService.saveValidated(
                        any(MonitoringRunPdfArtifact.class)
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        MonitoringRunPdfArtifact generatedArtifact =
                generationService.generate(
                        websiteId,
                        monitoringRunId
                );

        assertEquals(
                monitoringRunId,
                generatedArtifact.getMonitoringRunId()
        );

        assertEquals(
                MonitoringRunPdfVersion.V1.getValue(),
                generatedArtifact.getReportVersion()
        );

        assertEquals(
                "sitesentinel-monitoring-run-"
                        + monitoringRunId
                        + "-v1.pdf",
                generatedArtifact.getFileName()
        );

        assertEquals(
                "application/pdf",
                generatedArtifact.getContentType()
        );

        assertArrayEquals(
                renderedBytes,
                generatedArtifact.getArtifactBytes()
        );

        assertEquals(
                (long) renderedBytes.length,
                generatedArtifact.getSizeBytes()
        );

        assertEquals(
                calculateSha256Fingerprint(
                        renderedBytes
                ),
                generatedArtifact.getSha256Fingerprint()
        );

        assertNotNull(
                generatedArtifact.getGeneratedAt()
        );

        assertEquals(
                ZoneOffset.UTC,
                generatedArtifact
                        .getGeneratedAt()
                        .getOffset()
        );

        verify(
                reportService
        ).buildReport(
                websiteId,
                monitoringRunId
        );

        verify(
                pdfRenderer
        ).getReportVersion();

        verify(
                artifactService
        ).findByMonitoringRunIdAndReportVersion(
                monitoringRunId,
                MonitoringRunPdfVersion.V1
        );

        verify(
                pdfRenderer
        ).render(reportView);

        verify(
                artifactService
        ).saveValidated(
                generatedArtifact
        );
    }

    @Test
    void rejectsMissingIdentifiersBeforeBuildingReport() {
        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        assertThrows(
                IllegalArgumentException.class,
                () -> generationService.generate(
                        null,
                        monitoringRunId
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> generationService.generate(
                        websiteId,
                        null
                )
        );

        verifyNoInteractions(
                reportService,
                pdfRenderer,
                artifactService
        );
    }

    @Test
    void rejectsNullReportViewBeforeRendering() {
        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        when(
                reportService.buildReport(
                        websiteId,
                        monitoringRunId
                )
        ).thenReturn(null);

        assertThrows(
                NullPointerException.class,
                () -> generationService.generate(
                        websiteId,
                        monitoringRunId
                )
        );

        verifyNoInteractions(
                pdfRenderer,
                artifactService
        );
    }

    @Test
    void rejectsReportForDifferentMonitoringRun() {
        UUID websiteId =
                UUID.randomUUID();

        UUID requestedMonitoringRunId =
                UUID.randomUUID();

        UUID returnedMonitoringRunId =
                UUID.randomUUID();

        MonitoringRunReportView mismatchedReport =
                completedReportView(
                        websiteId,
                        returnedMonitoringRunId
                );

        when(
                reportService.buildReport(
                        websiteId,
                        requestedMonitoringRunId
                )
        ).thenReturn(mismatchedReport);

        assertThrows(
                IllegalStateException.class,
                () -> generationService.generate(
                        websiteId,
                        requestedMonitoringRunId
                )
        );

        verifyNoInteractions(
                pdfRenderer,
                artifactService
        );
    }

    @Test
    void duplicateArtifactIsRejectedBeforeRendering() {
        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        MonitoringRunReportView reportView =
                completedReportView(
                        websiteId,
                        monitoringRunId
                );

        when(
                reportService.buildReport(
                        websiteId,
                        monitoringRunId
                )
        ).thenReturn(reportView);

        when(
                pdfRenderer.getReportVersion()
        ).thenReturn(
                MonitoringRunPdfVersion.V1
        );

        when(
                artifactService
                        .findByMonitoringRunIdAndReportVersion(
                                monitoringRunId,
                                MonitoringRunPdfVersion.V1
                        )
        ).thenReturn(
                Optional.of(
                        mock(
                                MonitoringRunPdfArtifact.class
                        )
                )
        );

        assertThrows(
                IllegalStateException.class,
                () -> generationService.generate(
                        websiteId,
                        monitoringRunId
                )
        );

        verify(
                pdfRenderer,
                never()
        ).render(
                any(MonitoringRunReportView.class)
        );

        verify(
                artifactService,
                never()
        ).saveValidated(
                any(MonitoringRunPdfArtifact.class)
        );
    }

    @Test
    void rendererFailureDoesNotPersistArtifact() {
        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        MonitoringRunReportView reportView =
                completedReportView(
                        websiteId,
                        monitoringRunId
                );

        prepareGenerationUntilRendering(
                websiteId,
                monitoringRunId,
                reportView
        );

        when(
                pdfRenderer.render(reportView)
        ).thenThrow(
                new IllegalStateException(
                        "isolated-renderer-failure"
                )
        );

        assertThrows(
                IllegalStateException.class,
                () -> generationService.generate(
                        websiteId,
                        monitoringRunId
                )
        );

        verify(
                artifactService,
                never()
        ).saveValidated(
                any(MonitoringRunPdfArtifact.class)
        );
    }

    @Test
    void emptyRendererOutputDoesNotPersistArtifact() {
        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        MonitoringRunReportView reportView =
                completedReportView(
                        websiteId,
                        monitoringRunId
                );

        prepareGenerationUntilRendering(
                websiteId,
                monitoringRunId,
                reportView
        );

        when(
                pdfRenderer.render(reportView)
        ).thenReturn(
                new byte[0]
        );

        assertThrows(
                IllegalStateException.class,
                () -> generationService.generate(
                        websiteId,
                        monitoringRunId
                )
        );

        verify(
                artifactService,
                never()
        ).saveValidated(
                any(MonitoringRunPdfArtifact.class)
        );
    }

    @Test
    void oversizedRendererOutputDoesNotPersistArtifact() {
        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        MonitoringRunReportView reportView =
                completedReportView(
                        websiteId,
                        monitoringRunId
                );

        prepareGenerationUntilRendering(
                websiteId,
                monitoringRunId,
                reportView
        );

        byte[] oversizedOutput =
                new byte[
                        MAX_ARTIFACT_SIZE_BYTES + 1
                        ];

        byte[] pdfHeader =
                "%PDF-".getBytes(
                        StandardCharsets.US_ASCII
                );

        System.arraycopy(
                pdfHeader,
                0,
                oversizedOutput,
                0,
                pdfHeader.length
        );

        when(
                pdfRenderer.render(reportView)
        ).thenReturn(oversizedOutput);

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> generationService.generate(
                                websiteId,
                                monitoringRunId
                        )
                );

        assertTrue(
                exception.getMessage()
                        .contains(
                                "maximum allowed size"
                        )
        );

        verify(
                artifactService,
                never()
        ).saveValidated(
                any(MonitoringRunPdfArtifact.class)
        );
    }

    private void prepareGenerationUntilRendering(
            UUID websiteId,
            UUID monitoringRunId,
            MonitoringRunReportView reportView
    ) {
        when(
                reportService.buildReport(
                        websiteId,
                        monitoringRunId
                )
        ).thenReturn(reportView);

        when(
                pdfRenderer.getReportVersion()
        ).thenReturn(
                MonitoringRunPdfVersion.V1
        );

        when(
                artifactService
                        .findByMonitoringRunIdAndReportVersion(
                                monitoringRunId,
                                MonitoringRunPdfVersion.V1
                        )
        ).thenReturn(Optional.empty());
    }

    private MonitoringRunReportView completedReportView(
            UUID websiteId,
            UUID monitoringRunId
    ) {
        Website website =
                new Website(
                        "PDF Generation Test Website",
                        "pdf-generation.example.test"
                );

        ReflectionTestUtils.setField(
                website,
                "id",
                websiteId
        );

        MonitoringRun monitoringRun =
                new MonitoringRun(
                        websiteId
                );

        ReflectionTestUtils.setField(
                monitoringRun,
                "id",
                monitoringRunId
        );

        monitoringRun.markRunning();
        monitoringRun.markCompleted();

        return new MonitoringRunReportView(
                website,
                monitoringRun,
                null,
                null,
                null,
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                null
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