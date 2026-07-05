package com.cigabyte.sitesentinel.trust;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "trust_assessments")
public class TrustAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "website_id", nullable = false)
    private UUID websiteId;

    @Column(name = "monitoring_run_id", nullable = false)
    private UUID monitoringRunId;

    @Enumerated(EnumType.STRING)
    @Column(name = "trust_status", nullable = false, length = 40)
    private TrustStatus trustStatus;

    @Column(name = "trust_score", nullable = false)
    private Integer trustScore;

    @Column(name = "confidence_score", nullable = false)
    private Integer confidenceScore;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected TrustAssessment() {
    }

    public TrustAssessment(
            UUID websiteId,
            UUID monitoringRunId,
            TrustStatus trustStatus,
            Integer trustScore,
            Integer confidenceScore,
            String summary
    ) {
        this.websiteId = websiteId;
        this.monitoringRunId = monitoringRunId;
        this.trustStatus = trustStatus;
        this.trustScore = trustScore;
        this.confidenceScore = confidenceScore;
        this.summary = summary;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = OffsetDateTime.now();

        if (this.trustStatus == null) {
            this.trustStatus = TrustStatus.UNKNOWN;
        }

        if (this.trustScore == null) {
            this.trustScore = 0;
        }

        if (this.confidenceScore == null) {
            this.confidenceScore = 0;
        }
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

    public TrustStatus getTrustStatus() {
        return trustStatus;
    }

    public Integer getTrustScore() {
        return trustScore;
    }

    public Integer getConfidenceScore() {
        return confidenceScore;
    }

    public String getSummary() {
        return summary;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}