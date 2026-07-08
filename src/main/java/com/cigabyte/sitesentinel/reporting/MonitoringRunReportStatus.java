package com.cigabyte.sitesentinel.reporting;

import com.cigabyte.sitesentinel.monitoring.MonitoringRunStatus;

public enum MonitoringRunReportStatus {
    FULL_REPORT,
    LIMITED_PENDING_RUN,
    LIMITED_RUNNING_RUN,
    LIMITED_FAILED_RUN;

    public static MonitoringRunReportStatus from(MonitoringRunStatus monitoringRunStatus) {
        if (monitoringRunStatus == MonitoringRunStatus.COMPLETED) {
            return FULL_REPORT;
        }

        if (monitoringRunStatus == MonitoringRunStatus.RUNNING) {
            return LIMITED_RUNNING_RUN;
        }

        if (monitoringRunStatus == MonitoringRunStatus.FAILED) {
            return LIMITED_FAILED_RUN;
        }

        return LIMITED_PENDING_RUN;
    }

    public boolean isFullReport() {
        return this == FULL_REPORT;
    }

    public boolean isLimitedReport() {
        return !isFullReport();
    }
}