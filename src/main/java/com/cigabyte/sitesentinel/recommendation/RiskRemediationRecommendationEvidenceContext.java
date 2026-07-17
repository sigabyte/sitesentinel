package com.cigabyte.sitesentinel.recommendation;

import java.util.Objects;
import java.util.UUID;

public record RiskRemediationRecommendationEvidenceContext(
        UUID normalizedEvidenceId,
        UUID collectedEvidenceId,
        String normalizedType,
        String normalizedValue
) {

    public RiskRemediationRecommendationEvidenceContext {
        normalizedEvidenceId = Objects.requireNonNull(
                normalizedEvidenceId,
                "Normalized evidence ID is required."
        );

        collectedEvidenceId = Objects.requireNonNull(
                collectedEvidenceId,
                "Collected evidence ID is required."
        );

        normalizedType = requireText(
                normalizedType,
                "Normalized evidence type"
        );

        normalizedValue = requireText(
                normalizedValue,
                "Normalized evidence value"
        );
    }

    private static String requireText(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " is required."
            );
        }

        return value.trim();
    }
}