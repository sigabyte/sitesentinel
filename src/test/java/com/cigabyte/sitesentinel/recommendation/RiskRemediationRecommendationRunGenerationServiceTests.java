package com.cigabyte.sitesentinel.recommendation;

import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
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

    private final RiskRemediationRecommendationRunGenerationService
            runGenerationService =
            new RiskRemediationRecommendationRunGenerationService(
                    riskRepository,
                    generationService
            );

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
                        firstRisk.getId()
                )
        ).thenReturn(
                mock(
                        RiskRemediationRecommendation.class
                )
        );

        when(
                generationService.generateAndPersist(
                        completedRun.getId(),
                        secondRisk.getId()
                )
        ).thenThrow(
                new IllegalStateException(
                        "isolated-generation-failure"
                )
        );

        when(
                generationService.generateAndPersist(
                        completedRun.getId(),
                        thirdRisk.getId()
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
                firstRisk.getId()
        );

        verify(
                generationService
        ).generateAndPersist(
                completedRun.getId(),
                secondRisk.getId()
        );

        verify(
                generationService
        ).generateAndPersist(
                completedRun.getId(),
                thirdRisk.getId()
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
                org.mockito.ArgumentMatchers.any()
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