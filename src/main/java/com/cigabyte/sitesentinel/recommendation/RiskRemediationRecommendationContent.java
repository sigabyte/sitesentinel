package com.cigabyte.sitesentinel.recommendation;

public record RiskRemediationRecommendationContent(
        String title,
        String summary,
        String remediationSteps,
        String verificationSteps
) {

    private static final int TITLE_MAX_LENGTH = 220;

    public RiskRemediationRecommendationContent {
        title = normalizeRequiredText(
                title,
                "Recommendation title",
                TITLE_MAX_LENGTH
        );

        summary = normalizeRequiredText(
                summary,
                "Recommendation summary"
        );

        remediationSteps = normalizeRequiredText(
                remediationSteps,
                "Recommendation remediation steps"
        );

        verificationSteps = normalizeRequiredText(
                verificationSteps,
                "Recommendation verification steps"
        );
    }

    private static String normalizeRequiredText(
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

    private static String normalizeRequiredText(
            String value,
            String fieldName,
            int maximumLength
    ) {
        String normalizedValue =
                normalizeRequiredText(
                        value,
                        fieldName
                );

        if (normalizedValue.length() > maximumLength) {
            throw new IllegalArgumentException(
                    fieldName
                            + " must not exceed "
                            + maximumLength
                            + " characters."
            );
        }

        return normalizedValue;
    }
}