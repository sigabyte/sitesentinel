package com.cigabyte.sitesentinel.evidence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CollectedEvidenceRepository extends JpaRepository<CollectedEvidence, UUID> {

    List<CollectedEvidence> findByMonitoringRunIdOrderByCollectedAtDesc(UUID monitoringRunId);

    long countByMonitoringRunId(UUID monitoringRunId);

    long countByWebsiteId(UUID websiteId);
}