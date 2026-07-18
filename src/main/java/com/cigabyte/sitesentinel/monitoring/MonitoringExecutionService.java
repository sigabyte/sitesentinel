package com.cigabyte.sitesentinel.monitoring;

import com.cigabyte.sitesentinel.engine.analysis.EvidenceAnalysisEngine;
import com.cigabyte.sitesentinel.engine.collection.EvidenceCollectionEngine;
import com.cigabyte.sitesentinel.engine.risk.RiskEvaluationEngine;
import com.cigabyte.sitesentinel.engine.trust.TrustEvaluationEngine;
import org.springframework.stereotype.Service;
import com.cigabyte.sitesentinel.recommendation.RiskRemediationRecommendationRunGenerationResult;
import com.cigabyte.sitesentinel.recommendation.RiskRemediationRecommendationRunGenerationService;
import com.cigabyte.sitesentinel.notification.delivery.TelegramDocumentDeliveryResult;
import com.cigabyte.sitesentinel.reporting.dispatch.AutomaticMonitoringRunReportDispatchService;


import java.util.UUID;
import com.cigabyte.sitesentinel.notification.NotificationEventGenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MonitoringExecutionService {

    private static final Logger log = LoggerFactory.getLogger(MonitoringExecutionService.class);

    private final MonitoringRunService monitoringRunService;
    private final EvidenceCollectionEngine evidenceCollectionEngine;
    private final EvidenceAnalysisEngine evidenceAnalysisEngine;
    private final RiskEvaluationEngine riskEvaluationEngine;
    private final TrustEvaluationEngine trustEvaluationEngine;
    private final RiskRemediationRecommendationRunGenerationService
            recommendationRunGenerationService;
    private final NotificationEventGenerationService
            notificationEventGenerationService;
    private final AutomaticMonitoringRunReportDispatchService
            automaticReportDispatchService;



    public MonitoringExecutionService(
            MonitoringRunService monitoringRunService,
            EvidenceCollectionEngine evidenceCollectionEngine,
            EvidenceAnalysisEngine evidenceAnalysisEngine,
            RiskEvaluationEngine riskEvaluationEngine,
            TrustEvaluationEngine trustEvaluationEngine,
            NotificationEventGenerationService notificationEventGenerationService,
            RiskRemediationRecommendationRunGenerationService recommendationRunGenerationService,
            AutomaticMonitoringRunReportDispatchService automaticReportDispatchService
    ) {
        this.monitoringRunService = monitoringRunService;
        this.evidenceCollectionEngine = evidenceCollectionEngine;
        this.evidenceAnalysisEngine = evidenceAnalysisEngine;
        this.riskEvaluationEngine = riskEvaluationEngine;
        this.trustEvaluationEngine = trustEvaluationEngine;
        this.notificationEventGenerationService =
                notificationEventGenerationService;
        this.recommendationRunGenerationService =
                recommendationRunGenerationService;
        this.automaticReportDispatchService =
                automaticReportDispatchService;
    }

    public MonitoringRun execute(UUID websiteId) {
        MonitoringRun monitoringRun = monitoringRunService.createPendingRun(websiteId);

        return executeRun(monitoringRun);
    }

    public MonitoringRun executeScheduled(UUID websiteId, UUID monitoringScheduleId) {
        MonitoringRun monitoringRun = monitoringRunService.createScheduledPendingRun(
                websiteId,
                monitoringScheduleId
        );

        return executeRun(monitoringRun);
    }

    private MonitoringRun executeRun(MonitoringRun monitoringRun) {
        try {
            monitoringRunService.markRunning(monitoringRun.getId());

            evidenceCollectionEngine.collect(monitoringRun.getId());
            evidenceAnalysisEngine.analyze(monitoringRun.getId());
            riskEvaluationEngine.evaluate(monitoringRun.getId());
            trustEvaluationEngine.assess(monitoringRun.getId());

            MonitoringRun completedRun =
                    monitoringRunService.markCompleted(
                            monitoringRun.getId()
                    );

            boolean recommendationGenerationCompleted =
                    generateRecommendationsSafely(
                            completedRun
                    );

            if (recommendationGenerationCompleted) {
                dispatchAutomaticReportSafely(
                        completedRun
                );
            } else {
                log.warn(
                        "Automatic Telegram PDF dispatch was skipped "
                                + "because recommendation generation "
                                + "did not complete for monitoringRunId={}",
                        completedRun.getId()
                );
            }

            generateNotificationsSafely(
                    completedRun
            );

            return completedRun;
        } catch (RuntimeException exception) {
            MonitoringRun failedRun = monitoringRunService.markFailed(
                    monitoringRun.getId(),
                    safeFailureReason(exception)
            );

            generateNotificationsSafely(failedRun);
            return failedRun;
        }
    }

    private String safeFailureReason(RuntimeException exception) {
        String message = exception.getMessage();

        if (message == null || message.isBlank()) {
            return exception.getClass().getSimpleName();
        }

        if (message.length() > 2000) {
            return message.substring(0, 2000);
        }

        return message;
    }

    private boolean generateRecommendationsSafely(
            MonitoringRun monitoringRun
    ) {
        try {
            RiskRemediationRecommendationRunGenerationResult
                    result =
                    recommendationRunGenerationService
                            .generateForCompletedRun(
                                    monitoringRun
                            );

            log.info(
                    "Risk remediation recommendation generation "
                            + "completed for monitoringRunId={}, "
                            + "riskCount={}, generatedCount={}, "
                            + "failedCount={}",
                    result.monitoringRunId(),
                    result.riskCount(),
                    result.generatedCount(),
                    result.failedCount()
            );

            return true;

        } catch (RuntimeException exception) {
            log.warn(
                    "Risk remediation recommendation run generation "
                            + "failed for monitoringRunId={}, "
                            + "failureType={}",
                    monitoringRun.getId(),
                    exception.getClass().getSimpleName()
            );

            return false;
        }
    }

    private void dispatchAutomaticReportSafely(
            MonitoringRun monitoringRun
    ) {
        try {
            TelegramDocumentDeliveryResult result =
                    automaticReportDispatchService
                            .dispatchCompletedRun(
                                    monitoringRun
                            );

            if (result == null) {
                log.warn(
                        "Automatic Telegram PDF dispatch returned "
                                + "no result for monitoringRunId={}",
                        monitoringRun.getId()
                );

                return;
            }

            log.info(
                    "Automatic Telegram PDF dispatch completed "
                            + "for monitoringRunId={}, status={}, "
                            + "deliveryAttempted={}, successful={}",
                    monitoringRun.getId(),
                    result.getStatus(),
                    result.isDeliveryAttempted(),
                    result.isSuccessful()
            );

        } catch (RuntimeException exception) {
            log.warn(
                    "Automatic Telegram PDF dispatch failed "
                            + "for monitoringRunId={}, failureType={}",
                    monitoringRun.getId(),
                    exception.getClass().getSimpleName()
            );
        }
    }

    private void generateNotificationsSafely(MonitoringRun monitoringRun) {
        try {
            notificationEventGenerationService.generateForRun(monitoringRun);
        } catch (RuntimeException exception) {
            log.warn(
                    "Notification event generation failed for monitoringRunId={}",
                    monitoringRun.getId(),
                    exception
            );
        }
    }
}