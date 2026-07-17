package com.cigabyte.sitesentinel.recommendation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RiskRemediationRecommendationRepository
        extends JpaRepository<RiskRemediationRecommendation, UUID> {

    List<RiskRemediationRecommendation>
    findByMonitoringRunIdOrderByGeneratedAtAscCreatedAtAsc(
            UUID monitoringRunId
    );

    List<RiskRemediationRecommendation>
    findByRiskIdAndMonitoringRunIdOrderByGeneratedAtDescCreatedAtDesc(
            UUID riskId,
            UUID monitoringRunId
    );

    Optional<RiskRemediationRecommendation>
    findFirstByRiskIdAndMonitoringRunIdOrderByGeneratedAtDescCreatedAtDesc(
            UUID riskId,
            UUID monitoringRunId
    );

    long countByMonitoringRunId(UUID monitoringRunId);
}