package com.cigabyte.sitesentinel.monitoring;

import com.cigabyte.sitesentinel.engine.analysis.EvidenceAnalysisEngine;
import com.cigabyte.sitesentinel.engine.collection.EvidenceCollectionEngine;
import com.cigabyte.sitesentinel.engine.risk.RiskEvaluationEngine;
import com.cigabyte.sitesentinel.engine.trust.TrustEvaluationEngine;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MonitoringExecutionService {

    private final MonitoringRunService monitoringRunService;
    private final EvidenceCollectionEngine evidenceCollectionEngine;
    private final EvidenceAnalysisEngine evidenceAnalysisEngine;
    private final RiskEvaluationEngine riskEvaluationEngine;
    private final TrustEvaluationEngine trustEvaluationEngine;

    public MonitoringExecutionService(
            MonitoringRunService monitoringRunService,
            EvidenceCollectionEngine evidenceCollectionEngine,
            EvidenceAnalysisEngine evidenceAnalysisEngine,
            RiskEvaluationEngine riskEvaluationEngine,
            TrustEvaluationEngine trustEvaluationEngine
    ) {
        this.monitoringRunService = monitoringRunService;
        this.evidenceCollectionEngine = evidenceCollectionEngine;
        this.evidenceAnalysisEngine = evidenceAnalysisEngine;
        this.riskEvaluationEngine = riskEvaluationEngine;
        this.trustEvaluationEngine = trustEvaluationEngine;
    }

    public MonitoringRun execute(UUID websiteId) {
        MonitoringRun monitoringRun = monitoringRunService.createPendingRun(websiteId);

        try {
            monitoringRunService.markRunning(monitoringRun.getId());

            evidenceCollectionEngine.collect(monitoringRun.getId());
            evidenceAnalysisEngine.analyze(monitoringRun.getId());
            riskEvaluationEngine.evaluate(monitoringRun.getId());
            trustEvaluationEngine.assess(monitoringRun.getId());

            return monitoringRunService.markCompleted(monitoringRun.getId());
        } catch (RuntimeException exception) {
            return monitoringRunService.markFailed(
                    monitoringRun.getId(),
                    safeFailureReason(exception)
            );
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
}