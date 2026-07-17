package com.cigabyte.sitesentinel.reporting.pdf;

import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
import com.cigabyte.sitesentinel.monitoring.MonitoringRunService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HexFormat;
import java.util.UUID;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MonitoringRunPdfArtifactControllerTests {

    private static final OffsetDateTime GENERATED_AT =
            OffsetDateTime.of(
                    2026,
                    7,
                    17,
                    12,
                    0,
                    0,
                    0,
                    ZoneOffset.UTC
            );

    private MonitoringRunPdfArtifactGenerationService
            generationService;

    private MonitoringRunPdfArtifactService
            artifactService;

    private MonitoringRunService
            monitoringRunService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        generationService =
                mock(
                        MonitoringRunPdfArtifactGenerationService.class
                );

        artifactService =
                mock(
                        MonitoringRunPdfArtifactService.class
                );

        monitoringRunService =
                mock(
                        MonitoringRunService.class
                );

        MonitoringRunPdfArtifactController controller =
                new MonitoringRunPdfArtifactController(
                        generationService,
                        artifactService,
                        monitoringRunService
                );

        mockMvc =
                MockMvcBuilders
                        .standaloneSetup(controller)
                        .build();
    }

    @Test
    void successfulGenerationRedirectsWithSuccessFlash()
            throws Exception {

        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        UUID artifactId =
                UUID.randomUUID();

        MonitoringRunPdfArtifact artifact =
                createArtifact(
                        monitoringRunId,
                        artifactId,
                        pdfBytes("generated-report")
                );

        when(
                generationService.generate(
                        websiteId,
                        monitoringRunId
                )
        ).thenReturn(artifact);

        mockMvc.perform(
                        post(
                                "/websites/{websiteId}"
                                        + "/monitoring-runs/{runId}"
                                        + "/report/pdf-artifacts",
                                websiteId,
                                monitoringRunId
                        )
                )
                .andExpect(
                        status().is3xxRedirection()
                )
                .andExpect(
                        redirectedUrl(
                                "/websites/"
                                        + websiteId
                                        + "/monitoring-runs/"
                                        + monitoringRunId
                                        + "/report"
                        )
                )
                .andExpect(
                        flash().attribute(
                                "pdfArtifactStatus",
                                "SUCCESS"
                        )
                )
                .andExpect(
                        flash().attribute(
                                "pdfArtifactMessage",
                                "PDF artifact generated successfully."
                        )
                )
                .andExpect(
                        flash().attribute(
                                "generatedPdfArtifactId",
                                artifactId
                        )
                );

        verify(
                generationService
        ).generate(
                websiteId,
                monitoringRunId
        );

        verifyNoInteractions(
                artifactService,
                monitoringRunService
        );
    }

    @Test
    void generationFailureRedirectsWithSafeFailureFlash()
            throws Exception {

        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        when(
                generationService.generate(
                        websiteId,
                        monitoringRunId
                )
        ).thenThrow(
                new IllegalStateException(
                        "internal-renderer-detail"
                )
        );

        mockMvc.perform(
                        post(
                                "/websites/{websiteId}"
                                        + "/monitoring-runs/{runId}"
                                        + "/report/pdf-artifacts",
                                websiteId,
                                monitoringRunId
                        )
                )
                .andExpect(
                        status().is3xxRedirection()
                )
                .andExpect(
                        redirectedUrl(
                                "/websites/"
                                        + websiteId
                                        + "/monitoring-runs/"
                                        + monitoringRunId
                                        + "/report"
                        )
                )
                .andExpect(
                        flash().attribute(
                                "pdfArtifactStatus",
                                "FAILURE"
                        )
                )
                .andExpect(
                        flash().attribute(
                                "pdfArtifactMessage",
                                "PDF artifact generation was not completed. "
                                        + "The monitoring run may be incomplete "
                                        + "or a PDF artifact may already exist."
                        )
                );

        verify(
                generationService
        ).generate(
                websiteId,
                monitoringRunId
        );

        verifyNoInteractions(
                artifactService,
                monitoringRunService
        );
    }

    @Test
    void downloadReturnsPdfBodyAndSecurityHeaders()
            throws Exception {

        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        UUID artifactId =
                UUID.randomUUID();

        byte[] artifactBytes =
                pdfBytes(
                        "downloadable-report"
                );

        MonitoringRunPdfArtifact artifact =
                createArtifact(
                        monitoringRunId,
                        artifactId,
                        artifactBytes
                );

        when(
                monitoringRunService
                        .findByIdAndWebsiteId(
                                monitoringRunId,
                                websiteId
                        )
        ).thenReturn(
                mock(MonitoringRun.class)
        );

        when(
                artifactService
                        .findByIdAndMonitoringRunId(
                                artifactId,
                                monitoringRunId
                        )
        ).thenReturn(artifact);

        mockMvc.perform(
                        get(
                                "/websites/{websiteId}"
                                        + "/monitoring-runs/{runId}"
                                        + "/report/pdf-artifacts/{artifactId}"
                                        + "/download",
                                websiteId,
                                monitoringRunId,
                                artifactId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        content().contentType(
                                MediaType.APPLICATION_PDF
                        )
                )
                .andExpect(
                        content().bytes(
                                artifactBytes
                        )
                )
                .andExpect(
                        header().string(
                                HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\""
                                        + artifact.getFileName()
                                        + "\""
                        )
                )
                .andExpect(
                        header().string(
                                HttpHeaders.CONTENT_LENGTH,
                                String.valueOf(
                                        artifactBytes.length
                                )
                        )
                )
                .andExpect(
                        header().string(
                                HttpHeaders.CACHE_CONTROL,
                                "no-store"
                        )
                )
                .andExpect(
                        header().string(
                                "X-Content-Type-Options",
                                "nosniff"
                        )
                );

        verify(
                monitoringRunService
        ).findByIdAndWebsiteId(
                monitoringRunId,
                websiteId
        );

        verify(
                artifactService
        ).findByIdAndMonitoringRunId(
                artifactId,
                monitoringRunId
        );

        verifyNoInteractions(
                generationService
        );
    }

    @Test
    void websiteOwnershipFailureReturnsNotFound()
            throws Exception {

        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        UUID artifactId =
                UUID.randomUUID();

        when(
                monitoringRunService
                        .findByIdAndWebsiteId(
                                monitoringRunId,
                                websiteId
                        )
        ).thenThrow(
                new IllegalArgumentException(
                        "monitoring run ownership mismatch"
                )
        );

        mockMvc.perform(
                        get(
                                "/websites/{websiteId}"
                                        + "/monitoring-runs/{runId}"
                                        + "/report/pdf-artifacts/{artifactId}"
                                        + "/download",
                                websiteId,
                                monitoringRunId,
                                artifactId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );

        verify(
                monitoringRunService
        ).findByIdAndWebsiteId(
                monitoringRunId,
                websiteId
        );

        verify(
                artifactService,
                never()
        ).findByIdAndMonitoringRunId(
                artifactId,
                monitoringRunId
        );

        verifyNoInteractions(
                generationService
        );
    }

    @Test
    void artifactOwnershipFailureReturnsNotFound()
            throws Exception {

        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        UUID artifactId =
                UUID.randomUUID();

        when(
                monitoringRunService
                        .findByIdAndWebsiteId(
                                monitoringRunId,
                                websiteId
                        )
        ).thenReturn(
                mock(MonitoringRun.class)
        );

        when(
                artifactService
                        .findByIdAndMonitoringRunId(
                                artifactId,
                                monitoringRunId
                        )
        ).thenThrow(
                new IllegalArgumentException(
                        "artifact ownership mismatch"
                )
        );

        mockMvc.perform(
                        get(
                                "/websites/{websiteId}"
                                        + "/monitoring-runs/{runId}"
                                        + "/report/pdf-artifacts/{artifactId}"
                                        + "/download",
                                websiteId,
                                monitoringRunId,
                                artifactId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );

        verify(
                monitoringRunService
        ).findByIdAndWebsiteId(
                monitoringRunId,
                websiteId
        );

        verify(
                artifactService
        ).findByIdAndMonitoringRunId(
                artifactId,
                monitoringRunId
        );

        verifyNoInteractions(
                generationService
        );
    }

    @Test
    void downloadChecksRunOwnershipBeforeArtifactOwnership()
            throws Exception {

        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        UUID artifactId =
                UUID.randomUUID();

        MonitoringRun monitoringRun =
                mock(MonitoringRun.class);

        MonitoringRunPdfArtifact artifact =
                createArtifact(
                        monitoringRunId,
                        artifactId,
                        pdfBytes("ownership-order")
                );

        when(
                monitoringRunService
                        .findByIdAndWebsiteId(
                                monitoringRunId,
                                websiteId
                        )
        ).thenReturn(monitoringRun);

        when(
                artifactService
                        .findByIdAndMonitoringRunId(
                                artifactId,
                                monitoringRunId
                        )
        ).thenReturn(artifact);

        mockMvc.perform(
                        get(
                                "/websites/{websiteId}"
                                        + "/monitoring-runs/{runId}"
                                        + "/report/pdf-artifacts/{artifactId}"
                                        + "/download",
                                websiteId,
                                monitoringRunId,
                                artifactId
                        )
                )
                .andExpect(
                        status().isOk()
                );

        var orderedVerification =
                inOrder(
                        monitoringRunService,
                        artifactService
                );

        orderedVerification.verify(
                monitoringRunService
        ).findByIdAndWebsiteId(
                monitoringRunId,
                websiteId
        );

        orderedVerification.verify(
                artifactService
        ).findByIdAndMonitoringRunId(
                artifactId,
                monitoringRunId
        );

        verifyNoInteractions(
                generationService
        );
    }

    private MonitoringRunPdfArtifact createArtifact(
            UUID monitoringRunId,
            UUID artifactId,
            byte[] artifactBytes
    ) {
        MonitoringRunPdfArtifact artifact =
                MonitoringRunPdfArtifact.create(
                        monitoringRunId,
                        MonitoringRunPdfVersion.V1,
                        "sitesentinel-monitoring-run-"
                                + monitoringRunId
                                + "-v1.pdf",
                        artifactBytes,
                        calculateSha256Fingerprint(
                                artifactBytes
                        ),
                        GENERATED_AT
                );

        ReflectionTestUtils.setField(
                artifact,
                "id",
                artifactId
        );

        ReflectionTestUtils.setField(
                artifact,
                "createdAt",
                GENERATED_AT.plusSeconds(1)
        );

        return artifact;
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