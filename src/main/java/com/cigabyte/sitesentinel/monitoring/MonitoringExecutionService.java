package com.cigabyte.sitesentinel.monitoring;

import com.cigabyte.sitesentinel.engine.analysis.EvidenceAnalysisEngine;
import com.cigabyte.sitesentinel.engine.collection.EvidenceCollectionEngine;
import com.cigabyte.sitesentinel.engine.risk.RiskEvaluationEngine;
import com.cigabyte.sitesentinel.engine.trust.TrustEvaluationEngine;
import org.springframework.stereotype.Service;

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
    private final NotificationEventGenerationService notificationEventGenerationService;


    public MonitoringExecutionService(
            MonitoringRunService monitoringRunService,
            EvidenceCollectionEngine evidenceCollectionEngine,
            EvidenceAnalysisEngine evidenceAnalysisEngine,
            RiskEvaluationEngine riskEvaluationEngine,
            TrustEvaluationEngine trustEvaluationEngine,
            NotificationEventGenerationService notificationEventGenerationService
    ) {
        this.monitoringRunService = monitoringRunService;
        this.evidenceCollectionEngine = evidenceCollectionEngine;
        this.evidenceAnalysisEngine = evidenceAnalysisEngine;
        this.riskEvaluationEngine = riskEvaluationEngine;
        this.trustEvaluationEngine = trustEvaluationEngine;
        this.notificationEventGenerationService = notificationEventGenerationService;
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

            MonitoringRun completedRun = monitoringRunService.markCompleted(monitoringRun.getId());
            generateNotificationsSafely(completedRun);
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