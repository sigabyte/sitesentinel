package com.cigabyte.sitesentinel.comparison;

import com.cigabyte.sitesentinel.finding.Finding;

public class FindingComparisonItem {

    private final String findingType;
    private final ComparisonChangeStatus changeStatus;
    private final int currentCount;
    private final int previousCount;
    private final Finding currentFinding;
    private final Finding previousFinding;

    public FindingComparisonItem(
            String findingType,
            ComparisonChangeStatus changeStatus,
            int currentCount,
            int previousCount,
            Finding currentFinding,
            Finding previousFinding
    ) {
        this.findingType = findingType;
        this.changeStatus = changeStatus;
        this.currentCount = currentCount;
        this.previousCount = previousCount;
        this.currentFinding = currentFinding;
        this.previousFinding = previousFinding;
    }

    public String getFindingType() {
        return findingType;
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

    public Finding getCurrentFinding() {
        return currentFinding;
    }

    public Finding getPreviousFinding() {
        return previousFinding;
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