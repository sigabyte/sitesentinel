package com.cigabyte.sitesentinel.risk;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RiskRepository extends JpaRepository<Risk, UUID> {

    List<Risk> findByMonitoringRunIdOrderByRiskScoreDescCreatedAtAsc(UUID monitoringRunId);

    Optional<Risk> findByIdAndMonitoringRunIdAndWebsiteId(
            UUID id,
            UUID monitoringRunId,
            UUID websiteId
    );

    List<Risk> findByIdInAndMonitoringRunIdAndWebsiteIdOrderByRiskScoreDescCreatedAtAsc(
            List<UUID> ids,
            UUID monitoringRunId,
            UUID websiteId
    );

    Optional<Risk> findFirstByMonitoringRunIdAndRiskTypeOrderByCreatedAtAsc(
            UUID monitoringRunId,
            String riskType
    );

    long countByMonitoringRunId(UUID monitoringRunId);

    long countByWebsiteId(UUID websiteId);
}