package com.cigabyte.sitesentinel.trust;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrustAssessmentRepository extends JpaRepository<TrustAssessment, UUID> {

    List<TrustAssessment> findByMonitoringRunIdOrderByCreatedAtDesc(UUID monitoringRunId);

    Optional<TrustAssessment> findFirstByMonitoringRunIdOrderByCreatedAtDesc(UUID monitoringRunId);

    Optional<TrustAssessment> findByIdAndMonitoringRunIdAndWebsiteId(
            UUID id,
            UUID monitoringRunId,
            UUID websiteId
    );

    List<TrustAssessment> findTop10ByOrderByCreatedAtDesc();

    long countByMonitoringRunId(UUID monitoringRunId);

    long countByWebsiteId(UUID websiteId);
}