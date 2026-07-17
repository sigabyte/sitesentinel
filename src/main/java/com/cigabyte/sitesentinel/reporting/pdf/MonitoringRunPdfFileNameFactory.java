package com.cigabyte.sitesentinel.reporting.pdf;

import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class MonitoringRunPdfFileNameFactory {

    public String create(
            UUID monitoringRunId,
            MonitoringRunPdfVersion reportVersion
    ) {
        UUID requiredMonitoringRunId =
                Objects.requireNonNull(
                        monitoringRunId,
                        "Monitoring run ID is required."
                );

        MonitoringRunPdfVersion requiredReportVersion =
                Objects.requireNonNull(
                        reportVersion,
                        "PDF report version is required."
                );

        return "sitesentinel-monitoring-run-"
                + requiredMonitoringRunId
                + "-"
                + versionToken(requiredReportVersion)
                + ".pdf";
    }

    private String versionToken(
            MonitoringRunPdfVersion reportVersion
    ) {
        return switch (reportVersion) {
            case V1 -> "v1";
        };
    }
}