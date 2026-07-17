package com.cigabyte.sitesentinel.recommendation;

import java.util.UUID;

public record RiskRemediationRecommendationRunGenerationResult(
        UUID monitoringRunId,
        int riskCount,
        int generatedCount,
        int failedCount
) {

    public RiskRemediationRecommendationRunGenerationResult {
        if (monitoringRunId == null) {
            throw new IllegalArgumentException(
                    "Monitoring run ID is required."
            );
        }

        validateCount(
                riskCount,
                "Risk count"
        );

        validateCount(
                generatedCount,
                "Generated recommendation count"
        );

        validateCount(
                failedCount,
                "Failed recommendation count"
        );

        if (generatedCount + failedCount != riskCount) {
            throw new IllegalArgumentException(
                    "Generated and failed recommendation counts "
                            + "must equal the risk count."
            );
        }
    }

    private static void validateCount(
            int value,
            String fieldName
    ) {
        if (value < 0) {
            throw new IllegalArgumentException(
                    fieldName + " must not be negative."
            );
        }
    }

    public boolean isFullySuccessful() {
        return failedCount == 0;
    }

    public boolean isEmpty() {
        return riskCount == 0;
    }
}