package com.cigabyte.sitesentinel.trust;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "trust_assessment_risk",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_trust_assessment_risk_pair",
                        columnNames = {"trust_assessment_id", "risk_id"}
                )
        }
)
public class TrustAssessmentRisk {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "trust_assessment_id", nullable = false)
    private UUID trustAssessmentId;

    @Column(name = "risk_id", nullable = false)
    private UUID riskId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected TrustAssessmentRisk() {
    }

    public TrustAssessmentRisk(UUID trustAssessmentId, UUID riskId) {
        this.trustAssessmentId = trustAssessmentId;
        this.riskId = riskId;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getTrustAssessmentId() {
        return trustAssessmentId;
    }

    public UUID getRiskId() {
        return riskId;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}