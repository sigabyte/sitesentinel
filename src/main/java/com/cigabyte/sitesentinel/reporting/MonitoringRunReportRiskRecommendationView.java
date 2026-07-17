package com.cigabyte.sitesentinel.reporting;

import com.cigabyte.sitesentinel.recommendation.RiskRemediationRecommendation;
import com.cigabyte.sitesentinel.risk.Risk;

import java.util.Objects;

public class MonitoringRunReportRiskRecommendationView {

    private final Risk risk;

    private final RiskRemediationRecommendation
            latestRecommendation;

    public MonitoringRunReportRiskRecommendationView(
            Risk risk,
            RiskRemediationRecommendation latestRecommendation
    ) {
        this.risk = Objects.requireNonNull(
                risk,
                "Report risk is required."
        );

        this.latestRecommendation = latestRecommendation;
    }

    public Risk getRisk() {
        return risk;
    }

    public RiskRemediationRecommendation
    getLatestRecommendation() {
        return latestRecommendation;
    }

    public boolean isRecommendationAvailable() {
        return latestRecommendation != null;
    }
}