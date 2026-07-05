package com.cigabyte.sitesentinel.trust;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TrustAssessmentRepository extends JpaRepository<TrustAssessment, UUID> {

    List<TrustAssessment> findByMonitoringRunIdOrderByCreatedAtDesc(UUID monitoringRunId);

    List<TrustAssessment> findTop10ByOrderByCreatedAtDesc();

    long countByMonitoringRunId(UUID monitoringRunId);
}