package com.cigabyte.sitesentinel.finding;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "finding_evidence",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_finding_evidence_pair",
                        columnNames = {"finding_id", "collected_evidence_id"}
                )
        }
)
public class FindingEvidence {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "finding_id", nullable = false)
    private UUID findingId;

    @Column(name = "collected_evidence_id", nullable = false)
    private UUID collectedEvidenceId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected FindingEvidence() {
    }

    public FindingEvidence(UUID findingId, UUID collectedEvidenceId) {
        this.findingId = findingId;
        this.collectedEvidenceId = collectedEvidenceId;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getFindingId() {
        return findingId;
    }

    public UUID getCollectedEvidenceId() {
        return collectedEvidenceId;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}