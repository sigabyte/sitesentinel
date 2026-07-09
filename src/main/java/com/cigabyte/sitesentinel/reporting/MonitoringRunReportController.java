package com.cigabyte.sitesentinel.reporting;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;
import com.cigabyte.sitesentinel.notification.NotificationEventService;

@Controller
@RequestMapping("/websites/{websiteId}/monitoring-runs/{runId}/report")
public class MonitoringRunReportController {

    private final MonitoringRunReportService monitoringRunReportService;
    private final NotificationEventService notificationEventService;

    public MonitoringRunReportController(
            MonitoringRunReportService monitoringRunReportService,
            NotificationEventService notificationEventService
    ) {
        this.monitoringRunReportService = monitoringRunReportService;
        this.notificationEventService = notificationEventService;
    }

    @GetMapping
    public String detail(
            @PathVariable UUID websiteId,
            @PathVariable UUID runId,
            Model model
    ) {
        MonitoringRunReportView report = monitoringRunReportService.buildReport(
                websiteId,
                runId
        );

        model.addAttribute("report", report);
        model.addAttribute("website", report.getWebsite());
        model.addAttribute("monitoringRun", report.getMonitoringRun());
        model.addAttribute("comparison", report.getComparison());
        model.addAttribute("notificationEvents", notificationEventService.findByMonitoringRunId(runId));

        return "reports/monitoring-run-report";
    }
}