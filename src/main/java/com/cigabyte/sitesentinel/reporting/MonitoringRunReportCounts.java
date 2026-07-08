package com.cigabyte.sitesentinel.reporting;

public class MonitoringRunReportCounts {

    private final long collectedEvidenceCount;
    private final long normalizedEvidenceCount;
    private final long findingCount;
    private final long riskCount;
    private final long trustAssessmentCount;

    public MonitoringRunReportCounts(
            long collectedEvidenceCount,
            long normalizedEvidenceCount,
            long findingCount,
            long riskCount,
            long trustAssessmentCount
    ) {
        this.collectedEvidenceCount = collectedEvidenceCount;
        this.normalizedEvidenceCount = normalizedEvidenceCount;
        this.findingCount = findingCount;
        this.riskCount = riskCount;
        this.trustAssessmentCount = trustAssessmentCount;
    }

    public long getCollectedEvidenceCount() {
        return collectedEvidenceCount;
    }

    public long getNormalizedEvidenceCount() {
        return normalizedEvidenceCount;
    }

    public long getFindingCount() {
        return findingCount;
    }

    public long getRiskCount() {
        return riskCount;
    }

    public long getTrustAssessmentCount() {
        return trustAssessmentCount;
    }

    public boolean hasCollectedEvidence() {
        return collectedEvidenceCount > 0;
    }

    public boolean hasNormalizedEvidence() {
        return normalizedEvidenceCount > 0;
    }

    public boolean hasFindings() {
        return findingCount > 0;
    }

    public boolean hasRisks() {
        return riskCount > 0;
    }

    public boolean hasTrustAssessments() {
        return trustAssessmentCount > 0;
    }

    public boolean hasAssessmentOutput() {
        return hasFindings() || hasRisks() || hasTrustAssessments();
    }

    public boolean hasLifecycleOutput() {
        return hasCollectedEvidence()
                || hasNormalizedEvidence()
                || hasAssessmentOutput();
    }
}