package com.cigabyte.sitesentinel.recommendation;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class RiskRemediationRecommendationValidationResult {

    private final RiskRemediationRecommendationValidationStatus
            status;

    private final RiskRemediationRecommendationContent
            content;

    private final List<RiskRemediationRecommendationValidationIssueCode>
            issues;

    private RiskRemediationRecommendationValidationResult(
            RiskRemediationRecommendationValidationStatus status,
            RiskRemediationRecommendationContent content,
            List<RiskRemediationRecommendationValidationIssueCode>
                    issues
    ) {
        this.status = Objects.requireNonNull(
                status,
                "Recommendation validation status is required."
        );

        this.content = content;

        this.issues = List.copyOf(
                Objects.requireNonNull(
                        issues,
                        "Recommendation validation issues are required."
                )
        );

        validateState();
    }

    public static RiskRemediationRecommendationValidationResult valid(
            RiskRemediationRecommendationContent content
    ) {
        return new RiskRemediationRecommendationValidationResult(
                RiskRemediationRecommendationValidationStatus.VALID,
                Objects.requireNonNull(
                        content,
                        "Validated recommendation content is required."
                ),
                List.of()
        );
    }

    public static RiskRemediationRecommendationValidationResult invalid(
            List<RiskRemediationRecommendationValidationIssueCode>
                    issues
    ) {
        return new RiskRemediationRecommendationValidationResult(
                RiskRemediationRecommendationValidationStatus.INVALID,
                null,
                issues
        );
    }

    private void validateState() {
        if (status
                == RiskRemediationRecommendationValidationStatus.VALID) {

            if (content == null) {
                throw new IllegalArgumentException(
                        "Valid recommendation result requires content."
                );
            }

            if (!issues.isEmpty()) {
                throw new IllegalArgumentException(
                        "Valid recommendation result must not contain issues."
                );
            }

            return;
        }

        if (content != null) {
            throw new IllegalArgumentException(
                    "Invalid recommendation result must not contain content."
            );
        }

        if (issues.isEmpty()) {
            throw new IllegalArgumentException(
                    "Invalid recommendation result requires "
                            + "at least one validation issue."
            );
        }
    }

    public RiskRemediationRecommendationValidationStatus
    getStatus() {
        return status;
    }

    public Optional<RiskRemediationRecommendationContent>
    getContent() {
        return Optional.ofNullable(content);
    }

    public List<RiskRemediationRecommendationValidationIssueCode>
    getIssues() {
        return issues;
    }

    public boolean isValid() {
        return status
                == RiskRemediationRecommendationValidationStatus.VALID;
    }
}