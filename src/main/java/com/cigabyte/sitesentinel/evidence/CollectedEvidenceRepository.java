package com.cigabyte.sitesentinel.evidence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CollectedEvidenceRepository extends JpaRepository<CollectedEvidence, UUID> {

    List<CollectedEvidence> findByMonitoringRunIdOrderBySourceTypeAscEvidenceTypeAscCollectedAtAsc(UUID monitoringRunId);

    List<CollectedEvidence> findByIdInAndMonitoringRunIdAndWebsiteIdOrderBySourceTypeAscEvidenceTypeAscCollectedAtAsc(
            List<UUID> ids,
            UUID monitoringRunId,
            UUID websiteId
    );

    List<CollectedEvidence> findByMonitoringRunIdAndSourceTypeOrderByEvidenceTypeAscCollectedAtAsc(
            UUID monitoringRunId,
            String sourceType
    );

    List<CollectedEvidence> findByMonitoringRunIdAndSourceTypeNotInOrderBySourceTypeAscEvidenceTypeAscCollectedAtAsc(
            UUID monitoringRunId,
            List<String> sourceTypes
    );

    long countByMonitoringRunId(UUID monitoringRunId);

    long countByWebsiteId(UUID websiteId);
}