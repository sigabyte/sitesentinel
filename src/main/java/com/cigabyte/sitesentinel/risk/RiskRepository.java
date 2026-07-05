package com.cigabyte.sitesentinel.risk;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RiskRepository extends JpaRepository<Risk, UUID> {

    List<Risk> findByMonitoringRunIdOrderByRiskScoreDescCreatedAtAsc(UUID monitoringRunId);

    Optional<Risk> findFirstByMonitoringRunIdAndRiskTypeOrderByCreatedAtAsc(
            UUID monitoringRunId,
            String riskType
    );

    long countByMonitoringRunId(UUID monitoringRunId);

    long countByWebsiteId(UUID websiteId);
}