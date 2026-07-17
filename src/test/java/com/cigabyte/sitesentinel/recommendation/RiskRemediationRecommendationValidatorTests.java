package com.cigabyte.sitesentinel.recommendation;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RiskRemediationRecommendationValidatorTests {

    private final RiskRemediationRecommendationValidator
            validator =
            new RiskRemediationRecommendationValidator(
                    new RiskRemediationRecommendationContextSanitizer()
            );

    @Test
    void acceptsValidStructuredOutputAndFormatsSteps() {
        RiskRemediationAiOutput output =
                new RiskRemediationAiOutput(
                        "risk-remediation-output-v1",
                        "  Correct the persisted condition  ",
                        "  Apply a controlled remediation.  ",
                        List.of(
                                "Review the affected control.",
                                "Apply the corrective configuration."
                        ),
                        List.of(
                                "Run the same monitoring checks.",
                                "Confirm the condition is resolved."
                        ),
                        true
                );

        RiskRemediationRecommendationValidationResult result =
                validator.validate(
                        output,
                        RiskRemediationPromptVersion.V1
                );

        assertTrue(result.isValid());

        RiskRemediationRecommendationContent content =
                result.getContent().orElseThrow();

        assertEquals(
                "Correct the persisted condition",
                content.title()
        );

        assertEquals(
                "Apply a controlled remediation.",
                content.summary()
        );

        assertEquals(
                """
                1. Review the affected control.
                2. Apply the corrective configuration.
                """.strip(),
                content.remediationSteps()
        );

        assertEquals(
                """
                1. Run the same monitoring checks.
                2. Confirm the condition is resolved.
                """.strip(),
                content.verificationSteps()
        );

        assertTrue(result.getIssues().isEmpty());
    }

    @Test
    void rejectsMismatchedOutputSchemaVersion() {
        RiskRemediationAiOutput output =
                validOutput(
                        "unsupported-output-version"
                );

        RiskRemediationRecommendationValidationResult result =
                validator.validate(
                        output,
                        RiskRemediationPromptVersion.V1
                );

        assertFalse(result.isValid());

        assertTrue(
                result.getIssues().contains(
                        RiskRemediationRecommendationValidationIssueCode
                                .SCHEMA_VERSION_MISMATCH
                )
        );

        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void rejectsNonAdvisoryOutput() {
        RiskRemediationAiOutput output =
                new RiskRemediationAiOutput(
                        "risk-remediation-output-v1",
                        "Correct the condition",
                        "Controlled recommendation.",
                        List.of("Apply the correction."),
                        List.of("Verify the correction."),
                        false
                );

        RiskRemediationRecommendationValidationResult result =
                validator.validate(
                        output,
                        RiskRemediationPromptVersion.V1
                );

        assertFalse(result.isValid());

        assertTrue(
                result.getIssues().contains(
                        RiskRemediationRecommendationValidationIssueCode
                                .ADVISORY_MUST_BE_TRUE
                )
        );
    }

    @Test
    void rejectsSensitiveMaterialInAiOutput() {
        RiskRemediationAiOutput output =
                new RiskRemediationAiOutput(
                        "risk-remediation-output-v1",
                        "Correct the condition",
                        "Use Bearer sensitive-provider-token "
                                + "while applying the change.",
                        List.of("Apply the correction."),
                        List.of("Verify the correction."),
                        true
                );

        RiskRemediationRecommendationValidationResult result =
                validator.validate(
                        output,
                        RiskRemediationPromptVersion.V1
                );

        assertFalse(result.isValid());

        assertTrue(
                result.getIssues().contains(
                        RiskRemediationRecommendationValidationIssueCode
                                .SENSITIVE_CONTENT_DETECTED
                )
        );
    }

    @Test
    void rejectsNullStepItemWithoutThrowing() {
        List<String> remediationSteps =
                new ArrayList<>();

        remediationSteps.add(
                "Review the affected control."
        );

        remediationSteps.add(null);

        RiskRemediationAiOutput output =
                new RiskRemediationAiOutput(
                        "risk-remediation-output-v1",
                        "Correct the condition",
                        "Controlled recommendation.",
                        remediationSteps,
                        List.of("Verify the correction."),
                        true
                );

        RiskRemediationRecommendationValidationResult result =
                validator.validate(
                        output,
                        RiskRemediationPromptVersion.V1
                );

        assertFalse(result.isValid());

        assertTrue(
                result.getIssues().contains(
                        RiskRemediationRecommendationValidationIssueCode
                                .REMEDIATION_STEP_REQUIRED
                )
        );
    }

    @Test
    void rejectsStepCountAboveContractLimit() {
        RiskRemediationAiOutput output =
                new RiskRemediationAiOutput(
                        "risk-remediation-output-v1",
                        "Correct the condition",
                        "Controlled recommendation.",
                        Collections.nCopies(
                                13,
                                "Apply one controlled action."
                        ),
                        List.of("Verify the correction."),
                        true
                );

        RiskRemediationRecommendationValidationResult result =
                validator.validate(
                        output,
                        RiskRemediationPromptVersion.V1
                );

        assertFalse(result.isValid());

        assertTrue(
                result.getIssues().contains(
                        RiskRemediationRecommendationValidationIssueCode
                                .REMEDIATION_STEP_COUNT_EXCEEDED
                )
        );
    }

    private RiskRemediationAiOutput validOutput(
            String schemaVersion
    ) {
        return new RiskRemediationAiOutput(
                schemaVersion,
                "Correct the persisted condition",
                "Apply a controlled remediation.",
                List.of(
                        "Review the affected control.",
                        "Apply the corrective change."
                ),
                List.of(
                        "Re-run the monitoring checks."
                ),
                true
        );
    }
}