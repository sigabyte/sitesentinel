package com.cigabyte.sitesentinel.monitoring;

import com.cigabyte.sitesentinel.engine.analysis.EvidenceAnalysisEngine;
import com.cigabyte.sitesentinel.engine.collection.EvidenceCollectionEngine;
import com.cigabyte.sitesentinel.engine.risk.RiskEvaluationEngine;
import com.cigabyte.sitesentinel.engine.trust.TrustEvaluationEngine;
import com.cigabyte.sitesentinel.notification.NotificationEventGenerationService;
import com.cigabyte.sitesentinel.notification.delivery.TelegramDocumentDeliveryResult;
import com.cigabyte.sitesentinel.recommendation.RiskRemediationRecommendationRunGenerationResult;
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

class MonitoringExecutionReportDispatchSafetyTests {

    private final MonitoringRunService monitoringRunService =
            mock(
                    MonitoringRunService.class
            );

    private final EvidenceCollectionEngine
            evidenceCollectionEngine =
            mock(
                    EvidenceCollectionEngine.class
            );

    private final EvidenceAnalysisEngine
            evidenceAnalysisEngine =
            mock(
                    EvidenceAnalysisEngine.class
            );

    private final RiskEvaluationEngine
            riskEvaluationEngine =
            mock(
                    RiskEvaluationEngine.class
            );

    private final TrustEvaluationEngine
            trustEvaluationEngine =
            mock(
                    TrustEvaluationEngine.class
            );

    private final NotificationEventGenerationService
            notificationEventGenerationService =
            mock(
                    NotificationEventGenerationService.class
            );

    private final RiskRemediationRecommendationRunGenerationService
            recommendationRunGenerationService =
            mock(
                    RiskRemediationRecommendationRunGenerationService.class
            );

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
    void completedRunDispatchesReportAfterRecommendationsAndBeforeNotifications() {
        UUID websiteId =
                UUID.randomUUID();

        MonitoringRun pendingRun =
                persistedPendingRun(
                        websiteId
                );

        MonitoringRun completedRun =
                persistedCompletedRun(
                        websiteId,
                        pendingRun.getId()
                );

        when(
                monitoringRunService.createPendingRun(
                        websiteId
                )
        ).thenReturn(
                pendingRun
        );

        when(
                monitoringRunService.markCompleted(
                        pendingRun.getId()
                )
        ).thenReturn(
                completedRun
        );

        when(
                recommendationRunGenerationService
                        .generateForCompletedRun(
                                completedRun
                        )
        ).thenReturn(
                successfulRecommendationResult(
                        completedRun.getId()
                )
        );

        TelegramDocumentDeliveryResult deliveryResult =
                TelegramDocumentDeliveryResult.success(
                        2468L,
                        "Telegram document was sent successfully.",
                        "Telegram Bot API accepted the document."
                );

        when(
                automaticReportDispatchService
                        .dispatchCompletedRun(
                                completedRun
                        )
        ).thenReturn(
                deliveryResult
        );

        MonitoringRun result =
                monitoringExecutionService.execute(
                        websiteId
                );

        assertSame(
                completedRun,
                result
        );

        InOrder completionOrder =
                inOrder(
                        recommendationRunGenerationService,
                        automaticReportDispatchService,
                        notificationEventGenerationService
                );

        completionOrder.verify(
                recommendationRunGenerationService
        ).generateForCompletedRun(
                completedRun
        );

        completionOrder.verify(
                automaticReportDispatchService
        ).dispatchCompletedRun(
                completedRun
        );

        completionOrder.verify(
                notificationEventGenerationService
        ).generateForRun(
                completedRun
        );

        verify(
                monitoringRunService,
                never()
        ).markFailed(
                org.mockito.ArgumentMatchers.any(),
                anyString()
        );
    }

    @Test
    void failedDeliveryResultDoesNotChangeCompletedRunLifecycle() {
        UUID websiteId =
                UUID.randomUUID();

        MonitoringRun pendingRun =
                persistedPendingRun(
                        websiteId
                );

        MonitoringRun completedRun =
                persistedCompletedRun(
                        websiteId,
                        pendingRun.getId()
                );

        prepareCompletedExecution(
                websiteId,
                pendingRun,
                completedRun
        );

        when(
                automaticReportDispatchService
                        .dispatchCompletedRun(
                                completedRun
                        )
        ).thenReturn(
                TelegramDocumentDeliveryResult.failure(
                        "Telegram document delivery failed.",
                        "Telegram Bot API returned HTTP status=429."
                )
        );

        MonitoringRun result =
                monitoringExecutionService.execute(
                        websiteId
                );

        assertSame(
                completedRun,
                result
        );

        verify(
                automaticReportDispatchService
        ).dispatchCompletedRun(
                completedRun
        );

        verify(
                notificationEventGenerationService
        ).generateForRun(
                completedRun
        );

        verify(
                monitoringRunService,
                never()
        ).markFailed(
                org.mockito.ArgumentMatchers.any(),
                anyString()
        );
    }

    @Test
    void dispatchExceptionDoesNotChangeCompletedRunLifecycle() {
        UUID websiteId =
                UUID.randomUUID();

        MonitoringRun pendingRun =
                persistedPendingRun(
                        websiteId
                );

        MonitoringRun completedRun =
                persistedCompletedRun(
                        websiteId,
                        pendingRun.getId()
                );

        prepareCompletedExecution(
                websiteId,
                pendingRun,
                completedRun
        );

        doThrow(
                new IllegalStateException(
                        "controlled-dispatch-subsystem-failure"
                )
        ).when(
                automaticReportDispatchService
        ).dispatchCompletedRun(
                completedRun
        );

        MonitoringRun result =
                monitoringExecutionService.execute(
                        websiteId
                );

        assertSame(
                completedRun,
                result
        );

        verify(
                automaticReportDispatchService
        ).dispatchCompletedRun(
                completedRun
        );

        verify(
                notificationEventGenerationService
        ).generateForRun(
                completedRun
        );

        verify(
                monitoringRunService,
                never()
        ).markFailed(
                org.mockito.ArgumentMatchers.any(),
                anyString()
        );
    }

    private void prepareCompletedExecution(
            UUID websiteId,
            MonitoringRun pendingRun,
            MonitoringRun completedRun
    ) {
        when(
                monitoringRunService.createPendingRun(
                        websiteId
                )
        ).thenReturn(
                pendingRun
        );

        when(
                monitoringRunService.markCompleted(
                        pendingRun.getId()
                )
        ).thenReturn(
                completedRun
        );

        when(
                recommendationRunGenerationService
                        .generateForCompletedRun(
                                completedRun
                        )
        ).thenReturn(
                successfulRecommendationResult(
                        completedRun.getId()
                )
        );
    }

    private RiskRemediationRecommendationRunGenerationResult
    successfulRecommendationResult(
            UUID monitoringRunId
    ) {
        return new RiskRemediationRecommendationRunGenerationResult(
                monitoringRunId,
                0,
                0,
                0
        );
    }

    private MonitoringRun persistedPendingRun(
            UUID websiteId
    ) {
        MonitoringRun monitoringRun =
                new MonitoringRun(
                        websiteId
                );

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
                new MonitoringRun(
                        websiteId
                );

        ReflectionTestUtils.setField(
                monitoringRun,
                "id",
                monitoringRunId
        );

        monitoringRun.markRunning();
        monitoringRun.markCompleted();

        return monitoringRun;
    }
}