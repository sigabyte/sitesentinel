package com.cigabyte.sitesentinel.comparison;

import com.cigabyte.sitesentinel.trust.TrustAssessment;
import com.cigabyte.sitesentinel.trust.TrustStatus;

public class TrustComparisonSummary {

    private final boolean available;
    private final TrustAssessment currentAssessment;
    private final TrustAssessment previousAssessment;
    private final TrustStatus currentTrustStatus;
    private final TrustStatus previousTrustStatus;
    private final Integer currentTrustScore;
    private final Integer previousTrustScore;
    private final Integer scoreDelta;
    private final boolean statusChanged;
    private final TrustScoreDirection scoreDirection;

    private TrustComparisonSummary(
            boolean available,
            TrustAssessment currentAssessment,
            TrustAssessment previousAssessment,
            TrustStatus currentTrustStatus,
            TrustStatus previousTrustStatus,
            Integer currentTrustScore,
            Integer previousTrustScore,
            Integer scoreDelta,
            boolean statusChanged,
            TrustScoreDirection scoreDirection
    ) {
        this.available = available;
        this.currentAssessment = currentAssessment;
        this.previousAssessment = previousAssessment;
        this.currentTrustStatus = currentTrustStatus;
        this.previousTrustStatus = previousTrustStatus;
        this.currentTrustScore = currentTrustScore;
        this.previousTrustScore = previousTrustScore;
        this.scoreDelta = scoreDelta;
        this.statusChanged = statusChanged;
        this.scoreDirection = scoreDirection;
    }

    public static TrustComparisonSummary unavailable() {
        return new TrustComparisonSummary(
                false,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                TrustScoreDirection.UNAVAILABLE
        );
    }

    public static TrustComparisonSummary compare(
            TrustAssessment currentAssessment,
            TrustAssessment previousAssessment
    ) {
        if (currentAssessment == null || previousAssessment == null) {
            return unavailable();
        }

        Integer currentScore = currentAssessment.getTrustScore();
        Integer previousScore = previousAssessment.getTrustScore();

        Integer scoreDelta = null;
        TrustScoreDirection scoreDirection = TrustScoreDirection.UNAVAILABLE;

        if (currentScore != null && previousScore != null) {
            scoreDelta = currentScore - previousScore;

            if (scoreDelta > 0) {
                scoreDirection = TrustScoreDirection.IMPROVED;
            } else if (scoreDelta < 0) {
                scoreDirection = TrustScoreDirection.DECLINED;
            } else {
                scoreDirection = TrustScoreDirection.UNCHANGED;
            }
        }

        TrustStatus currentStatus = currentAssessment.getTrustStatus();
        TrustStatus previousStatus = previousAssessment.getTrustStatus();

        boolean statusChanged = currentStatus != null
                && previousStatus != null
                && currentStatus != previousStatus;

        return new TrustComparisonSummary(
                true,
                currentAssessment,
                previousAssessment,
                currentStatus,
                previousStatus,
                currentScore,
                previousScore,
                scoreDelta,
                statusChanged,
                scoreDirection
        );
    }

    public boolean isAvailable() {
        return available;
    }

    public TrustAssessment getCurrentAssessment() {
        return currentAssessment;
    }

    public TrustAssessment getPreviousAssessment() {
        return previousAssessment;
    }

    public TrustStatus getCurrentTrustStatus() {
        return currentTrustStatus;
    }

    public TrustStatus getPreviousTrustStatus() {
        return previousTrustStatus;
    }

    public Integer getCurrentTrustScore() {
        return currentTrustScore;
    }

    public Integer getPreviousTrustScore() {
        return previousTrustScore;
    }

    public Integer getScoreDelta() {
        return scoreDelta;
    }

    public boolean isStatusChanged() {
        return statusChanged;
    }

    public TrustScoreDirection getScoreDirection() {
        return scoreDirection;
    }
}