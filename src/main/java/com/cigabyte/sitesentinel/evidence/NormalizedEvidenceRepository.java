package com.cigabyte.sitesentinel.evidence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NormalizedEvidenceRepository extends JpaRepository<NormalizedEvidence, UUID> {

    List<NormalizedEvidence> findByMonitoringRunIdOrderByCreatedAtDesc(UUID monitoringRunId);

    Optional<NormalizedEvidence> findFirstByCollectedEvidenceIdAndNormalizedTypeOrderByCreatedAtAsc(
            UUID collectedEvidenceId,
            String normalizedType
    );

    long countByMonitoringRunId(UUID monitoringRunId);

    long countByWebsiteId(UUID websiteId);
}