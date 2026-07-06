package com.cigabyte.sitesentinel.finding;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FindingEvidenceRepository extends JpaRepository<FindingEvidence, UUID> {

    List<FindingEvidence> findByFindingId(UUID findingId);

    List<FindingEvidence> findByCollectedEvidenceIdOrderByCreatedAtAsc(UUID collectedEvidenceId);

    boolean existsByFindingIdAndCollectedEvidenceId(UUID findingId, UUID collectedEvidenceId);

    long countByFindingId(UUID findingId);

    long countByCollectedEvidenceId(UUID collectedEvidenceId);
}