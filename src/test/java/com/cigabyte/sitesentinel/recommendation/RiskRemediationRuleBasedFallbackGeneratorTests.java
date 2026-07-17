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
                context(RiskSeverity.HIGH);

        RiskRemediationRuleBasedFallbackResult first =
                generator.generate(context);

        RiskRemediationRuleBasedFallbackResult second =
                generator.generate(context);

        assertEquals(first, second);

        assertEquals(
                "risk-remediation-fallback-v1",
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
                        context(RiskSeverity.CRITICAL)
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
                        context(RiskSeverity.MEDIUM)
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

    private RiskRemediationRecommendationContext context(
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
                "TLS_CONFIGURATION",
                severity,
                82,
                91,
                "DO_NOT_ECHO_RISK_RATIONALE",
                List.of(finding)
        );
    }
}