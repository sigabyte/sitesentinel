package com.cigabyte.sitesentinel.reporting;

import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
import com.cigabyte.sitesentinel.notification.NotificationEventService;
import com.cigabyte.sitesentinel.reporting.dispatch.MonitoringRunReportDispatchAttempt;
import com.cigabyte.sitesentinel.reporting.dispatch.MonitoringRunReportDispatchAttemptService;
import com.cigabyte.sitesentinel.reporting.dispatch.MonitoringRunReportDispatchStatus;
import com.cigabyte.sitesentinel.reporting.pdf.MonitoringRunPdfArtifactService;
import com.cigabyte.sitesentinel.website.Website;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MonitoringRunReportControllerTests {

    private MonitoringRunReportService
            monitoringRunReportService;

    private NotificationEventService
            notificationEventService;

    private MonitoringRunPdfArtifactService
            pdfArtifactService;

    private MonitoringRunReportDispatchAttemptService
            reportDispatchAttemptService;

    private MonitoringRunReportController controller;

    @BeforeEach
    void setUp() {
        monitoringRunReportService =
                mock(
                        MonitoringRunReportService.class
                );

        notificationEventService =
                mock(
                        NotificationEventService.class
                );

        pdfArtifactService =
                mock(
                        MonitoringRunPdfArtifactService.class
                );

        reportDispatchAttemptService =
                mock(
                        MonitoringRunReportDispatchAttemptService.class
                );

        controller =
                new MonitoringRunReportController(
                        monitoringRunReportService,
                        notificationEventService,
                        pdfArtifactService,
                        reportDispatchAttemptService
                );
    }

    @Test
    void detailAddsDispatchHistoryAndEnablesRetryForLatestFailedAttempt() {
        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        MonitoringRunReportView report =
                report(
                        monitoringRunId,
                        true
                );

        MonitoringRunReportDispatchAttempt
                latestFailedAttempt =
                dispatchAttempt(
                        MonitoringRunReportDispatchStatus.FAILED
                );

        MonitoringRunReportDispatchAttempt
                olderSentAttempt =
                dispatchAttempt(
                        MonitoringRunReportDispatchStatus.SENT
                );

        List<MonitoringRunReportDispatchAttempt> attempts =
                List.of(
                        latestFailedAttempt,
                        olderSentAttempt
                );

        prepareReportDependencies(
                websiteId,
                monitoringRunId,
                report
        );

        when(
                reportDispatchAttemptService
                        .findAttemptsForMonitoringRun(
                                monitoringRunId
                        )
        ).thenReturn(
                attempts
        );

        ExtendedModelMap model =
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
                attempts,
                model.get(
                        "reportDispatchAttempts"
                )
        );

        assertEquals(
                2,
                model.get(
                        "reportDispatchAttemptCount"
                )
        );

        assertSame(
                latestFailedAttempt,
                model.get(
                        "latestReportDispatchAttempt"
                )
        );

        assertTrue(
                (Boolean) model.get(
                        "reportDispatchRetryAvailable"
                )
        );

        verify(
                reportDispatchAttemptService
        ).findAttemptsForMonitoringRun(
                monitoringRunId
        );
    }

    @Test
    void detailDisablesRetryWhenNoDispatchAttemptExists() {
        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        MonitoringRunReportView report =
                report(
                        monitoringRunId,
                        true
                );

        prepareReportDependencies(
                websiteId,
                monitoringRunId,
                report
        );

        when(
                reportDispatchAttemptService
                        .findAttemptsForMonitoringRun(
                                monitoringRunId
                        )
        ).thenReturn(
                List.of()
        );

        ExtendedModelMap model =
                new ExtendedModelMap();

        controller.detail(
                websiteId,
                monitoringRunId,
                model
        );

        assertEquals(
                List.of(),
                model.get(
                        "reportDispatchAttempts"
                )
        );

        assertEquals(
                0,
                model.get(
                        "reportDispatchAttemptCount"
                )
        );

        assertNull(
                model.get(
                        "latestReportDispatchAttempt"
                )
        );

        assertFalse(
                (Boolean) model.get(
                        "reportDispatchRetryAvailable"
                )
        );
    }

    @Test
    void detailDoesNotEnableRetryForOlderFailedAttempt() {
        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        MonitoringRunReportView report =
                report(
                        monitoringRunId,
                        true
                );

        MonitoringRunReportDispatchAttempt
                latestSentAttempt =
                dispatchAttempt(
                        MonitoringRunReportDispatchStatus.SENT
                );

        MonitoringRunReportDispatchAttempt
                olderFailedAttempt =
                dispatchAttempt(
                        MonitoringRunReportDispatchStatus.FAILED
                );

        prepareReportDependencies(
                websiteId,
                monitoringRunId,
                report
        );

        when(
                reportDispatchAttemptService
                        .findAttemptsForMonitoringRun(
                                monitoringRunId
                        )
        ).thenReturn(
                List.of(
                        latestSentAttempt,
                        olderFailedAttempt
                )
        );

        ExtendedModelMap model =
                new ExtendedModelMap();

        controller.detail(
                websiteId,
                monitoringRunId,
                model
        );

        assertSame(
                latestSentAttempt,
                model.get(
                        "latestReportDispatchAttempt"
                )
        );

        assertFalse(
                (Boolean) model.get(
                        "reportDispatchRetryAvailable"
                )
        );
    }

    private void prepareReportDependencies(
            UUID websiteId,
            UUID monitoringRunId,
            MonitoringRunReportView report
    ) {
        when(
                monitoringRunReportService.buildReport(
                        websiteId,
                        monitoringRunId
                )
        ).thenReturn(
                report
        );

        when(
                notificationEventService
                        .findByMonitoringRunId(
                                monitoringRunId
                        )
        ).thenReturn(
                List.of()
        );

        when(
                pdfArtifactService
                        .findByMonitoringRunId(
                                monitoringRunId
                        )
        ).thenReturn(
                List.of()
        );
    }

    private MonitoringRunReportView report(
            UUID monitoringRunId,
            boolean completed
    ) {
        MonitoringRunReportView report =
                mock(
                        MonitoringRunReportView.class
                );

        Website website =
                mock(
                        Website.class
                );

        MonitoringRun monitoringRun =
                mock(
                        MonitoringRun.class
                );

        when(
                monitoringRun.getId()
        ).thenReturn(
                monitoringRunId
        );

        when(
                report.getWebsite()
        ).thenReturn(
                website
        );

        when(
                report.getMonitoringRun()
        ).thenReturn(
                monitoringRun
        );

        when(
                report.getComparison()
        ).thenReturn(
                null
        );

        when(
                report.isCompletedRun()
        ).thenReturn(
                completed
        );

        return report;
    }

    private MonitoringRunReportDispatchAttempt
    dispatchAttempt(
            MonitoringRunReportDispatchStatus status
    ) {
        MonitoringRunReportDispatchAttempt attempt =
                mock(
                        MonitoringRunReportDispatchAttempt.class
                );

        when(
                attempt.getStatus()
        ).thenReturn(
                status
        );

        return attempt;
    }
}