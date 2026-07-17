package com.cigabyte.sitesentinel.recommendation;

import com.cigabyte.sitesentinel.risk.RiskSeverity;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RiskRemediationPromptAndContextTests {

    private final RiskRemediationPromptFactory promptFactory =
            new RiskRemediationPromptFactory();

    @Test
    void sameOrderedContextProducesSameSha256Fingerprint() {
        ContextFixture fixture = fixture(
                "strict-transport-security-present"
        );

        RiskRemediationRecommendationContext first =
                fixture.context();

        RiskRemediationRecommendationContext second =
                createContext(
                        fixture.monitoringRunId(),
                        fixture.riskId(),
                        fixture.findingId(),
                        fixture.normalizedEvidenceId(),
                        fixture.collectedEvidenceId(),
                        "strict-transport-security-present"
                );

        assertEquals(
                first.getFingerprint(),
                second.getFingerprint()
        );

        assertTrue(
                first.getFingerprint()
                        .matches("[0-9a-f]{64}")
        );

        assertEquals(1, first.getFindingCount());
        assertEquals(1, first.getEvidenceCount());
    }

    @Test
    void changedNormalizedEvidenceChangesFingerprint() {
        ContextFixture fixture = fixture(
                "strict-transport-security-present"
        );

        RiskRemediationRecommendationContext changed =
                createContext(
                        fixture.monitoringRunId(),
                        fixture.riskId(),
                        fixture.findingId(),
                        fixture.normalizedEvidenceId(),
                        fixture.collectedEvidenceId(),
                        "strict-transport-security-missing"
                );

        assertNotEquals(
                fixture.context().getFingerprint(),
                changed.getFingerprint()
        );
    }

    @Test
    void promptRequestContainsRequiredVersionsAndFingerprint() {
        ContextFixture fixture = fixture(
                "strict-transport-security-present"
        );

        RiskRemediationAiRequest request =
                promptFactory.create(
                        fixture.context()
                );

        assertEquals(
                "risk-remediation-v1",
                request.promptVersion()
        );

        assertEquals(
                "risk-remediation-output-v1",
                request.outputSchemaVersion()
        );

        assertEquals(
                fixture.context().getFingerprint(),
                request.contextFingerprint()
        );

        assertTrue(
                request.userInstruction().contains(
                        "\"schemaVersion\": "
                                + "\"risk-remediation-output-v1\""
                )
        );

        assertTrue(
                request.userInstruction().contains(
                        "strict-transport-security-present"
                )
        );

        assertTrue(
                request.systemInstruction().contains(
                        "Do not create or infer a new risk."
                )
        );

        assertTrue(
                request.systemInstruction().contains(
                        "Do not change risk severity, risk score, "
                                + "confidence score, or trust score."
                )
        );
    }

    @Test
    void promptPayloadExcludesDatabaseTraceabilityIdentifiers() {
        ContextFixture fixture = fixture(
                "strict-transport-security-present"
        );

        RiskRemediationAiRequest request =
                promptFactory.create(
                        fixture.context()
                );

        String prompt = request.userInstruction();

        assertFalse(
                prompt.contains(
                        fixture.monitoringRunId().toString()
                )
        );

        assertFalse(
                prompt.contains(
                        fixture.riskId().toString()
                )
        );

        assertFalse(
                prompt.contains(
                        fixture.findingId().toString()
                )
        );

        assertFalse(
                prompt.contains(
                        fixture.normalizedEvidenceId().toString()
                )
        );

        assertFalse(
                prompt.contains(
                        fixture.collectedEvidenceId().toString()
                )
        );

        assertFalse(
                prompt.contains("\"monitoringRunId\"")
        );

        assertFalse(
                prompt.contains("\"riskId\"")
        );

        assertFalse(
                prompt.contains("\"findingId\"")
        );

        assertFalse(
                prompt.contains(
                        "\"normalizedEvidenceId\""
                )
        );

        assertFalse(
                prompt.contains(
                        "\"collectedEvidenceId\""
                )
        );
    }

    private ContextFixture fixture(
            String normalizedEvidenceValue
    ) {
        UUID monitoringRunId = UUID.randomUUID();
        UUID riskId = UUID.randomUUID();
        UUID findingId = UUID.randomUUID();
        UUID normalizedEvidenceId = UUID.randomUUID();
        UUID collectedEvidenceId = UUID.randomUUID();

        RiskRemediationRecommendationContext context =
                createContext(
                        monitoringRunId,
                        riskId,
                        findingId,
                        normalizedEvidenceId,
                        collectedEvidenceId,
                        normalizedEvidenceValue
                );

        return new ContextFixture(
                monitoringRunId,
                riskId,
                findingId,
                normalizedEvidenceId,
                collectedEvidenceId,
                context
        );
    }

    private RiskRemediationRecommendationContext createContext(
            UUID monitoringRunId,
            UUID riskId,
            UUID findingId,
            UUID normalizedEvidenceId,
            UUID collectedEvidenceId,
            String normalizedEvidenceValue
    ) {
        RiskRemediationRecommendationEvidenceContext evidence =
                new RiskRemediationRecommendationEvidenceContext(
                        normalizedEvidenceId,
                        collectedEvidenceId,
                        "SECURITY_HEADER",
                        normalizedEvidenceValue
                );

        RiskRemediationRecommendationFindingContext finding =
                new RiskRemediationRecommendationFindingContext(
                        findingId,
                        "SECURITY_HEADER",
                        "Strict transport security finding",
                        "Persisted normalized header assessment.",
                        93,
                        List.of(evidence)
                );

        return RiskRemediationRecommendationContext.create(
                monitoringRunId,
                riskId,
                "TLS_CONFIGURATION",
                RiskSeverity.HIGH,
                86,
                92,
                "Persisted risk rationale.",
                List.of(finding)
        );
    }

    private record ContextFixture(
            UUID monitoringRunId,
            UUID riskId,
            UUID findingId,
            UUID normalizedEvidenceId,
            UUID collectedEvidenceId,
            RiskRemediationRecommendationContext context
    ) {
    }
}