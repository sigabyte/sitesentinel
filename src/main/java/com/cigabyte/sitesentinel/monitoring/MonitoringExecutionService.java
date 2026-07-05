package com.cigabyte.sitesentinel.monitoring;

import com.cigabyte.sitesentinel.engine.analysis.EvidenceAnalysisEngine;
import com.cigabyte.sitesentinel.engine.collection.EvidenceCollectionEngine;
import com.cigabyte.sitesentinel.engine.risk.RiskEvaluationEngine;
import com.cigabyte.sitesentinel.engine.trust.TrustEvaluationEngine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class MonitoringExecutionService {

    private final MonitoringRunRepository monitoringRunRepository;
    private final MonitoringRunService monitoringRunService;
    private final EvidenceCollectionEngine evidenceCollectionEngine;
    private final EvidenceAnalysisEngine evidenceAnalysisEngine;
    private final RiskEvaluationEngine riskEvaluationEngine;
    private final TrustEvaluationEngine trustEvaluationEngine;

    public MonitoringExecutionService(
            MonitoringRunRepository monitoringRunRepository,
            MonitoringRunService monitoringRunService,
            EvidenceCollectionEngine evidenceCollectionEngine,
            EvidenceAnalysisEngine evidenceAnalysisEngine,
            RiskEvaluationEngine riskEvaluationEngine,
            TrustEvaluationEngine trustEvaluationEngine
    ) {
        this.monitoringRunRepository = monitoringRunRepository;
        this.monitoringRunService = monitoringRunService;
        this.evidenceCollectionEngine = evidenceCollectionEngine;
        this.evidenceAnalysisEngine = evidenceAnalysisEngine;
        this.riskEvaluationEngine = riskEvaluationEngine;
        this.trustEvaluationEngine = trustEvaluationEngine;
    }

    @Transactional
    public MonitoringRun execute(UUID websiteId) {
        MonitoringRun monitoringRun = monitoringRunService.createPendingRun(websiteId);

        try {
            monitoringRun.markRunning();

            evidenceCollectionEngine.collect(monitoringRun.getId());
            evidenceAnalysisEngine.analyze(monitoringRun.getId());
            riskEvaluationEngine.evaluate(monitoringRun.getId());
            trustEvaluationEngine.assess(monitoringRun.getId());

            monitoringRun.markCompleted();
        } catch (RuntimeException exception) {
            monitoringRun.markFailed(exception.getMessage());
        }

        return monitoringRunRepository.save(monitoringRun);
    }
}