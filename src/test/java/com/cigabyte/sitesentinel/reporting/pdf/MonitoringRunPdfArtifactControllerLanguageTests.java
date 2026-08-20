package com.cigabyte.sitesentinel.reporting.pdf;

import com.cigabyte.sitesentinel.monitoring.MonitoringRunService;
import com.cigabyte.sitesentinel.reporting.SiteSentinelReportLanguage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MonitoringRunPdfArtifactControllerLanguageTests {

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
    void turkishGenerationRequestDelegatesWithTurkishLanguage()
            throws Exception {

        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        UUID artifactId =
                UUID.randomUUID();

        MonitoringRunPdfArtifact artifact =
                mock(MonitoringRunPdfArtifact.class);

        when(
                artifact.getId()
        ).thenReturn(artifactId);

        when(
                generationService.generate(
                        websiteId,
                        monitoringRunId,
                        SiteSentinelReportLanguage.TURKISH
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
                                .param(
                                        "reportLanguage",
                                        "TURKISH"
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
                                "generatedPdfArtifactId",
                                artifactId
                        )
                );

        verify(
                generationService
        ).generate(
                websiteId,
                monitoringRunId,
                SiteSentinelReportLanguage.TURKISH
        );
    }
}