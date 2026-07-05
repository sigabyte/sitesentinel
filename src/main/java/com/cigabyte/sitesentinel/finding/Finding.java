package com.cigabyte.sitesentinel.finding;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "findings")
public class Finding {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "website_id", nullable = false)
    private UUID websiteId;

    @Column(name = "monitoring_run_id", nullable = false)
    private UUID monitoringRunId;

    @Column(name = "finding_type", nullable = false, length = 100)
    private String findingType;

    @Column(nullable = false, length = 220)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "confidence_score", nullable = false)
    private Integer confidenceScore;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected Finding() {
    }

    public Finding(
            UUID websiteId,
            UUID monitoringRunId,
            String findingType,
            String title,
            String description,
            Integer confidenceScore
    ) {
        this.websiteId = websiteId;
        this.monitoringRunId = monitoringRunId;
        this.findingType = findingType;
        this.title = title;
        this.description = description;
        this.confidenceScore = confidenceScore;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = OffsetDateTime.now();

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

    public String getFindingType() {
        return findingType;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getConfidenceScore() {
        return confidenceScore;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}