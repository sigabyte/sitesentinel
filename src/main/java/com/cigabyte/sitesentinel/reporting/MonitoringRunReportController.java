package com.cigabyte.sitesentinel.reporting;

import com.cigabyte.sitesentinel.notification.NotificationEventService;
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

    public MonitoringRunReportController(
            MonitoringRunReportService
                    monitoringRunReportService,
            NotificationEventService
                    notificationEventService,
            MonitoringRunPdfArtifactService
                    pdfArtifactService
    ) {
        this.monitoringRunReportService =
                monitoringRunReportService;

        this.notificationEventService =
                notificationEventService;

        this.pdfArtifactService =
                pdfArtifactService;
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

        MonitoringRunPdfArtifact currentPdfArtifact =
                findCurrentPdfArtifact(
                        pdfArtifacts
                );

        boolean pdfArtifactAvailable =
                currentPdfArtifact != null;

        boolean pdfArtifactGenerationAvailable =
                report.isCompletedRun()
                        && !pdfArtifactAvailable;

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

        return "reports/monitoring-run-report";
    }

    private MonitoringRunPdfArtifact
    findCurrentPdfArtifact(
            List<MonitoringRunPdfArtifact> pdfArtifacts
    ) {
        String currentReportVersion =
                MonitoringRunPdfVersion.V1
                        .getValue();

        return pdfArtifacts.stream()
                .filter(
                        artifact ->
                                currentReportVersion.equals(
                                        artifact.getReportVersion()
                                )
                )
                .findFirst()
                .orElse(null);
    }
}