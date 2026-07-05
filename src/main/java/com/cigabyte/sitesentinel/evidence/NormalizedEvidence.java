package com.cigabyte.sitesentinel.evidence;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "normalized_evidence")
public class NormalizedEvidence {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "website_id", nullable = false)
    private UUID websiteId;

    @Column(name = "monitoring_run_id", nullable = false)
    private UUID monitoringRunId;

    @Column(name = "collected_evidence_id", nullable = false)
    private UUID collectedEvidenceId;

    @Column(name = "normalized_type", nullable = false, length = 80)
    private String normalizedType;

    @Column(name = "normalized_value", nullable = false, columnDefinition = "TEXT")
    private String normalizedValue;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected NormalizedEvidence() {
    }

    public NormalizedEvidence(
            UUID websiteId,
            UUID monitoringRunId,
            UUID collectedEvidenceId,
            String normalizedType,
            String normalizedValue
    ) {
        this.websiteId = websiteId;
        this.monitoringRunId = monitoringRunId;
        this.collectedEvidenceId = collectedEvidenceId;
        this.normalizedType = normalizedType;
        this.normalizedValue = normalizedValue;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getWebsiteId() {
        return websiteId;
    }

    public UUID getMonitoringRunId() {
        return monitoringRunId;
    }

    public UUID getCollectedEvidenceId() {
        return collectedEvidenceId;
    }

    public String getNormalizedType() {
        return normalizedType;
    }

    public String getNormalizedValue() {
        return normalizedValue;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}