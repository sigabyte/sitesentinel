package com.cigabyte.sitesentinel.recommendation;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class RiskRemediationRecommendationValidator {

    private static final int TITLE_MAX_LENGTH = 220;
    private static final int SUMMARY_MAX_LENGTH = 3000;

    private static final int STEP_MAX_LENGTH = 1200;
    private static final int STEP_MAX_COUNT = 12;

    private final RiskRemediationRecommendationContextSanitizer
            textSafetyBoundary;

    public RiskRemediationRecommendationValidator(
            RiskRemediationRecommendationContextSanitizer
                    textSafetyBoundary
    ) {
        this.textSafetyBoundary = textSafetyBoundary;
    }

    public RiskRemediationRecommendationValidationResult validate(
            RiskRemediationAiOutput output,
            RiskRemediationPromptVersion promptVersion
    ) {
        RiskRemediationPromptVersion requiredPromptVersion =
                Objects.requireNonNull(
                        promptVersion,
                        "Risk remediation prompt version is required."
                );

        if (output == null) {
            return RiskRemediationRecommendationValidationResult
                    .invalid(
                            List.of(
                                    RiskRemediationRecommendationValidationIssueCode
                                            .OUTPUT_REQUIRED
                            )
                    );
        }

        List<RiskRemediationRecommendationValidationIssueCode>
                issues = new ArrayList<>();

        validateSchemaVersion(
                output.schemaVersion(),
                requiredPromptVersion.getOutputSchemaVersion(),
                issues
        );

        String title = validateText(
                output.title(),
                TITLE_MAX_LENGTH,
                RiskRemediationRecommendationValidationIssueCode
                        .TITLE_REQUIRED,
                RiskRemediationRecommendationValidationIssueCode
                        .TITLE_TOO_LONG,
                issues
        );

        String summary = validateText(
                output.summary(),
                SUMMARY_MAX_LENGTH,
                RiskRemediationRecommendationValidationIssueCode
                        .SUMMARY_REQUIRED,
                RiskRemediationRecommendationValidationIssueCode
                        .SUMMARY_TOO_LONG,
                issues
        );

        List<String> remediationSteps = validateSteps(
                output.remediationSteps(),
                RiskRemediationRecommendationValidationIssueCode
                        .REMEDIATION_STEPS_REQUIRED,
                RiskRemediationRecommendationValidationIssueCode
                        .REMEDIATION_STEP_REQUIRED,
                RiskRemediationRecommendationValidationIssueCode
                        .REMEDIATION_STEP_TOO_LONG,
                RiskRemediationRecommendationValidationIssueCode
                        .REMEDIATION_STEP_COUNT_EXCEEDED,
                issues
        );

        List<String> verificationSteps = validateSteps(
                output.verificationSteps(),
                RiskRemediationRecommendationValidationIssueCode
                        .VERIFICATION_STEPS_REQUIRED,
                RiskRemediationRecommendationValidationIssueCode
                        .VERIFICATION_STEP_REQUIRED,
                RiskRemediationRecommendationValidationIssueCode
                        .VERIFICATION_STEP_TOO_LONG,
                RiskRemediationRecommendationValidationIssueCode
                        .VERIFICATION_STEP_COUNT_EXCEEDED,
                issues
        );

        validateAdvisory(
                output.advisory(),
                issues
        );

        validateSensitiveContent(
                title,
                summary,
                remediationSteps,
                verificationSteps,
                issues
        );

        if (!issues.isEmpty()) {
            return RiskRemediationRecommendationValidationResult
                    .invalid(
                            distinctIssues(issues)
                    );
        }

        try {
            RiskRemediationRecommendationContent content =
                    new RiskRemediationRecommendationContent(
                            title,
                            summary,
                            formatSteps(remediationSteps),
                            formatSteps(verificationSteps)
                    );

            return RiskRemediationRecommendationValidationResult
                    .valid(content);
        } catch (IllegalArgumentException exception) {
            return RiskRemediationRecommendationValidationResult
                    .invalid(
                            List.of(
                                    RiskRemediationRecommendationValidationIssueCode
                                            .CONTENT_CONTRACT_VIOLATION
                            )
                    );
        }
    }

    private void validateSchemaVersion(
            String actualSchemaVersion,
            String expectedSchemaVersion,
            List<RiskRemediationRecommendationValidationIssueCode>
                    issues
    ) {
        String normalizedSchemaVersion =
                normalizeOptionalText(
                        actualSchemaVersion
                );

        if (normalizedSchemaVersion == null) {
            issues.add(
                    RiskRemediationRecommendationValidationIssueCode
                            .SCHEMA_VERSION_REQUIRED
            );

            return;
        }

        if (!expectedSchemaVersion.equals(
                normalizedSchemaVersion
        )) {
            issues.add(
                    RiskRemediationRecommendationValidationIssueCode
                            .SCHEMA_VERSION_MISMATCH
            );
        }
    }

    private String validateText(
            String value,
            int maximumLength,
            RiskRemediationRecommendationValidationIssueCode
                    requiredIssue,
            RiskRemediationRecommendationValidationIssueCode
                    lengthIssue,
            List<RiskRemediationRecommendationValidationIssueCode>
                    issues
    ) {
        String normalizedValue =
                normalizeOptionalText(value);

        if (normalizedValue == null) {
            issues.add(requiredIssue);
            return null;
        }

        if (normalizedValue.length() > maximumLength) {
            issues.add(lengthIssue);
            return null;
        }

        return normalizedValue;
    }

    private List<String> validateSteps(
            List<String> values,
            RiskRemediationRecommendationValidationIssueCode
                    requiredListIssue,
            RiskRemediationRecommendationValidationIssueCode
                    requiredItemIssue,
            RiskRemediationRecommendationValidationIssueCode
                    itemLengthIssue,
            RiskRemediationRecommendationValidationIssueCode
                    countIssue,
            List<RiskRemediationRecommendationValidationIssueCode>
                    issues
    ) {
        if (values == null || values.isEmpty()) {
            issues.add(requiredListIssue);
            return List.of();
        }

        if (values.size() > STEP_MAX_COUNT) {
            issues.add(countIssue);
        }

        int validationLimit = Math.min(
                values.size(),
                STEP_MAX_COUNT
        );

        List<String> normalizedValues =
                new ArrayList<>();

        for (int index = 0;
             index < validationLimit;
             index++) {

            String normalizedValue =
                    normalizeOptionalText(
                            values.get(index)
                    );

            if (normalizedValue == null) {
                issues.add(requiredItemIssue);
                continue;
            }

            if (normalizedValue.length()
                    > STEP_MAX_LENGTH) {

                issues.add(itemLengthIssue);
                continue;
            }

            normalizedValues.add(
                    normalizedValue
            );
        }

        return List.copyOf(normalizedValues);
    }

    private void validateAdvisory(
            Boolean advisory,
            List<RiskRemediationRecommendationValidationIssueCode>
                    issues
    ) {
        if (advisory == null) {
            issues.add(
                    RiskRemediationRecommendationValidationIssueCode
                            .ADVISORY_REQUIRED
            );

            return;
        }

        if (!advisory) {
            issues.add(
                    RiskRemediationRecommendationValidationIssueCode
                            .ADVISORY_MUST_BE_TRUE
            );
        }
    }

    private void validateSensitiveContent(
            String title,
            String summary,
            List<String> remediationSteps,
            List<String> verificationSteps,
            List<RiskRemediationRecommendationValidationIssueCode>
                    issues
    ) {
        boolean sensitiveContentDetected =
                textSafetyBoundary
                        .containsSensitiveMaterial(title)
                        || textSafetyBoundary
                        .containsSensitiveMaterial(summary)
                        || containsSensitiveMaterial(
                        remediationSteps
                )
                        || containsSensitiveMaterial(
                        verificationSteps
                );

        if (sensitiveContentDetected) {
            issues.add(
                    RiskRemediationRecommendationValidationIssueCode
                            .SENSITIVE_CONTENT_DETECTED
            );
        }
    }

    private boolean containsSensitiveMaterial(
            List<String> values
    ) {
        for (String value : values) {
            if (textSafetyBoundary
                    .containsSensitiveMaterial(value)) {

                return true;
            }
        }

        return false;
    }

    private String formatSteps(
            List<String> steps
    ) {
        StringBuilder formattedSteps =
                new StringBuilder();

        for (int index = 0;
             index < steps.size();
             index++) {

            if (index > 0) {
                formattedSteps.append('\n');
            }

            formattedSteps
                    .append(index + 1)
                    .append(". ")
                    .append(steps.get(index));
        }

        return formattedSteps.toString();
    }

    private String normalizeOptionalText(
            String value
    ) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.strip();
    }

    private List<RiskRemediationRecommendationValidationIssueCode>
    distinctIssues(
            List<RiskRemediationRecommendationValidationIssueCode>
                    issues
    ) {
        return issues.stream()
                .distinct()
                .toList();
    }
}