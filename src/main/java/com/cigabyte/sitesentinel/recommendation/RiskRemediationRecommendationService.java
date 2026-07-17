package com.cigabyte.sitesentinel.recommendation;

import com.cigabyte.sitesentinel.monitoring.MonitoringRunRepository;
import com.cigabyte.sitesentinel.risk.Risk;
import com.cigabyte.sitesentinel.risk.RiskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class RiskRemediationRecommendationService {

    private final RiskRemediationRecommendationRepository
            recommendationRepository;

    private final MonitoringRunRepository monitoringRunRepository;
    private final RiskRepository riskRepository;

    public RiskRemediationRecommendationService(
            RiskRemediationRecommendationRepository
                    recommendationRepository,
            MonitoringRunRepository monitoringRunRepository,
            RiskRepository riskRepository
    ) {
        this.recommendationRepository =
                recommendationRepository;

        this.monitoringRunRepository =
                monitoringRunRepository;

        this.riskRepository =
                riskRepository;
    }

    @Transactional
    public RiskRemediationRecommendation saveValidated(
            RiskRemediationRecommendation recommendation
    ) {
        RiskRemediationRecommendation requiredRecommendation =
                Objects.requireNonNull(
                        recommendation,
                        "Risk remediation recommendation is required."
                );

        validatePersistenceBoundary(requiredRecommendation);

        return recommendationRepository.save(
                requiredRecommendation
        );
    }

    @Transactional(readOnly = true)
    public List<RiskRemediationRecommendation>
    findByMonitoringRunId(UUID monitoringRunId) {
        UUID requiredMonitoringRunId =
                requireId(
                        monitoringRunId,
                        "Monitoring run ID"
                );

        return recommendationRepository
                .findByMonitoringRunIdOrderByGeneratedAtAscCreatedAtAsc(
                        requiredMonitoringRunId
                );
    }

    @Transactional(readOnly = true)
    public List<RiskRemediationRecommendation>
    findByRiskIdAndMonitoringRunId(
            UUID riskId,
            UUID monitoringRunId
    ) {
        UUID requiredRiskId =
                requireId(
                        riskId,
                        "Risk ID"
                );

        UUID requiredMonitoringRunId =
                requireId(
                        monitoringRunId,
                        "Monitoring run ID"
                );

        return recommendationRepository
                .findByRiskIdAndMonitoringRunIdOrderByGeneratedAtDescCreatedAtDesc(
                        requiredRiskId,
                        requiredMonitoringRunId
                );
    }

    @Transactional(readOnly = true)
    public Optional<RiskRemediationRecommendation>
    findLatestByRiskIdAndMonitoringRunId(
            UUID riskId,
            UUID monitoringRunId
    ) {
        UUID requiredRiskId =
                requireId(
                        riskId,
                        "Risk ID"
                );

        UUID requiredMonitoringRunId =
                requireId(
                        monitoringRunId,
                        "Monitoring run ID"
                );

        return recommendationRepository
                .findFirstByRiskIdAndMonitoringRunIdOrderByGeneratedAtDescCreatedAtDesc(
                        requiredRiskId,
                        requiredMonitoringRunId
                );
    }

    @Transactional(readOnly = true)
    public long countByMonitoringRunId(
            UUID monitoringRunId
    ) {
        UUID requiredMonitoringRunId =
                requireId(
                        monitoringRunId,
                        "Monitoring run ID"
                );

        return recommendationRepository.countByMonitoringRunId(
                requiredMonitoringRunId
        );
    }

    private void validatePersistenceBoundary(
            RiskRemediationRecommendation recommendation
    ) {
        UUID monitoringRunId =
                requireId(
                        recommendation.getMonitoringRunId(),
                        "Monitoring run ID"
                );

        UUID riskId =
                requireId(
                        recommendation.getRiskId(),
                        "Risk ID"
                );

        if (recommendation.getValidationStatus()
                != RiskRemediationRecommendationValidationStatus.VALID) {

            throw new IllegalArgumentException(
                    "Only validated risk remediation recommendations "
                            + "may be persisted."
            );
        }

        if (!recommendation.isAdvisory()) {
            throw new IllegalArgumentException(
                    "Risk remediation recommendations must remain advisory."
            );
        }

        if (!monitoringRunRepository.existsById(
                monitoringRunId
        )) {
            throw new IllegalArgumentException(
                    "Monitoring run not found: "
                            + monitoringRunId
            );
        }

        Risk risk = riskRepository.findById(riskId)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Risk not found: " + riskId
                        )
                );

        if (!monitoringRunId.equals(
                risk.getMonitoringRunId()
        )) {
            throw new IllegalArgumentException(
                    "Risk does not belong to monitoring run: "
                            + riskId
            );
        }
    }

    private UUID requireId(
            UUID value,
            String fieldName
    ) {
        if (value == null) {
            throw new IllegalArgumentException(
                    fieldName + " is required."
            );
        }

        return value;
    }
}