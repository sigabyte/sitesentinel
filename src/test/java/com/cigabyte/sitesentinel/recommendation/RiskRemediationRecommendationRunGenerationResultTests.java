package com.cigabyte.sitesentinel.recommendation;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RiskRemediationRecommendationRunGenerationResultTests {

    @Test
    void skippedRecommendationsRemainSuccessfulLifecycleOutcomes() {
        UUID monitoringRunId =
                UUID.randomUUID();

        RiskRemediationRecommendationRunGenerationResult result =
                new RiskRemediationRecommendationRunGenerationResult(
                        monitoringRunId,
                        3,
                        1,
                        2,
                        0
                );

        assertEquals(
                monitoringRunId,
                result.monitoringRunId()
        );

        assertEquals(3, result.riskCount());
        assertEquals(1, result.generatedCount());
        assertEquals(2, result.skippedCount());
        assertEquals(0, result.failedCount());

        assertTrue(result.isFullySuccessful());
        assertFalse(result.isEmpty());
    }

    @Test
    void rejectsLifecycleCountsThatDoNotEqualRiskCount() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RiskRemediationRecommendationRunGenerationResult(
                        UUID.randomUUID(),
                        3,
                        1,
                        1,
                        0
                )
        );
    }

    @Test
    void rejectsNegativeSkippedCount() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RiskRemediationRecommendationRunGenerationResult(
                        UUID.randomUUID(),
                        1,
                        1,
                        -1,
                        1
                )
        );
    }

    @Test
    void legacyConstructorPreservesZeroSkippedCompatibility() {
        RiskRemediationRecommendationRunGenerationResult result =
                new RiskRemediationRecommendationRunGenerationResult(
                        UUID.randomUUID(),
                        2,
                        1,
                        1
                );

        assertEquals(2, result.riskCount());
        assertEquals(1, result.generatedCount());
        assertEquals(0, result.skippedCount());
        assertEquals(1, result.failedCount());

        assertFalse(result.isFullySuccessful());
        assertFalse(result.isEmpty());
    }
}
