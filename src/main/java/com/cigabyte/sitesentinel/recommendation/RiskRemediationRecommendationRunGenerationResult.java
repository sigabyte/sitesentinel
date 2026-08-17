package com.cigabyte.sitesentinel.recommendation;

import java.util.UUID;

public record RiskRemediationRecommendationRunGenerationResult(
        UUID monitoringRunId,
        int riskCount,
        int generatedCount,
        int skippedCount,
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
                skippedCount,
                "Skipped recommendation count"
        );

        validateCount(
                failedCount,
                "Failed recommendation count"
        );

        if (generatedCount
                + skippedCount
                + failedCount
                != riskCount) {

            throw new IllegalArgumentException(
                    "Generated, skipped and failed recommendation "
                            + "counts must equal the risk count."
            );
        }
    }

    public RiskRemediationRecommendationRunGenerationResult(
            UUID monitoringRunId,
            int riskCount,
            int generatedCount,
            int failedCount
    ) {
        this(
                monitoringRunId,
                riskCount,
                generatedCount,
                0,
                failedCount
        );
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