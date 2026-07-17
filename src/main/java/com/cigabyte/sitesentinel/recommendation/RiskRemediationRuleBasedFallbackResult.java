package com.cigabyte.sitesentinel.recommendation;

import java.util.Objects;

public record RiskRemediationRuleBasedFallbackResult(
        String ruleVersion,
        RiskRemediationRecommendationContent content
) {

    public RiskRemediationRuleBasedFallbackResult {
        ruleVersion = requireText(
                ruleVersion,
                "Fallback rule version"
        );

        content = Objects.requireNonNull(
                content,
                "Fallback recommendation content is required."
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