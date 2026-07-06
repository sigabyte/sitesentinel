package com.cigabyte.sitesentinel.finding;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FindingRepository extends JpaRepository<Finding, UUID> {

    List<Finding> findByMonitoringRunIdOrderByFindingTypeAscCreatedAtAsc(UUID monitoringRunId);

    Optional<Finding> findByIdAndMonitoringRunIdAndWebsiteId(
            UUID id,
            UUID monitoringRunId,
            UUID websiteId
    );

    Optional<Finding> findFirstByMonitoringRunIdAndFindingTypeOrderByCreatedAtAsc(
            UUID monitoringRunId,
            String findingType
    );

    long countByMonitoringRunId(UUID monitoringRunId);

    long countByWebsiteId(UUID websiteId);
}