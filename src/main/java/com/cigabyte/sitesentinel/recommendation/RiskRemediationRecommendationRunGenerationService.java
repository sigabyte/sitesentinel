package com.cigabyte.sitesentinel.recommendation;

import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
import com.cigabyte.sitesentinel.monitoring.MonitoringRunStatus;
import com.cigabyte.sitesentinel.risk.Risk;
import com.cigabyte.sitesentinel.risk.RiskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class RiskRemediationRecommendationRunGenerationService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    RiskRemediationRecommendationRunGenerationService.class
            );

    private final RiskRepository riskRepository;

    private final RiskRemediationRecommendationGenerationService
            recommendationGenerationService;

    public RiskRemediationRecommendationRunGenerationService(
            RiskRepository riskRepository,
            RiskRemediationRecommendationGenerationService
                    recommendationGenerationService
    ) {
        this.riskRepository = riskRepository;
        this.recommendationGenerationService =
                recommendationGenerationService;
    }

    public RiskRemediationRecommendationRunGenerationResult
    generateForCompletedRun(
            MonitoringRun monitoringRun
    ) {
        MonitoringRun requiredMonitoringRun =
                Objects.requireNonNull(
                        monitoringRun,
                        "Monitoring run is required."
                );

        if (requiredMonitoringRun.getId() == null) {
            throw new IllegalArgumentException(
                    "Persisted monitoring run ID is required."
            );
        }

        if (requiredMonitoringRun.getStatus()
                != MonitoringRunStatus.COMPLETED) {

            throw new IllegalArgumentException(
                    "Risk remediation recommendations may only "
                            + "be generated for a completed monitoring run."
            );
        }

        List<Risk> risks =
                riskRepository
                        .findByMonitoringRunIdOrderByRiskScoreDescCreatedAtAsc(
                                requiredMonitoringRun.getId()
                        );

        int generatedCount = 0;
        int failedCount = 0;

        for (Risk risk : risks) {
            try {
                recommendationGenerationService
                        .generateAndPersist(
                                requiredMonitoringRun.getId(),
                                risk.getId()
                        );

                generatedCount++;
            } catch (RuntimeException exception) {
                failedCount++;

                log.warn(
                        "Risk remediation recommendation generation "
                                + "failed for monitoringRunId={}, "
                                + "riskId={}, failureType={}",
                        requiredMonitoringRun.getId(),
                        risk.getId(),
                        exception.getClass().getSimpleName()
                );
            }
        }

        return new RiskRemediationRecommendationRunGenerationResult(
                requiredMonitoringRun.getId(),
                risks.size(),
                generatedCount,
                failedCount
        );
    }
}