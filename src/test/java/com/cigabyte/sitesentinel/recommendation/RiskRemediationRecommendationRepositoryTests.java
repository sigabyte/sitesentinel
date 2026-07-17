package com.cigabyte.sitesentinel.recommendation;

import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
import com.cigabyte.sitesentinel.monitoring.MonitoringRunRepository;
import com.cigabyte.sitesentinel.risk.Risk;
import com.cigabyte.sitesentinel.risk.RiskRepository;
import com.cigabyte.sitesentinel.risk.RiskSeverity;
import com.cigabyte.sitesentinel.website.Website;
import com.cigabyte.sitesentinel.website.WebsiteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class RiskRemediationRecommendationRepositoryTests {

    private static final OffsetDateTime FUTURE_BASE_TIME =
            OffsetDateTime.of(
                    2500,
                    1,
                    1,
                    12,
                    0,
                    0,
                    0,
                    ZoneOffset.UTC
            );

    private static final String CONTEXT_FINGERPRINT =
            "a".repeat(64);

    private final WebsiteRepository websiteRepository;
    private final MonitoringRunRepository monitoringRunRepository;
    private final RiskRepository riskRepository;

    private final RiskRemediationRecommendationRepository
            recommendationRepository;

    private final RiskRemediationRecommendationService
            recommendationService;

    @Autowired
    RiskRemediationRecommendationRepositoryTests(
            WebsiteRepository websiteRepository,
            MonitoringRunRepository monitoringRunRepository,
            RiskRepository riskRepository,
            RiskRemediationRecommendationRepository
                    recommendationRepository,
            RiskRemediationRecommendationService
                    recommendationService
    ) {
        this.websiteRepository = websiteRepository;
        this.monitoringRunRepository =
                monitoringRunRepository;
        this.riskRepository = riskRepository;

        this.recommendationRepository =
                recommendationRepository;

        this.recommendationService =
                recommendationService;
    }

    @Test
    void findByMonitoringRunReturnsAscendingGenerationOrderAndFiltersOtherRun() {
        Website website = persistWebsite();

        MonitoringRun monitoringRun =
                persistCompletedRun(website);

        MonitoringRun otherMonitoringRun =
                persistCompletedRun(website);

        Risk firstRisk = persistRisk(
                website,
                monitoringRun,
                "TLS_CONFIGURATION"
        );

        Risk secondRisk = persistRisk(
                website,
                monitoringRun,
                "SECURITY_HEADERS"
        );

        Risk otherRunRisk = persistRisk(
                website,
                otherMonitoringRun,
                "CONTENT_INTEGRITY"
        );

        recommendationRepository.saveAndFlush(
                fallbackRecommendation(
                        monitoringRun,
                        firstRisk,
                        "repository-ordering-oldest",
                        FUTURE_BASE_TIME.plusMinutes(1),
                        RiskRemediationFallbackReason
                                .PROVIDER_UNAVAILABLE
                )
        );

        recommendationRepository.saveAndFlush(
                aiRecommendation(
                        monitoringRun,
                        secondRisk,
                        "repository-ordering-middle",
                        FUTURE_BASE_TIME.plusMinutes(2)
                )
        );

        recommendationRepository.saveAndFlush(
                fallbackRecommendation(
                        monitoringRun,
                        firstRisk,
                        "repository-ordering-latest",
                        FUTURE_BASE_TIME.plusMinutes(3),
                        RiskRemediationFallbackReason
                                .VALIDATION_FAILURE
                )
        );

        recommendationRepository.saveAndFlush(
                aiRecommendation(
                        otherMonitoringRun,
                        otherRunRisk,
                        "repository-ordering-other-run",
                        FUTURE_BASE_TIME.plusYears(1)
                )
        );

        List<RiskRemediationRecommendation> result =
                recommendationRepository
                        .findByMonitoringRunIdOrderByGeneratedAtAscCreatedAtAsc(
                                monitoringRun.getId()
                        );

        assertEquals(3, result.size());

        assertEquals(
                List.of(
                        "repository-ordering-oldest",
                        "repository-ordering-middle",
                        "repository-ordering-latest"
                ),
                result.stream()
                        .map(
                                RiskRemediationRecommendation
                                        ::getTitle
                        )
                        .toList()
        );

        assertTrue(
                result.stream().allMatch(
                        recommendation ->
                                monitoringRun.getId().equals(
                                        recommendation
                                                .getMonitoringRunId()
                                )
                )
        );

        assertTrue(
                result.stream().noneMatch(
                        recommendation ->
                                recommendation.getTitle().equals(
                                        "repository-ordering-other-run"
                                )
                )
        );
    }

    @Test
    void riskHistoryReturnsDescendingOrderAndLatestQueryReturnsNewest() {
        Website website = persistWebsite();

        MonitoringRun monitoringRun =
                persistCompletedRun(website);

        Risk targetRisk = persistRisk(
                website,
                monitoringRun,
                "TLS_CONFIGURATION"
        );

        Risk otherRisk = persistRisk(
                website,
                monitoringRun,
                "SECURITY_HEADERS"
        );

        recommendationRepository.saveAndFlush(
                fallbackRecommendation(
                        monitoringRun,
                        targetRisk,
                        "risk-history-oldest",
                        FUTURE_BASE_TIME.plusMinutes(1),
                        RiskRemediationFallbackReason
                                .PROVIDER_UNAVAILABLE
                )
        );

        recommendationRepository.saveAndFlush(
                aiRecommendation(
                        monitoringRun,
                        targetRisk,
                        "risk-history-middle",
                        FUTURE_BASE_TIME.plusMinutes(2)
                )
        );

        recommendationRepository.saveAndFlush(
                fallbackRecommendation(
                        monitoringRun,
                        targetRisk,
                        "risk-history-latest",
                        FUTURE_BASE_TIME.plusMinutes(3),
                        RiskRemediationFallbackReason
                                .VALIDATION_FAILURE
                )
        );

        recommendationRepository.saveAndFlush(
                aiRecommendation(
                        monitoringRun,
                        otherRisk,
                        "other-risk-future-recommendation",
                        FUTURE_BASE_TIME.plusYears(1)
                )
        );

        List<RiskRemediationRecommendation> history =
                recommendationRepository
                        .findByRiskIdAndMonitoringRunIdOrderByGeneratedAtDescCreatedAtDesc(
                                targetRisk.getId(),
                                monitoringRun.getId()
                        );

        assertEquals(3, history.size());

        assertEquals(
                List.of(
                        "risk-history-latest",
                        "risk-history-middle",
                        "risk-history-oldest"
                ),
                history.stream()
                        .map(
                                RiskRemediationRecommendation
                                        ::getTitle
                        )
                        .toList()
        );

        RiskRemediationRecommendation latest =
                recommendationRepository
                        .findFirstByRiskIdAndMonitoringRunIdOrderByGeneratedAtDescCreatedAtDesc(
                                targetRisk.getId(),
                                monitoringRun.getId()
                        )
                        .orElseThrow();

        assertEquals(
                "risk-history-latest",
                latest.getTitle()
        );

        assertEquals(
                FUTURE_BASE_TIME.plusMinutes(3),
                latest.getGeneratedAt()
        );
    }

    @Test
    void countByMonitoringRunDoesNotIncludeOtherRuns() {
        Website website = persistWebsite();

        MonitoringRun firstRun =
                persistCompletedRun(website);

        MonitoringRun secondRun =
                persistCompletedRun(website);

        Risk firstRunRisk = persistRisk(
                website,
                firstRun,
                "TLS_CONFIGURATION"
        );

        Risk secondRunRisk = persistRisk(
                website,
                secondRun,
                "SECURITY_HEADERS"
        );

        recommendationRepository.saveAndFlush(
                aiRecommendation(
                        firstRun,
                        firstRunRisk,
                        "first-run-ai",
                        FUTURE_BASE_TIME.plusMinutes(1)
                )
        );

        recommendationRepository.saveAndFlush(
                fallbackRecommendation(
                        firstRun,
                        firstRunRisk,
                        "first-run-fallback",
                        FUTURE_BASE_TIME.plusMinutes(2),
                        RiskRemediationFallbackReason
                                .VALIDATION_FAILURE
                )
        );

        recommendationRepository.saveAndFlush(
                fallbackRecommendation(
                        secondRun,
                        secondRunRisk,
                        "second-run-fallback",
                        FUTURE_BASE_TIME.plusMinutes(3),
                        RiskRemediationFallbackReason
                                .PROVIDER_UNAVAILABLE
                )
        );

        assertEquals(
                2,
                recommendationRepository
                        .countByMonitoringRunId(
                                firstRun.getId()
                        )
        );

        assertEquals(
                1,
                recommendationRepository
                        .countByMonitoringRunId(
                                secondRun.getId()
                        )
        );
    }

    @Test
    void persistsCompleteAiRecommendationAuditMetadata() {
        Website website = persistWebsite();

        MonitoringRun monitoringRun =
                persistCompletedRun(website);

        Risk risk = persistRisk(
                website,
                monitoringRun,
                "TLS_CONFIGURATION"
        );

        RiskRemediationRecommendation saved =
                recommendationRepository.saveAndFlush(
                        aiRecommendation(
                                monitoringRun,
                                risk,
                                "AI recommendation title",
                                FUTURE_BASE_TIME
                        )
                );

        assertNotNull(saved.getId());

        assertEquals(
                monitoringRun.getId(),
                saved.getMonitoringRunId()
        );

        assertEquals(
                risk.getId(),
                saved.getRiskId()
        );

        assertEquals(
                RiskRemediationRecommendationSource.AI,
                saved.getSource()
        );

        assertEquals(
                RiskRemediationFallbackReason.NONE,
                saved.getFallbackReason()
        );

        assertEquals(
                RiskRemediationRecommendationValidationStatus.VALID,
                saved.getValidationStatus()
        );

        assertTrue(saved.isAdvisory());

        assertEquals(
                "test-ai-provider",
                saved.getProviderName()
        );

        assertEquals(
                "test-ai-model",
                saved.getModelName()
        );

        assertEquals(
                "risk-remediation-v1",
                saved.getPromptVersion()
        );

        assertNull(saved.getFallbackRuleVersion());

        assertEquals(
                CONTEXT_FINGERPRINT,
                saved.getContextFingerprint()
        );

        assertEquals(
                2,
                saved.getContextFindingCount()
        );

        assertEquals(
                3,
                saved.getContextEvidenceCount()
        );

        assertEquals(
                FUTURE_BASE_TIME,
                saved.getGeneratedAt()
        );

        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void persistsCompleteFallbackRecommendationAuditMetadata() {
        Website website = persistWebsite();

        MonitoringRun monitoringRun =
                persistCompletedRun(website);

        Risk risk = persistRisk(
                website,
                monitoringRun,
                "SECURITY_HEADERS"
        );

        RiskRemediationRecommendation saved =
                recommendationRepository.saveAndFlush(
                        fallbackRecommendation(
                                monitoringRun,
                                risk,
                                "Fallback recommendation title",
                                FUTURE_BASE_TIME,
                                RiskRemediationFallbackReason
                                        .VALIDATION_FAILURE
                        )
                );

        assertNotNull(saved.getId());

        assertEquals(
                RiskRemediationRecommendationSource
                        .RULE_BASED_FALLBACK,
                saved.getSource()
        );

        assertEquals(
                RiskRemediationFallbackReason
                        .VALIDATION_FAILURE,
                saved.getFallbackReason()
        );

        assertEquals(
                RiskRemediationRecommendationValidationStatus.VALID,
                saved.getValidationStatus()
        );

        assertTrue(saved.isAdvisory());

        assertEquals(
                "attempted-provider",
                saved.getProviderName()
        );

        assertEquals(
                "attempted-model",
                saved.getModelName()
        );

        assertEquals(
                "risk-remediation-v1",
                saved.getPromptVersion()
        );

        assertEquals(
                "risk-remediation-fallback-v1",
                saved.getFallbackRuleVersion()
        );

        assertEquals(
                CONTEXT_FINGERPRINT,
                saved.getContextFingerprint()
        );

        assertEquals(
                2,
                saved.getContextFindingCount()
        );

        assertEquals(
                3,
                saved.getContextEvidenceCount()
        );

        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void persistenceServiceSavesRecommendationForMatchingRiskAndRun() {
        Website website = persistWebsite();

        MonitoringRun monitoringRun =
                persistCompletedRun(website);

        Risk risk = persistRisk(
                website,
                monitoringRun,
                "TLS_CONFIGURATION"
        );

        RiskRemediationRecommendation recommendation =
                fallbackRecommendation(
                        monitoringRun,
                        risk,
                        "service-valid-association",
                        FUTURE_BASE_TIME,
                        RiskRemediationFallbackReason
                                .PROVIDER_UNAVAILABLE
                );

        RiskRemediationRecommendation saved =
                recommendationService.saveValidated(
                        recommendation
                );

        recommendationRepository.flush();

        assertNotNull(saved.getId());

        assertEquals(
                1,
                recommendationRepository
                        .countByMonitoringRunId(
                                monitoringRun.getId()
                        )
        );

        assertTrue(
                recommendationRepository
                        .findById(saved.getId())
                        .isPresent()
        );
    }

    @Test
    void persistenceServiceRejectsRiskFromDifferentMonitoringRun() {
        Website website = persistWebsite();

        MonitoringRun riskMonitoringRun =
                persistCompletedRun(website);

        MonitoringRun requestedMonitoringRun =
                persistCompletedRun(website);

        Risk risk = persistRisk(
                website,
                riskMonitoringRun,
                "TLS_CONFIGURATION"
        );

        RiskRemediationRecommendation mismatchedRecommendation =
                RiskRemediationRecommendation
                        .ruleBasedFallback(
                                requestedMonitoringRun.getId(),
                                risk.getId(),
                                recommendationContent(
                                        "mismatched-association"
                                ),
                                RiskRemediationFallbackReason
                                        .PROVIDER_UNAVAILABLE,
                                null,
                                null,
                                "risk-remediation-v1",
                                "risk-remediation-fallback-v1",
                                CONTEXT_FINGERPRINT,
                                2,
                                3,
                                FUTURE_BASE_TIME
                        );

        assertThrows(
                IllegalArgumentException.class,
                () -> recommendationService
                        .saveValidated(
                                mismatchedRecommendation
                        )
        );

        assertEquals(
                0,
                recommendationRepository
                        .countByMonitoringRunId(
                                requestedMonitoringRun.getId()
                        )
        );

        assertFalse(
                recommendationRepository
                        .findFirstByRiskIdAndMonitoringRunIdOrderByGeneratedAtDescCreatedAtDesc(
                                risk.getId(),
                                requestedMonitoringRun.getId()
                        )
                        .isPresent()
        );
    }

    private Website persistWebsite() {
        String uniqueValue =
                UUID.randomUUID().toString();

        return websiteRepository.saveAndFlush(
                new Website(
                        "Recommendation Repository Test",
                        "repository-"
                                + uniqueValue
                                + ".example.test"
                )
        );
    }

    private MonitoringRun persistCompletedRun(
            Website website
    ) {
        MonitoringRun monitoringRun =
                new MonitoringRun(
                        website.getId()
                );

        monitoringRun.markRunning();
        monitoringRun.markCompleted();

        return monitoringRunRepository.saveAndFlush(
                monitoringRun
        );
    }

    private Risk persistRisk(
            Website website,
            MonitoringRun monitoringRun,
            String riskType
    ) {
        return riskRepository.saveAndFlush(
                new Risk(
                        website.getId(),
                        monitoringRun.getId(),
                        riskType,
                        RiskSeverity.HIGH,
                        82,
                        91,
                        "Persisted recommendation repository "
                                + "test rationale."
                )
        );
    }

    private RiskRemediationRecommendation aiRecommendation(
            MonitoringRun monitoringRun,
            Risk risk,
            String title,
            OffsetDateTime generatedAt
    ) {
        return RiskRemediationRecommendation
                .aiGenerated(
                        monitoringRun.getId(),
                        risk.getId(),
                        recommendationContent(title),
                        "test-ai-provider",
                        "test-ai-model",
                        "risk-remediation-v1",
                        CONTEXT_FINGERPRINT,
                        2,
                        3,
                        generatedAt
                );
    }

    private RiskRemediationRecommendation
    fallbackRecommendation(
            MonitoringRun monitoringRun,
            Risk risk,
            String title,
            OffsetDateTime generatedAt,
            RiskRemediationFallbackReason fallbackReason
    ) {
        String attemptedProviderName =
                fallbackReason
                        == RiskRemediationFallbackReason
                        .PROVIDER_UNAVAILABLE
                        ? null
                        : "attempted-provider";

        String attemptedModelName =
                fallbackReason
                        == RiskRemediationFallbackReason
                        .PROVIDER_UNAVAILABLE
                        ? null
                        : "attempted-model";

        return RiskRemediationRecommendation
                .ruleBasedFallback(
                        monitoringRun.getId(),
                        risk.getId(),
                        recommendationContent(title),
                        fallbackReason,
                        attemptedProviderName,
                        attemptedModelName,
                        "risk-remediation-v1",
                        "risk-remediation-fallback-v1",
                        CONTEXT_FINGERPRINT,
                        2,
                        3,
                        generatedAt
                );
    }

    private RiskRemediationRecommendationContent
    recommendationContent(
            String title
    ) {
        return new RiskRemediationRecommendationContent(
                title,
                "Repository integration advisory summary.",
                """
                1. Review the persisted condition.
                2. Apply the controlled remediation.
                """.strip(),
                """
                1. Re-run the monitoring checks.
                2. Confirm the persisted condition is resolved.
                """.strip()
        );
    }
}