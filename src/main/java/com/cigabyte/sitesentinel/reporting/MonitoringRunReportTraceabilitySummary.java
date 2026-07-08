package com.cigabyte.sitesentinel.reporting;

public class MonitoringRunReportTraceabilitySummary {

    private final boolean collectedEvidenceAvailable;
    private final boolean normalizedEvidenceAvailable;
    private final boolean findingsAvailable;
    private final boolean risksAvailable;
    private final boolean trustAssessmentsAvailable;

    private MonitoringRunReportTraceabilitySummary(
            boolean collectedEvidenceAvailable,
            boolean normalizedEvidenceAvailable,
            boolean findingsAvailable,
            boolean risksAvailable,
            boolean trustAssessmentsAvailable
    ) {
        this.collectedEvidenceAvailable = collectedEvidenceAvailable;
        this.normalizedEvidenceAvailable = normalizedEvidenceAvailable;
        this.findingsAvailable = findingsAvailable;
        this.risksAvailable = risksAvailable;
        this.trustAssessmentsAvailable = trustAssessmentsAvailable;
    }

    public static MonitoringRunReportTraceabilitySummary fromCounts(
            MonitoringRunReportCounts counts
    ) {
        if (counts == null) {
            return new MonitoringRunReportTraceabilitySummary(
                    false,
                    false,
                    false,
                    false,
                    false
            );
        }

        return new MonitoringRunReportTraceabilitySummary(
                counts.hasCollectedEvidence(),
                counts.hasNormalizedEvidence(),
                counts.hasFindings(),
                counts.hasRisks(),
                counts.hasTrustAssessments()
        );
    }

    public boolean isCollectedEvidenceAvailable() {
        return collectedEvidenceAvailable;
    }

    public boolean isNormalizedEvidenceAvailable() {
        return normalizedEvidenceAvailable;
    }

    public boolean isFindingsAvailable() {
        return findingsAvailable;
    }

    public boolean isRisksAvailable() {
        return risksAvailable;
    }

    public boolean isTrustAssessmentsAvailable() {
        return trustAssessmentsAvailable;
    }

    public boolean isEvidenceToFindingReviewable() {
        return collectedEvidenceAvailable && findingsAvailable;
    }

    public boolean isFindingToRiskReviewable() {
        return findingsAvailable && risksAvailable;
    }

    public boolean isRiskToTrustReviewable() {
        return risksAvailable && trustAssessmentsAvailable;
    }

    public boolean isEndToEndReviewable() {
        return collectedEvidenceAvailable
                && normalizedEvidenceAvailable
                && findingsAvailable
                && risksAvailable
                && trustAssessmentsAvailable;
    }
}