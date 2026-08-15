package com.cigabyte.sitesentinel.recommendation;

import com.cigabyte.sitesentinel.risk.RiskSeverity;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RiskRemediationRuleBasedFallbackGeneratorTests {

    private final RiskRemediationRuleBasedFallbackGenerator
            generator =
            new RiskRemediationRuleBasedFallbackGenerator();

    @Test
    void generatesDeterministicVersionedFallback() {
        RiskRemediationRecommendationContext context =
                context(
                        "TLS_CONFIGURATION",
                        RiskSeverity.HIGH);

        RiskRemediationRuleBasedFallbackResult first =
                generator.generate(context);

        RiskRemediationRuleBasedFallbackResult second =
                generator.generate(context);

        assertEquals(first, second);

        assertEquals(
                "risk-remediation-fallback-v2",
                first.ruleVersion()
        );

        assertEquals(
                "Review and remediate TLS_CONFIGURATION",
                first.content().title()
        );
    }

    @Test
    void usesSeverityAwareCriticalGuidance() {
        RiskRemediationRuleBasedFallbackResult result =
                generator.generate(
                        context(
                                "TLS_CONFIGURATION",
                                RiskSeverity.CRITICAL)
                );

        assertTrue(
                result.content()
                        .remediationSteps()
                        .contains(
                                "immediate controlled containment"
                        )
        );

        assertTrue(
                result.content()
                        .remediationSteps()
                        .contains(
                                "rollback readiness"
                        )
        );
    }

    @Test
    void doesNotEchoRationaleFindingOrEvidenceFreeText() {
        RiskRemediationRuleBasedFallbackResult result =
                generator.generate(
                        context(
                                "TLS_CONFIGURATION",
                                RiskSeverity.MEDIUM)
                );

        String generatedContent =
                result.content().title()
                        + result.content().summary()
                        + result.content().remediationSteps()
                        + result.content().verificationSteps();

        assertFalse(
                generatedContent.contains(
                        "DO_NOT_ECHO_RISK_RATIONALE"
                )
        );

        assertFalse(
                generatedContent.contains(
                        "DO_NOT_ECHO_FINDING_DESCRIPTION"
                )
        );

        assertFalse(
                generatedContent.contains(
                        "DO_NOT_ECHO_NORMALIZED_EVIDENCE"
                )
        );
    }

    @Test
    void providesRiskSpecificExplanationsForSupportedRiskTypes() {
        assertRiskSpecificSummary(
                "WEBSITE_REACHABILITY_RISK",
                "homepage could not be fetched"
        );

        assertRiskSpecificSummary(
                "WEBSITE_AVAILABILITY_RISK",
                "HTTP error status"
        );

        assertRiskSpecificSummary(
                "TRANSPORT_SECURITY_RISK",
                "not served over HTTPS"
        );

        assertRiskSpecificSummary(
                "BROWSER_SECURITY_POLICY_RISK",
                "Content Security Policy"
        );

        assertRiskSpecificSummary(
                "TRANSPORT_SECURITY_POLICY_RISK",
                "Strict Transport Security"
        );

        assertRiskSpecificSummary(
                "CLICKJACKING_PROTECTION_RISK",
                "framing protection"
        );

        assertRiskSpecificSummary(
                "CONTENT_SNIFFING_PROTECTION_RISK",
                "MIME type sniffing"
        );

        assertRiskSpecificSummary(
                "REFERRER_PRIVACY_POLICY_RISK",
                "referrer information"
        );

        assertRiskSpecificSummary(
                "CONTENT_QUALITY_RISK",
                "usable page title"
        );

        assertRiskSpecificSummary(
                "SEARCH_PRESENTATION_RISK",
                "meta description"
        );

        assertRiskSpecificSummary(
                "CANONICALIZATION_RISK",
                "canonical URL"
        );

        assertRiskSpecificSummary(
                "ASSESSMENT_DATA_QUALITY_RISK",
                "HTTP status evidence"
        );
    }

    @Test
    void riskSpecificSummaryExplainsEvidenceBoundaryWithoutIncidentClaim() {
        RiskRemediationRuleBasedFallbackResult result =
                generator.generate(
                        context(
                                "BROWSER_SECURITY_POLICY_RISK",
                                RiskSeverity.HIGH
                        )
                );

        String summary =
                result.content().summary();

        assertTrue(
                summary.contains(
                        "The persisted evidence confirms"
                )
        );

        assertTrue(
                summary.contains(
                        "The evidence does not confirm"
                )
        );

        assertTrue(
                summary.contains(
                        "may"
                )
        );

        assertFalse(
                summary.contains(
                        "was exploited"
                )
        );

        assertFalse(
                summary.contains(
                        "a data breach occurred"
                )
        );
    }

    @Test
    void preservesSafeGenericExplanationForUnknownFutureRiskType() {
        RiskRemediationRuleBasedFallbackResult result =
                generator.generate(
                        context(
                                "FUTURE_UNKNOWN_RISK",
                                RiskSeverity.MEDIUM
                        )
                );

        String summary =
                result.content().summary();

        assertTrue(
                summary.contains(
                        "FUTURE_UNKNOWN_RISK"
                )
        );

        assertTrue(
                summary.contains(
                        "The persisted evidence confirms"
                )
        );

        assertTrue(
                summary.contains(
                        "The evidence does not confirm"
                )
        );

        assertTrue(
                summary.contains(
                        "potential impact"
                )
        );

        assertFalse(
                summary.contains(
                        "was exploited"
                )
        );
    }

    private void assertRiskSpecificSummary(
            String riskType,
            String expectedExplanation
    ) {
        RiskRemediationRuleBasedFallbackResult result =
                generator.generate(
                        context(
                                riskType,
                                RiskSeverity.MEDIUM
                        )
                );

        String summary =
                result.content().summary();

        assertTrue(
                summary.contains(
                        expectedExplanation
                ),
                () -> "Missing risk-specific explanation for "
                        + riskType
        );
    }

    private RiskRemediationRecommendationContext context(
            String riskType,
            RiskSeverity severity
    ) {
        RiskRemediationRecommendationEvidenceContext evidence =
                new RiskRemediationRecommendationEvidenceContext(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "HEADER",
                        "DO_NOT_ECHO_NORMALIZED_EVIDENCE"
                );

        RiskRemediationRecommendationFindingContext finding =
                new RiskRemediationRecommendationFindingContext(
                        UUID.randomUUID(),
                        "SECURITY_HEADER",
                        "Persisted finding title",
                        "DO_NOT_ECHO_FINDING_DESCRIPTION",
                        88,
                        List.of(evidence)
                );

        return RiskRemediationRecommendationContext.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                riskType,
                severity,
                82,
                91,
                "DO_NOT_ECHO_RISK_RATIONALE",
                List.of(finding)
        );
    }
}