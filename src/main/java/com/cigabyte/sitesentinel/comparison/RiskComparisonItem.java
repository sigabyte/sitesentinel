package com.cigabyte.sitesentinel.comparison;

import com.cigabyte.sitesentinel.risk.Risk;
import com.cigabyte.sitesentinel.risk.RiskSeverity;

public class RiskComparisonItem {

    private final String riskType;
    private final ComparisonChangeStatus changeStatus;
    private final int currentCount;
    private final int previousCount;
    private final Risk currentRisk;
    private final Risk previousRisk;
    private final RiskSeverity currentSeverity;
    private final RiskSeverity previousSeverity;
    private final Integer currentRiskScore;
    private final Integer previousRiskScore;

    public RiskComparisonItem(
            String riskType,
            ComparisonChangeStatus changeStatus,
            int currentCount,
            int previousCount,
            Risk currentRisk,
            Risk previousRisk
    ) {
        this.riskType = riskType;
        this.changeStatus = changeStatus;
        this.currentCount = currentCount;
        this.previousCount = previousCount;
        this.currentRisk = currentRisk;
        this.previousRisk = previousRisk;
        this.currentSeverity = currentRisk == null ? null : currentRisk.getSeverity();
        this.previousSeverity = previousRisk == null ? null : previousRisk.getSeverity();
        this.currentRiskScore = currentRisk == null ? null : currentRisk.getRiskScore();
        this.previousRiskScore = previousRisk == null ? null : previousRisk.getRiskScore();
    }

    public String getRiskType() {
        return riskType;
    }

    public ComparisonChangeStatus getChangeStatus() {
        return changeStatus;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public int getPreviousCount() {
        return previousCount;
    }

    public Risk getCurrentRisk() {
        return currentRisk;
    }

    public Risk getPreviousRisk() {
        return previousRisk;
    }

    public RiskSeverity getCurrentSeverity() {
        return currentSeverity;
    }

    public RiskSeverity getPreviousSeverity() {
        return previousSeverity;
    }

    public Integer getCurrentRiskScore() {
        return currentRiskScore;
    }

    public Integer getPreviousRiskScore() {
        return previousRiskScore;
    }

    public boolean isNew() {
        return changeStatus == ComparisonChangeStatus.NEW;
    }

    public boolean isResolved() {
        return changeStatus == ComparisonChangeStatus.RESOLVED;
    }

    public boolean isUnchanged() {
        return changeStatus == ComparisonChangeStatus.UNCHANGED;
    }
}