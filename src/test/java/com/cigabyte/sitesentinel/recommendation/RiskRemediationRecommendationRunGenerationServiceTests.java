package com.cigabyte.sitesentinel.recommendation;

import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
import com.cigabyte.sitesentinel.reporting.SiteSentinelReportLanguage;
import com.cigabyte.sitesentinel.risk.Risk;
import com.cigabyte.sitesentinel.risk.RiskRepository;
import com.cigabyte.sitesentinel.risk.RiskSeverity;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RiskRemediationRecommendationRunGenerationServiceTests {

    private final RiskRepository riskRepository =
            mock(RiskRepository.class);

    private final RiskRemediationRecommendationGenerationService
            generationService =
            mock(
                    RiskRemediationRecommendationGenerationService.class
            );

    private final RiskRemediationRecommendationRepository
            recommendationRepository =
            mock(
                    RiskRemediationRecommendationRepository.class
            );

    private final RiskRemediationRecommendationRunGenerationService
            runGenerationService =
            new RiskRemediationRecommendationRunGenerationService(
                    riskRepository,
                    recommendationRepository,
                    generationService
            );

    @Test
    void skipsGenerationWhenRecommendationAlreadyExistsForRunAndRisk() {
        MonitoringRun completedRun =
                completedRun();

        Risk persistedRisk =
                risk(
                        completedRun,
                        "TLS_CONFIGURATION"
                );

        when(
                riskRepository
                        .findByMonitoringRunIdOrderByRiskScoreDescCreatedAtAsc(
                                completedRun.getId()
                        )
        ).thenReturn(
                List.of(persistedRisk)
        );

        when(
                recommendationRepository
                        .existsByRiskIdAndMonitoringRunIdAndReportLanguage(
                                persistedRisk.getId(),
                                completedRun.getId(),
                                SiteSentinelReportLanguage.ENGLISH
                        )
        ).thenReturn(true);

        RiskRemediationRecommendationRunGenerationResult result =
                runGenerationService
                        .generateForCompletedRun(
                                completedRun
                        );

        assertEquals(1, result.riskCount());
        assertEquals(0, result.generatedCount());
        assertEquals(1, result.skippedCount());
        assertEquals(0, result.failedCount());

        assertTrue(result.isFullySuccessful());
        assertFalse(result.isEmpty());

        verify(
                recommendationRepository
        ).existsByRiskIdAndMonitoringRunIdAndReportLanguage(
                persistedRisk.getId(),
                completedRun.getId(),
                SiteSentinelReportLanguage.ENGLISH
        );

        verify(
                generationService,
                never()
        ).generateAndPersist(
                completedRun.getId(),
                persistedRisk.getId(),
                SiteSentinelReportLanguage.ENGLISH
        );
    }

    @Test
    void isolatesPerRiskFailureAndContinuesRemainingRisks() {
        MonitoringRun completedRun =
                completedRun();

        Risk firstRisk =
                risk(
                        completedRun,
                        "TLS_CONFIGURATION"
                );

        Risk secondRisk =
                risk(
                        completedRun,
                        "SECURITY_HEADERS"
                );

        Risk thirdRisk =
                risk(
                        completedRun,
                        "CONTENT_INTEGRITY"
                );

        when(
                riskRepository
                        .findByMonitoringRunIdOrderByRiskScoreDescCreatedAtAsc(
                                completedRun.getId()
                        )
        ).thenReturn(
                List.of(
                        firstRisk,
                        secondRisk,
                        thirdRisk
                )
        );

        when(
                generationService.generateAndPersist(
                        completedRun.getId(),
                        firstRisk.getId(),
                        SiteSentinelReportLanguage.ENGLISH
                )
        ).thenReturn(
                mock(
                        RiskRemediationRecommendation.class
                )
        );

        when(
                generationService.generateAndPersist(
                        completedRun.getId(),
                        secondRisk.getId(),
                        SiteSentinelReportLanguage.ENGLISH
                )
        ).thenThrow(
                new IllegalStateException(
                        "isolated-generation-failure"
                )
        );

        when(
                generationService.generateAndPersist(
                        completedRun.getId(),
                        thirdRisk.getId(),
                        SiteSentinelReportLanguage.ENGLISH
                )
        ).thenReturn(
                mock(
                        RiskRemediationRecommendation.class
                )
        );

        RiskRemediationRecommendationRunGenerationResult result =
                runGenerationService
                        .generateForCompletedRun(
                                completedRun
                        );

        assertEquals(
                completedRun.getId(),
                result.monitoringRunId()
        );

        assertEquals(3, result.riskCount());
        assertEquals(2, result.generatedCount());
        assertEquals(1, result.failedCount());

        assertFalse(result.isFullySuccessful());
        assertFalse(result.isEmpty());

        verify(
                generationService
        ).generateAndPersist(
                completedRun.getId(),
                firstRisk.getId(),
                SiteSentinelReportLanguage.ENGLISH
        );

        verify(
                generationService
        ).generateAndPersist(
                completedRun.getId(),
                secondRisk.getId(),
                SiteSentinelReportLanguage.ENGLISH
        );

        verify(
                generationService
        ).generateAndPersist(
                completedRun.getId(),
                thirdRisk.getId(),
                SiteSentinelReportLanguage.ENGLISH
        );
    }

    @Test
    void completedRunWithNoRisksReturnsEmptySuccessfulResult() {
        MonitoringRun completedRun =
                completedRun();

        when(
                riskRepository
                        .findByMonitoringRunIdOrderByRiskScoreDescCreatedAtAsc(
                                completedRun.getId()
                        )
        ).thenReturn(List.of());

        RiskRemediationRecommendationRunGenerationResult result =
                runGenerationService
                        .generateForCompletedRun(
                                completedRun
                        );

        assertEquals(0, result.riskCount());
        assertEquals(0, result.generatedCount());
        assertEquals(0, result.failedCount());

        assertTrue(result.isFullySuccessful());
        assertTrue(result.isEmpty());

        verify(
                generationService,
                never()
        ).generateAndPersist(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void rejectsRunThatIsNotCompleted() {
        MonitoringRun pendingRun =
                persistedPendingRun();

        assertThrows(
                IllegalArgumentException.class,
                () -> runGenerationService
                        .generateForCompletedRun(
                                pendingRun
                        )
        );

        verify(
                riskRepository,
                never()
        ).findByMonitoringRunIdOrderByRiskScoreDescCreatedAtAsc(
                pendingRun.getId()
        );

        verify(
                generationService,
                never()
        ).generateAndPersist(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void reportsGeneratedSkippedAndFailedRisksSeparately() {
        MonitoringRun completedRun =
                completedRun();

        Risk existingRecommendationRisk =
                risk(
                        completedRun,
                        "TLS_CONFIGURATION"
                );

        Risk newlyGeneratedRisk =
                risk(
                        completedRun,
                        "SECURITY_HEADERS"
                );

        Risk failedGenerationRisk =
                risk(
                        completedRun,
                        "CONTENT_INTEGRITY"
                );

        when(
                riskRepository
                        .findByMonitoringRunIdOrderByRiskScoreDescCreatedAtAsc(
                                completedRun.getId()
                        )
        ).thenReturn(
                List.of(
                        existingRecommendationRisk,
                        newlyGeneratedRisk,
                        failedGenerationRisk
                )
        );

        when(
                recommendationRepository
                        .existsByRiskIdAndMonitoringRunIdAndReportLanguage(
                                existingRecommendationRisk.getId(),
                                completedRun.getId(),
                                SiteSentinelReportLanguage.ENGLISH
                        )
        ).thenReturn(true);

        when(
                recommendationRepository
                        .existsByRiskIdAndMonitoringRunIdAndReportLanguage(
                                newlyGeneratedRisk.getId(),
                                completedRun.getId(),
                                SiteSentinelReportLanguage.ENGLISH
                        )
        ).thenReturn(false);

        when(
                recommendationRepository
                        .existsByRiskIdAndMonitoringRunIdAndReportLanguage(
                                failedGenerationRisk.getId(),
                                completedRun.getId(),
                                SiteSentinelReportLanguage.ENGLISH
                        )
        ).thenReturn(false);

        when(
                generationService.generateAndPersist(
                        completedRun.getId(),
                        newlyGeneratedRisk.getId(),
                        SiteSentinelReportLanguage.ENGLISH
                )
        ).thenReturn(
                mock(
                        RiskRemediationRecommendation.class
                )
        );

        when(
                generationService.generateAndPersist(
                        completedRun.getId(),
                        failedGenerationRisk.getId(),
                        SiteSentinelReportLanguage.ENGLISH
                )
        ).thenThrow(
                new IllegalStateException(
                        "isolated-generation-failure"
                )
        );

        RiskRemediationRecommendationRunGenerationResult result =
                runGenerationService
                        .generateForCompletedRun(
                                completedRun
                        );

        assertEquals(
                completedRun.getId(),
                result.monitoringRunId()
        );

        assertEquals(3, result.riskCount());
        assertEquals(1, result.generatedCount());
        assertEquals(1, result.skippedCount());
        assertEquals(1, result.failedCount());

        assertFalse(result.isFullySuccessful());
        assertFalse(result.isEmpty());

        verify(
                generationService,
                never()
        ).generateAndPersist(
                completedRun.getId(),
                existingRecommendationRisk.getId(),
                SiteSentinelReportLanguage.ENGLISH
        );

        verify(
                generationService
        ).generateAndPersist(
                completedRun.getId(),
                newlyGeneratedRisk.getId(),
                SiteSentinelReportLanguage.ENGLISH
        );

        verify(
                generationService
        ).generateAndPersist(
                completedRun.getId(),
                failedGenerationRisk.getId(),
                SiteSentinelReportLanguage.ENGLISH
        );
    }

    @Test
    void repeatedRunGenerationDoesNotGenerateRecommendationAgainAfterPersistence() {
        MonitoringRun completedRun =
                completedRun();

        Risk persistedRisk =
                risk(
                        completedRun,
                        "TLS_CONFIGURATION"
                );

        when(
                riskRepository
                        .findByMonitoringRunIdOrderByRiskScoreDescCreatedAtAsc(
                                completedRun.getId()
                        )
        ).thenReturn(
                List.of(persistedRisk)
        );

        when(
                recommendationRepository
                        .existsByRiskIdAndMonitoringRunIdAndReportLanguage(
                                persistedRisk.getId(),
                                completedRun.getId(),
                                SiteSentinelReportLanguage.ENGLISH
                        )
        ).thenReturn(
                false,
                true
        );

        when(
                generationService.generateAndPersist(
                        completedRun.getId(),
                        persistedRisk.getId(),
                        SiteSentinelReportLanguage.ENGLISH
                )
        ).thenReturn(
                mock(
                        RiskRemediationRecommendation.class
                )
        );

        RiskRemediationRecommendationRunGenerationResult firstResult =
                runGenerationService
                        .generateForCompletedRun(
                                completedRun
                        );

        RiskRemediationRecommendationRunGenerationResult secondResult =
                runGenerationService
                        .generateForCompletedRun(
                                completedRun
                        );

        assertEquals(1, firstResult.riskCount());
        assertEquals(1, firstResult.generatedCount());
        assertEquals(0, firstResult.skippedCount());
        assertEquals(0, firstResult.failedCount());

        assertEquals(1, secondResult.riskCount());
        assertEquals(0, secondResult.generatedCount());
        assertEquals(1, secondResult.skippedCount());
        assertEquals(0, secondResult.failedCount());

        assertTrue(firstResult.isFullySuccessful());
        assertTrue(secondResult.isFullySuccessful());

        verify(
                recommendationRepository,
                org.mockito.Mockito.times(2)
        ).existsByRiskIdAndMonitoringRunIdAndReportLanguage(
                persistedRisk.getId(),
                completedRun.getId(),
                SiteSentinelReportLanguage.ENGLISH
        );

        verify(
                generationService,
                org.mockito.Mockito.times(1)
        ).generateAndPersist(
                completedRun.getId(),
                persistedRisk.getId(),
                SiteSentinelReportLanguage.ENGLISH
        );
    }

    @Test
    void generatesTurkishRecommendationIndependentlyWhenEnglishAlreadyExists() {
        MonitoringRun completedRun =
                completedRun();

        Risk persistedRisk =
                risk(
                        completedRun,
                        "TLS_CONFIGURATION"
                );

        when(
                riskRepository
                        .findByMonitoringRunIdOrderByRiskScoreDescCreatedAtAsc(
                                completedRun.getId()
                        )
        ).thenReturn(
                List.of(persistedRisk)
        );

        when(
                recommendationRepository
                        .existsByRiskIdAndMonitoringRunIdAndReportLanguage(
                                persistedRisk.getId(),
                                completedRun.getId(),
                                SiteSentinelReportLanguage.TURKISH
                        )
        ).thenReturn(false);

        when(
                generationService.generateAndPersist(
                        completedRun.getId(),
                        persistedRisk.getId(),
                        SiteSentinelReportLanguage.TURKISH
                )
        ).thenReturn(
                mock(
                        RiskRemediationRecommendation.class
                )
        );

        RiskRemediationRecommendationRunGenerationResult result =
                runGenerationService
                        .generateForCompletedRun(
                                completedRun,
                                SiteSentinelReportLanguage.TURKISH
                        );

        assertEquals(1, result.riskCount());
        assertEquals(1, result.generatedCount());
        assertEquals(0, result.skippedCount());
        assertEquals(0, result.failedCount());
        assertTrue(result.isFullySuccessful());

        verify(
                recommendationRepository
        ).existsByRiskIdAndMonitoringRunIdAndReportLanguage(
                persistedRisk.getId(),
                completedRun.getId(),
                SiteSentinelReportLanguage.TURKISH
        );

        verify(
                generationService
        ).generateAndPersist(
                completedRun.getId(),
                persistedRisk.getId(),
                SiteSentinelReportLanguage.TURKISH
        );
    }

    private MonitoringRun completedRun() {
        MonitoringRun monitoringRun =
                persistedPendingRun();

        monitoringRun.markRunning();
        monitoringRun.markCompleted();

        return monitoringRun;
    }

    private MonitoringRun persistedPendingRun() {
        MonitoringRun monitoringRun =
                new MonitoringRun(
                        UUID.randomUUID()
                );

        ReflectionTestUtils.setField(
                monitoringRun,
                "id",
                UUID.randomUUID()
        );

        return monitoringRun;
    }

    private Risk risk(
            MonitoringRun monitoringRun,
            String riskType
    ) {
        Risk risk =
                new Risk(
                        monitoringRun.getWebsiteId(),
                        monitoringRun.getId(),
                        riskType,
                        RiskSeverity.HIGH,
                        80,
                        90,
                        "Persisted risk rationale."
                );

        ReflectionTestUtils.setField(
                risk,
                "id",
                UUID.randomUUID()
        );

        return risk;
    }
}