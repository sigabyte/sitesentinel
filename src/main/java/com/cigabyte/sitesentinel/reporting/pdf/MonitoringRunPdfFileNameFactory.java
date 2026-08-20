package com.cigabyte.sitesentinel.reporting.pdf;

import com.cigabyte.sitesentinel.reporting.SiteSentinelReportLanguage;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class MonitoringRunPdfFileNameFactory {

    public String create(
            UUID monitoringRunId,
            MonitoringRunPdfVersion reportVersion
    ) {
        return create(
                monitoringRunId,
                reportVersion,
                SiteSentinelReportLanguage.ENGLISH
        );
    }

    public String create(
            UUID monitoringRunId,
            MonitoringRunPdfVersion reportVersion,
            SiteSentinelReportLanguage reportLanguage
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

        SiteSentinelReportLanguage requiredReportLanguage =
                Objects.requireNonNull(
                        reportLanguage,
                        "PDF report language is required."
                );

        return "sitesentinel-monitoring-run-"
                + requiredMonitoringRunId
                + "-"
                + requiredReportLanguage.getFileToken()
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