package com.cigabyte.sitesentinel.reporting;

import com.cigabyte.sitesentinel.comparison.AssessmentComparisonSummary;
import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
import com.cigabyte.sitesentinel.notification.NotificationEventService;
import com.cigabyte.sitesentinel.reporting.dispatch.MonitoringRunReportDispatchAttemptService;
import com.cigabyte.sitesentinel.reporting.pdf.MonitoringRunPdfArtifact;
import com.cigabyte.sitesentinel.reporting.pdf.MonitoringRunPdfArtifactService;
import com.cigabyte.sitesentinel.reporting.pdf.MonitoringRunPdfVersion;
import com.cigabyte.sitesentinel.website.Website;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MonitoringRunReportControllerLanguageTests {

    private final MonitoringRunReportService
            reportService =
            mock(MonitoringRunReportService.class);

    private final NotificationEventService
            notificationEventService =
            mock(NotificationEventService.class);

    private final MonitoringRunPdfArtifactService
            artifactService =
            mock(MonitoringRunPdfArtifactService.class);

    private final MonitoringRunReportDispatchAttemptService
            dispatchAttemptService =
            mock(
                    MonitoringRunReportDispatchAttemptService.class
            );

    private final MonitoringRunReportController controller =
            new MonitoringRunReportController(
                    reportService,
                    notificationEventService,
                    artifactService,
                    dispatchAttemptService
            );

    @Test
    void reportModelSeparatesEnglishAndTurkishPdfArtifacts() {
        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        MonitoringRunReportView report =
                mock(MonitoringRunReportView.class);

        Website website =
                mock(Website.class);

        MonitoringRun monitoringRun =
                mock(MonitoringRun.class);

        AssessmentComparisonSummary comparison =
                mock(AssessmentComparisonSummary.class);

        MonitoringRunPdfArtifact englishArtifact =
                artifact(
                        SiteSentinelReportLanguage.ENGLISH
                );

        MonitoringRunPdfArtifact turkishArtifact =
                artifact(
                        SiteSentinelReportLanguage.TURKISH
                );

        when(
                reportService.buildReport(
                        websiteId,
                        monitoringRunId
                )
        ).thenReturn(report);

        when(
                report.getWebsite()
        ).thenReturn(website);

        when(
                report.getMonitoringRun()
        ).thenReturn(monitoringRun);

        when(
                report.getComparison()
        ).thenReturn(comparison);

        when(
                report.isCompletedRun()
        ).thenReturn(true);

        when(
                artifactService.findByMonitoringRunId(
                        monitoringRunId
                )
        ).thenReturn(
                List.of(
                        turkishArtifact,
                        englishArtifact
                )
        );

        when(
                notificationEventService
                        .findByMonitoringRunId(
                                monitoringRunId
                        )
        ).thenReturn(List.of());

        when(
                dispatchAttemptService
                        .findAttemptsForMonitoringRun(
                                monitoringRunId
                        )
        ).thenReturn(List.of());

        Model model =
                new ExtendedModelMap();

        String viewName =
                controller.detail(
                        websiteId,
                        monitoringRunId,
                        model
                );

        assertEquals(
                "reports/monitoring-run-report",
                viewName
        );

        assertSame(
                englishArtifact,
                model.getAttribute(
                        "englishPdfArtifact"
                )
        );

        assertSame(
                turkishArtifact,
                model.getAttribute(
                        "turkishPdfArtifact"
                )
        );

        assertEquals(
                true,
                model.getAttribute(
                        "englishPdfArtifactAvailable"
                )
        );

        assertEquals(
                true,
                model.getAttribute(
                        "turkishPdfArtifactAvailable"
                )
        );

        assertFalse(
                (Boolean) model.getAttribute(
                        "englishPdfArtifactGenerationAvailable"
                )
        );

        assertFalse(
                (Boolean) model.getAttribute(
                        "turkishPdfArtifactGenerationAvailable"
                )
        );
    }

    private MonitoringRunPdfArtifact artifact(
            SiteSentinelReportLanguage reportLanguage
    ) {
        MonitoringRunPdfArtifact artifact =
                mock(MonitoringRunPdfArtifact.class);

        when(
                artifact.getReportVersion()
        ).thenReturn(
                MonitoringRunPdfVersion.V1.getValue()
        );

        when(
                artifact.getReportLanguage()
        ).thenReturn(reportLanguage);

        return artifact;
    }
}