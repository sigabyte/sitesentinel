package com.cigabyte.sitesentinel.comparison;

import com.cigabyte.sitesentinel.monitoring.MonitoringRun;

import java.util.List;

public class AssessmentComparisonSummary {

    private final ComparisonStatus status;
    private final MonitoringRun currentRun;
    private final MonitoringRun previousRun;
    private final TrustComparisonSummary trustComparison;
    private final List<FindingComparisonItem> findingChanges;
    private final List<RiskComparisonItem> riskChanges;

    private final int newFindingCount;
    private final int resolvedFindingCount;
    private final int unchangedFindingCount;

    private final int newRiskCount;
    private final int resolvedRiskCount;
    private final int unchangedRiskCount;

    private AssessmentComparisonSummary(
            ComparisonStatus status,
            MonitoringRun currentRun,
            MonitoringRun previousRun,
            TrustComparisonSummary trustComparison,
            List<FindingComparisonItem> findingChanges,
            List<RiskComparisonItem> riskChanges
    ) {
        this.status = status;
        this.currentRun = currentRun;
        this.previousRun = previousRun;
        this.trustComparison = trustComparison == null
                ? TrustComparisonSummary.unavailable()
                : trustComparison;
        this.findingChanges = findingChanges == null
                ? List.of()
                : List.copyOf(findingChanges);
        this.riskChanges = riskChanges == null
                ? List.of()
                : List.copyOf(riskChanges);

        this.newFindingCount = countFindingChanges(ComparisonChangeStatus.NEW);
        this.resolvedFindingCount = countFindingChanges(ComparisonChangeStatus.RESOLVED);
        this.unchangedFindingCount = countFindingChanges(ComparisonChangeStatus.UNCHANGED);

        this.newRiskCount = countRiskChanges(ComparisonChangeStatus.NEW);
        this.resolvedRiskCount = countRiskChanges(ComparisonChangeStatus.RESOLVED);
        this.unchangedRiskCount = countRiskChanges(ComparisonChangeStatus.UNCHANGED);
    }

    public static AssessmentComparisonSummary available(
            MonitoringRun currentRun,
            MonitoringRun previousRun,
            TrustComparisonSummary trustComparison,
            List<FindingComparisonItem> findingChanges,
            List<RiskComparisonItem> riskChanges
    ) {
        return new AssessmentComparisonSummary(
                ComparisonStatus.AVAILABLE,
                currentRun,
                previousRun,
                trustComparison,
                findingChanges,
                riskChanges
        );
    }

    public static AssessmentComparisonSummary unavailable(
            ComparisonStatus status,
            MonitoringRun currentRun
    ) {
        return new AssessmentComparisonSummary(
                status,
                currentRun,
                null,
                TrustComparisonSummary.unavailable(),
                List.of(),
                List.of()
        );
    }

    private int countFindingChanges(ComparisonChangeStatus changeStatus) {
        return (int) findingChanges.stream()
                .filter(item -> item.getChangeStatus() == changeStatus)
                .count();
    }

    private int countRiskChanges(ComparisonChangeStatus changeStatus) {
        return (int) riskChanges.stream()
                .filter(item -> item.getChangeStatus() == changeStatus)
                .count();
    }

    public ComparisonStatus getStatus() {
        return status;
    }

    public boolean isAvailable() {
        return status == ComparisonStatus.AVAILABLE;
    }

    public MonitoringRun getCurrentRun() {
        return currentRun;
    }

    public MonitoringRun getPreviousRun() {
        return previousRun;
    }

    public TrustComparisonSummary getTrustComparison() {
        return trustComparison;
    }

    public List<FindingComparisonItem> getFindingChanges() {
        return findingChanges;
    }

    public List<RiskComparisonItem> getRiskChanges() {
        return riskChanges;
    }

    public int getNewFindingCount() {
        return newFindingCount;
    }

    public int getResolvedFindingCount() {
        return resolvedFindingCount;
    }

    public int getUnchangedFindingCount() {
        return unchangedFindingCount;
    }

    public int getNewRiskCount() {
        return newRiskCount;
    }

    public int getResolvedRiskCount() {
        return resolvedRiskCount;
    }

    public int getUnchangedRiskCount() {
        return unchangedRiskCount;
    }

    public int getCurrentFindingCount() {
        return findingChanges.stream()
                .mapToInt(FindingComparisonItem::getCurrentCount)
                .sum();
    }

    public int getPreviousFindingCount() {
        return findingChanges.stream()
                .mapToInt(FindingComparisonItem::getPreviousCount)
                .sum();
    }

    public int getCurrentRiskCount() {
        return riskChanges.stream()
                .mapToInt(RiskComparisonItem::getCurrentCount)
                .sum();
    }

    public int getPreviousRiskCount() {
        return riskChanges.stream()
                .mapToInt(RiskComparisonItem::getPreviousCount)
                .sum();
    }
}