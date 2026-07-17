package com.cigabyte.sitesentinel.recommendation;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record RiskRemediationRecommendationFindingContext(
        UUID findingId,
        String findingType,
        String title,
        String description,
        int confidenceScore,
        List<RiskRemediationRecommendationEvidenceContext> evidenceItems
) {

    public RiskRemediationRecommendationFindingContext {
        findingId = Objects.requireNonNull(
                findingId,
                "Finding ID is required."
        );

        findingType = requireText(
                findingType,
                "Finding type"
        );

        title = requireText(
                title,
                "Finding title"
        );

        description = requireText(
                description,
                "Finding description"
        );

        if (confidenceScore < 0 || confidenceScore > 100) {
            throw new IllegalArgumentException(
                    "Finding confidence score must be between 0 and 100."
            );
        }

        evidenceItems = evidenceItems == null
                ? List.of()
                : List.copyOf(evidenceItems);
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