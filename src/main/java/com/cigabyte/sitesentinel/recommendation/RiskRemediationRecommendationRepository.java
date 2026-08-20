package com.cigabyte.sitesentinel.recommendation;

import com.cigabyte.sitesentinel.reporting.SiteSentinelReportLanguage;
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

    Optional<RiskRemediationRecommendation>
    findFirstByRiskIdAndMonitoringRunIdAndReportLanguageOrderByGeneratedAtDescCreatedAtDesc(
            UUID riskId,
            UUID monitoringRunId,
            SiteSentinelReportLanguage reportLanguage
    );

    boolean existsByRiskIdAndMonitoringRunId(
            UUID riskId,
            UUID monitoringRunId
    );

    boolean existsByRiskIdAndMonitoringRunIdAndReportLanguage(
            UUID riskId,
            UUID monitoringRunId,
            SiteSentinelReportLanguage reportLanguage
    );

    long countByMonitoringRunId(UUID monitoringRunId);
}