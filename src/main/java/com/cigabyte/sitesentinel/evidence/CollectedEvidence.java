package com.cigabyte.sitesentinel.evidence;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "collected_evidence")
public class CollectedEvidence {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "website_id", nullable = false)
    private UUID websiteId;

    @Column(name = "monitoring_run_id", nullable = false)
    private UUID monitoringRunId;

    @Column(name = "source_type", nullable = false, length = 80)
    private String sourceType;

    @Column(name = "evidence_type", nullable = false, length = 80)
    private String evidenceType;

    @Column(name = "source_url", length = 500)
    private String sourceUrl;

    @Column(name = "raw_value", nullable = false, columnDefinition = "TEXT")
    private String rawValue;

    @Column(name = "collected_at", nullable = false)
    private OffsetDateTime collectedAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected CollectedEvidence() {
    }

    public CollectedEvidence(
            UUID websiteId,
            UUID monitoringRunId,
            String sourceType,
            String evidenceType,
            String sourceUrl,
            String rawValue
    ) {
        this.websiteId = websiteId;
        this.monitoringRunId = monitoringRunId;
        this.sourceType = sourceType;
        this.evidenceType = evidenceType;
        this.sourceUrl = sourceUrl;
        this.rawValue = rawValue;
        this.collectedAt = OffsetDateTime.now();
    }

    @PrePersist
    void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();

        if (this.collectedAt == null) {
            this.collectedAt = now;
        }

        this.createdAt = now;
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

    public String getSourceType() {
        return sourceType;
    }

    public String getEvidenceType() {
        return evidenceType;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getRawValue() {
        return rawValue;
    }

    public OffsetDateTime getCollectedAt() {
        return collectedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}