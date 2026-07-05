package com.cigabyte.sitesentinel.finding;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FindingEvidenceRepository extends JpaRepository<FindingEvidence, UUID> {

    List<FindingEvidence> findByFindingId(UUID findingId);

    long countByFindingId(UUID findingId);
}