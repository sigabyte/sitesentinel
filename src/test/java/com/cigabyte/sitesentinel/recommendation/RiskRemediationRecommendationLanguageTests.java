package com.cigabyte.sitesentinel.recommendation;

import com.cigabyte.sitesentinel.reporting.SiteSentinelReportLanguage;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RiskRemediationRecommendationLanguageTests {

    private static final UUID MONITORING_RUN_ID =
            UUID.fromString(
                    "3ee4b7bb-9a56-4c21-b67d-dce5c14f3bd2"
            );

    private static final UUID RISK_ID =
            UUID.fromString(
                    "be2c7353-d178-43f1-ab26-034cfe30a02b"
            );

    private static final String CONTEXT_FINGERPRINT =
            "b".repeat(64);

    private static final OffsetDateTime GENERATED_AT =
            OffsetDateTime.of(
                    2026,
                    8,
                    17,
                    12,
                    45,
                    0,
                    0,
                    ZoneOffset.UTC
            );

    @Test
    void createsEnglishRecommendationWithExplicitLanguage() {
        RiskRemediationRecommendation recommendation =
                createAiRecommendation(
                        SiteSentinelReportLanguage.ENGLISH
                );

        assertEquals(
                SiteSentinelReportLanguage.ENGLISH,
                recommendation.getReportLanguage()
        );
    }

    @Test
    void createsTurkishRecommendationWithExplicitLanguage() {
        RiskRemediationRecommendation recommendation =
                createAiRecommendation(
                        SiteSentinelReportLanguage.TURKISH
                );

        assertEquals(
                SiteSentinelReportLanguage.TURKISH,
                recommendation.getReportLanguage()
        );
    }

    @Test
    void rejectsMissingRecommendationLanguage() {
        assertThrows(
                NullPointerException.class,
                () -> createAiRecommendation(
                        null
                )
        );
    }

    @Test
    void createsTurkishFallbackRecommendationWithExplicitLanguage() {
        RiskRemediationRecommendation recommendation =
                RiskRemediationRecommendation.ruleBasedFallback(
                        MONITORING_RUN_ID,
                        RISK_ID,
                        SiteSentinelReportLanguage.TURKISH,
                        recommendationContent(),
                        RiskRemediationFallbackReason.PROVIDER_UNAVAILABLE,
                        null,
                        null,
                        "risk-remediation-v3",
                        "risk-remediation-fallback-v3",
                        CONTEXT_FINGERPRINT,
                        2,
                        3,
                        GENERATED_AT
                );

        assertEquals(
                SiteSentinelReportLanguage.TURKISH,
                recommendation.getReportLanguage()
        );

        assertEquals(
                RiskRemediationRecommendationSource
                        .RULE_BASED_FALLBACK,
                recommendation.getSource()
        );

        assertEquals(
                RiskRemediationFallbackReason
                        .PROVIDER_UNAVAILABLE,
                recommendation.getFallbackReason()
        );
    }

    private RiskRemediationRecommendation
    createAiRecommendation(
            SiteSentinelReportLanguage reportLanguage
    ) {
        return RiskRemediationRecommendation.aiGenerated(
                MONITORING_RUN_ID,
                RISK_ID,
                reportLanguage,
                recommendationContent(),
                "controlled-ai-provider",
                "controlled-ai-model",
                "risk-remediation-v3",
                CONTEXT_FINGERPRINT,
                2,
                3,
                GENERATED_AT
        );
    }

    private RiskRemediationRecommendationContent
    recommendationContent() {
        return new RiskRemediationRecommendationContent(
                "Controlled recommendation",
                "Controlled evidence-bounded summary.",
                """
                1. Review the persisted condition.
                2. Apply the controlled remediation.
                """.strip(),
                """
                1. Re-run the monitoring checks.
                2. Confirm the condition is resolved.
                """.strip()
        );
    }
}