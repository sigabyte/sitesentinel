package com.cigabyte.sitesentinel.monitoring;

import com.cigabyte.sitesentinel.engine.analysis.EvidenceAnalysisEngine;
import com.cigabyte.sitesentinel.engine.collection.EvidenceCollectionEngine;
import com.cigabyte.sitesentinel.engine.risk.RiskEvaluationEngine;
import com.cigabyte.sitesentinel.engine.trust.TrustEvaluationEngine;
import com.cigabyte.sitesentinel.notification.NotificationEventGenerationService;
import com.cigabyte.sitesentinel.recommendation.RiskRemediationRecommendationRunGenerationService;
import com.cigabyte.sitesentinel.reporting.dispatch.AutomaticMonitoringRunReportDispatchService;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MonitoringExecutionRecommendationSafetyTests {

    private final MonitoringRunService monitoringRunService =
            mock(MonitoringRunService.class);

    private final EvidenceCollectionEngine
            evidenceCollectionEngine =
            mock(EvidenceCollectionEngine.class);

    private final EvidenceAnalysisEngine
            evidenceAnalysisEngine =
            mock(EvidenceAnalysisEngine.class);

    private final RiskEvaluationEngine riskEvaluationEngine =
            mock(RiskEvaluationEngine.class);

    private final TrustEvaluationEngine trustEvaluationEngine =
            mock(TrustEvaluationEngine.class);

    private final RiskRemediationRecommendationRunGenerationService
            recommendationRunGenerationService =
            mock(
                    RiskRemediationRecommendationRunGenerationService.class
            );

    private final NotificationEventGenerationService
            notificationEventGenerationService =
            mock(NotificationEventGenerationService.class);

    private final AutomaticMonitoringRunReportDispatchService
            automaticReportDispatchService =
            mock(
                    AutomaticMonitoringRunReportDispatchService.class
            );

    private final MonitoringExecutionService
            monitoringExecutionService =
            new MonitoringExecutionService(
                    monitoringRunService,
                    evidenceCollectionEngine,
                    evidenceAnalysisEngine,
                    riskEvaluationEngine,
                    trustEvaluationEngine,
                    notificationEventGenerationService,
                    recommendationRunGenerationService,
                    automaticReportDispatchService
            );

    @Test
    void recommendationFailureDoesNotChangeCompletedRunLifecycle() {
        UUID websiteId = UUID.randomUUID();

        MonitoringRun pendingRun =
                persistedPendingRun(websiteId);

        MonitoringRun completedRun =
                persistedCompletedRun(
                        websiteId,
                        pendingRun.getId()
                );

        when(
                monitoringRunService.createPendingRun(
                        websiteId
                )
        ).thenReturn(pendingRun);

        when(
                monitoringRunService.markCompleted(
                        pendingRun.getId()
                )
        ).thenReturn(completedRun);

        doThrow(
                new IllegalStateException(
                        "recommendation-subsystem-failure"
                )
        ).when(
                recommendationRunGenerationService
        ).generateForCompletedRun(completedRun);

        MonitoringRun result =
                monitoringExecutionService.execute(
                        websiteId
                );

        assertSame(completedRun, result);

        verify(
                monitoringRunService,
                never()
        ).markFailed(
                org.mockito.ArgumentMatchers.any(),
                anyString()
        );

        InOrder completionOrder =
                inOrder(
                        recommendationRunGenerationService,
                        notificationEventGenerationService
                );

        completionOrder.verify(
                recommendationRunGenerationService
        ).generateForCompletedRun(completedRun);

        completionOrder.verify(
                notificationEventGenerationService
        ).generateForRun(completedRun);

        verify(
                automaticReportDispatchService,
                never()
        ).dispatchCompletedRun(
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void failedMonitoringPipelineDoesNotGenerateRecommendations() {
        UUID websiteId = UUID.randomUUID();

        MonitoringRun pendingRun =
                persistedPendingRun(websiteId);

        MonitoringRun failedRun =
                persistedFailedRun(
                        websiteId,
                        pendingRun.getId()
                );

        when(
                monitoringRunService.createPendingRun(
                        websiteId
                )
        ).thenReturn(pendingRun);

        doThrow(
                new IllegalStateException(
                        "evidence-collection-failure"
                )
        ).when(
                evidenceCollectionEngine
        ).collect(pendingRun.getId());

        when(
                monitoringRunService.markFailed(
                        pendingRun.getId(),
                        "evidence-collection-failure"
                )
        ).thenReturn(failedRun);

        MonitoringRun result =
                monitoringExecutionService.execute(
                        websiteId
                );

        assertSame(failedRun, result);

        verify(
                recommendationRunGenerationService,
                never()
        ).generateForCompletedRun(
                org.mockito.ArgumentMatchers.any()
        );

        verify(
                notificationEventGenerationService
        ).generateForRun(failedRun);

        verify(
                evidenceAnalysisEngine,
                never()
        ).analyze(
                org.mockito.ArgumentMatchers.any()
        );

        verify(
                riskEvaluationEngine,
                never()
        ).evaluate(
                org.mockito.ArgumentMatchers.any()
        );

        verify(
                trustEvaluationEngine,
                never()
        ).assess(
                org.mockito.ArgumentMatchers.any()
        );

        verify(
                monitoringRunService,
                never()
        ).markCompleted(
                org.mockito.ArgumentMatchers.any()
        );

        verify(
                automaticReportDispatchService,
                never()
        ).dispatchCompletedRun(
                org.mockito.ArgumentMatchers.any()
        );
    }

    private MonitoringRun persistedPendingRun(
            UUID websiteId
    ) {
        MonitoringRun monitoringRun =
                new MonitoringRun(websiteId);

        ReflectionTestUtils.setField(
                monitoringRun,
                "id",
                UUID.randomUUID()
        );

        return monitoringRun;
    }

    private MonitoringRun persistedCompletedRun(
            UUID websiteId,
            UUID monitoringRunId
    ) {
        MonitoringRun monitoringRun =
                new MonitoringRun(websiteId);

        ReflectionTestUtils.setField(
                monitoringRun,
                "id",
                monitoringRunId
        );

        monitoringRun.markRunning();
        monitoringRun.markCompleted();

        return monitoringRun;
    }

    private MonitoringRun persistedFailedRun(
            UUID websiteId,
            UUID monitoringRunId
    ) {
        MonitoringRun monitoringRun =
                new MonitoringRun(websiteId);

        ReflectionTestUtils.setField(
                monitoringRun,
                "id",
                monitoringRunId
        );

        monitoringRun.markRunning();

        monitoringRun.markFailed(
                "evidence-collection-failure"
        );

        return monitoringRun;
    }
}