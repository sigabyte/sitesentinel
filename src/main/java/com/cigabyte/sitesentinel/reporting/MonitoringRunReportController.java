package com.cigabyte.sitesentinel.reporting;

import com.cigabyte.sitesentinel.notification.NotificationEventService;
import com.cigabyte.sitesentinel.reporting.dispatch.MonitoringRunReportDispatchAttempt;
import com.cigabyte.sitesentinel.reporting.dispatch.MonitoringRunReportDispatchAttemptService;
import com.cigabyte.sitesentinel.reporting.dispatch.MonitoringRunReportDispatchStatus;
import com.cigabyte.sitesentinel.reporting.pdf.MonitoringRunPdfArtifact;
import com.cigabyte.sitesentinel.reporting.pdf.MonitoringRunPdfArtifactService;
import com.cigabyte.sitesentinel.reporting.pdf.MonitoringRunPdfVersion;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping(
        "/websites/{websiteId}/monitoring-runs/{runId}/report"
)
public class MonitoringRunReportController {

    private final MonitoringRunReportService
            monitoringRunReportService;

    private final NotificationEventService
            notificationEventService;

    private final MonitoringRunPdfArtifactService
            pdfArtifactService;

    private final MonitoringRunReportDispatchAttemptService
            reportDispatchAttemptService;

    public MonitoringRunReportController(
            MonitoringRunReportService
                    monitoringRunReportService,
            NotificationEventService
                    notificationEventService,
            MonitoringRunPdfArtifactService
                    pdfArtifactService,
            MonitoringRunReportDispatchAttemptService
                    reportDispatchAttemptService
    ) {
        this.monitoringRunReportService =
                monitoringRunReportService;

        this.notificationEventService =
                notificationEventService;

        this.pdfArtifactService =
                pdfArtifactService;

        this.reportDispatchAttemptService =
                reportDispatchAttemptService;
    }

    @GetMapping
    public String detail(
            @PathVariable UUID websiteId,
            @PathVariable UUID runId,
            Model model
    ) {
        MonitoringRunReportView report =
                monitoringRunReportService.buildReport(
                        websiteId,
                        runId
                );

        List<MonitoringRunPdfArtifact> pdfArtifacts =
                pdfArtifactService.findByMonitoringRunId(
                        runId
                );

        MonitoringRunPdfArtifact englishPdfArtifact =
                findCurrentPdfArtifact(
                        pdfArtifacts,
                        SiteSentinelReportLanguage.ENGLISH
                );

        MonitoringRunPdfArtifact turkishPdfArtifact =
                findCurrentPdfArtifact(
                        pdfArtifacts,
                        SiteSentinelReportLanguage.TURKISH
                );

        boolean englishPdfArtifactAvailable =
                englishPdfArtifact != null;

        boolean turkishPdfArtifactAvailable =
                turkishPdfArtifact != null;

        boolean englishPdfArtifactGenerationAvailable =
                report.isCompletedRun()
                        && !englishPdfArtifactAvailable;

        boolean turkishPdfArtifactGenerationAvailable =
                report.isCompletedRun()
                        && !turkishPdfArtifactAvailable;

        /*
         * Preserve the existing single-artifact model contract
         * temporarily. Existing manual generation remains English by
         * default, while the language-specific model attributes expose
         * both persisted artifacts.
         */
        MonitoringRunPdfArtifact currentPdfArtifact =
                englishPdfArtifact;

        boolean pdfArtifactAvailable =
                englishPdfArtifactAvailable;

        boolean pdfArtifactGenerationAvailable =
                englishPdfArtifactGenerationAvailable;

        List<MonitoringRunReportDispatchAttempt>
                reportDispatchAttempts =
                reportDispatchAttemptService
                        .findAttemptsForMonitoringRun(
                                runId
                        );

        MonitoringRunReportDispatchAttempt
                latestReportDispatchAttempt =
                findLatestReportDispatchAttempt(
                        reportDispatchAttempts
                );

        boolean reportDispatchRetryAvailable =
                latestReportDispatchAttempt != null
                        && latestReportDispatchAttempt.getStatus()
                        == MonitoringRunReportDispatchStatus.FAILED;

        model.addAttribute(
                "report",
                report
        );

        model.addAttribute(
                "website",
                report.getWebsite()
        );

        model.addAttribute(
                "monitoringRun",
                report.getMonitoringRun()
        );

        model.addAttribute(
                "comparison",
                report.getComparison()
        );

        model.addAttribute(
                "notificationEvents",
                notificationEventService
                        .findByMonitoringRunId(
                                runId
                        )
        );

        model.addAttribute(
                "pdfArtifacts",
                pdfArtifacts
        );

        model.addAttribute(
                "englishPdfArtifact",
                englishPdfArtifact
        );

        model.addAttribute(
                "turkishPdfArtifact",
                turkishPdfArtifact
        );

        model.addAttribute(
                "englishPdfArtifactAvailable",
                englishPdfArtifactAvailable
        );

        model.addAttribute(
                "turkishPdfArtifactAvailable",
                turkishPdfArtifactAvailable
        );

        model.addAttribute(
                "englishPdfArtifactGenerationAvailable",
                englishPdfArtifactGenerationAvailable
        );

        model.addAttribute(
                "turkishPdfArtifactGenerationAvailable",
                turkishPdfArtifactGenerationAvailable
        );

        /*
         * Existing attributes are retained to prevent a template
         * regression before the bilingual template block is applied.
         */
        model.addAttribute(
                "pdfArtifact",
                currentPdfArtifact
        );

        model.addAttribute(
                "pdfArtifactAvailable",
                pdfArtifactAvailable
        );

        model.addAttribute(
                "pdfArtifactGenerationAvailable",
                pdfArtifactGenerationAvailable
        );

        model.addAttribute(
                "pdfArtifactCount",
                pdfArtifacts.size()
        );

        model.addAttribute(
                "pdfReportVersion",
                MonitoringRunPdfVersion.V1
                        .getValue()
        );

        model.addAttribute(
                "reportDispatchAttempts",
                reportDispatchAttempts
        );

        model.addAttribute(
                "reportDispatchAttemptCount",
                reportDispatchAttempts.size()
        );

        model.addAttribute(
                "latestReportDispatchAttempt",
                latestReportDispatchAttempt
        );

        model.addAttribute(
                "reportDispatchRetryAvailable",
                reportDispatchRetryAvailable
        );

        return "reports/monitoring-run-report";
    }

    private MonitoringRunReportDispatchAttempt
    findLatestReportDispatchAttempt(
            List<MonitoringRunReportDispatchAttempt>
                    reportDispatchAttempts
    ) {
        if (reportDispatchAttempts == null
                || reportDispatchAttempts.isEmpty()) {

            return null;
        }

        return reportDispatchAttempts.get(0);
    }

    private MonitoringRunPdfArtifact
    findCurrentPdfArtifact(
            List<MonitoringRunPdfArtifact> pdfArtifacts,
            SiteSentinelReportLanguage reportLanguage
    ) {
        if (pdfArtifacts == null
                || pdfArtifacts.isEmpty()) {

            return null;
        }

        String currentReportVersion =
                MonitoringRunPdfVersion.V1
                        .getValue();

        return pdfArtifacts.stream()
                .filter(
                        artifact ->
                                artifact != null
                                        && currentReportVersion.equals(
                                        artifact.getReportVersion()
                                )
                                        && artifact.getReportLanguage()
                                        == reportLanguage
                )
                .findFirst()
                .orElse(null);
    }
}