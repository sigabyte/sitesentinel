package com.cigabyte.sitesentinel.reporting;

import com.cigabyte.sitesentinel.comparison.AssessmentComparisonSummary;
import com.cigabyte.sitesentinel.finding.Finding;
import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
import com.cigabyte.sitesentinel.monitoring.MonitoringRunStatus;
import com.cigabyte.sitesentinel.risk.Risk;
import com.cigabyte.sitesentinel.trust.TrustAssessment;
import com.cigabyte.sitesentinel.website.Website;

import java.util.List;

public class MonitoringRunReportView {

    private final Website website;
    private final MonitoringRun monitoringRun;
    private final MonitoringRunReportStatus reportStatus;
    private final MonitoringRunReportCounts counts;
    private final MonitoringRunReportTraceabilitySummary traceabilitySummary;
    private final TrustAssessment latestTrustAssessment;
    private final List<Finding> findings;
    private final List<Risk> risks;
    private final AssessmentComparisonSummary comparison;

    public MonitoringRunReportView(
            Website website,
            MonitoringRun monitoringRun,
            MonitoringRunReportCounts counts,
            MonitoringRunReportTraceabilitySummary traceabilitySummary,
            TrustAssessment latestTrustAssessment,
            List<Finding> findings,
            List<Risk> risks,
            AssessmentComparisonSummary comparison
    ) {
        this.website = website;
        this.monitoringRun = monitoringRun;
        this.reportStatus = MonitoringRunReportStatus.from(
                monitoringRun == null ? null : monitoringRun.getStatus()
        );
        this.counts = counts == null
                ? new MonitoringRunReportCounts(0, 0, 0, 0, 0)
                : counts;
        this.traceabilitySummary = traceabilitySummary == null
                ? MonitoringRunReportTraceabilitySummary.fromCounts(this.counts)
                : traceabilitySummary;
        this.latestTrustAssessment = latestTrustAssessment;
        this.findings = findings == null ? List.of() : List.copyOf(findings);
        this.risks = risks == null ? List.of() : List.copyOf(risks);
        this.comparison = comparison;
    }

    public Website getWebsite() {
        return website;
    }

    public MonitoringRun getMonitoringRun() {
        return monitoringRun;
    }

    public MonitoringRunReportStatus getReportStatus() {
        return reportStatus;
    }

    public MonitoringRunReportCounts getCounts() {
        return counts;
    }

    public MonitoringRunReportTraceabilitySummary getTraceabilitySummary() {
        return traceabilitySummary;
    }

    public TrustAssessment getLatestTrustAssessment() {
        return latestTrustAssessment;
    }

    public List<Finding> getFindings() {
        return findings;
    }

    public List<Risk> getRisks() {
        return risks;
    }

    public AssessmentComparisonSummary getComparison() {
        return comparison;
    }

    public boolean isFullReport() {
        return reportStatus.isFullReport();
    }

    public boolean isLimitedReport() {
        return reportStatus.isLimitedReport();
    }

    public boolean isCompletedRun() {
        return monitoringRun != null
                && monitoringRun.getStatus() == MonitoringRunStatus.COMPLETED;
    }

    public boolean isFailedRun() {
        return monitoringRun != null
                && monitoringRun.getStatus() == MonitoringRunStatus.FAILED;
    }

    public boolean isRunningRun() {
        return monitoringRun != null
                && monitoringRun.getStatus() == MonitoringRunStatus.RUNNING;
    }

    public boolean isPendingRun() {
        return monitoringRun != null
                && monitoringRun.getStatus() == MonitoringRunStatus.PENDING;
    }

    public boolean hasFailureReason() {
        return monitoringRun != null
                && monitoringRun.getFailureReason() != null
                && !monitoringRun.getFailureReason().isBlank();
    }

    public boolean hasTrustAssessment() {
        return latestTrustAssessment != null;
    }

    public boolean hasFindings() {
        return !findings.isEmpty();
    }

    public boolean hasRisks() {
        return !risks.isEmpty();
    }

    public boolean hasComparison() {
        return comparison != null && comparison.isAvailable();
    }

    public String getReportTitle() {
        if (website == null || website.getName() == null || website.getName().isBlank()) {
            return "Monitoring Run Report";
        }

        return "Monitoring Run Report — " + website.getName();
    }
}