package com.cigabyte.sitesentinel.risk;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "risks")
public class Risk {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "website_id", nullable = false)
    private UUID websiteId;

    @Column(name = "monitoring_run_id", nullable = false)
    private UUID monitoringRunId;

    @Column(name = "risk_type", nullable = false, length = 100)
    private String riskType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RiskSeverity severity;

    @Column(name = "risk_score", nullable = false)
    private Integer riskScore;

    @Column(name = "confidence_score", nullable = false)
    private Integer confidenceScore;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String rationale;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected Risk() {
    }

    public Risk(
            UUID websiteId,
            UUID monitoringRunId,
            String riskType,
            RiskSeverity severity,
            Integer riskScore,
            Integer confidenceScore,
            String rationale
    ) {
        this.websiteId = websiteId;
        this.monitoringRunId = monitoringRunId;
        this.riskType = riskType;
        this.severity = severity;
        this.riskScore = riskScore;
        this.confidenceScore = confidenceScore;
        this.rationale = rationale;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = OffsetDateTime.now();

        if (this.severity == null) {
            this.severity = RiskSeverity.LOW;
        }

        if (this.riskScore == null) {
            this.riskScore = 0;
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

    public String getRiskType() {
        return riskType;
    }

    public RiskSeverity getSeverity() {
        return severity;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public Integer getConfidenceScore() {
        return confidenceScore;
    }

    public String getRationale() {
        return rationale;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}