package com.cigabyte.sitesentinel.risk;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "risk_finding",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_risk_finding_pair",
                        columnNames = {"risk_id", "finding_id"}
                )
        }
)
public class RiskFinding {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "risk_id", nullable = false)
    private UUID riskId;

    @Column(name = "finding_id", nullable = false)
    private UUID findingId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected RiskFinding() {
    }

    public RiskFinding(UUID riskId, UUID findingId) {
        this.riskId = riskId;
        this.findingId = findingId;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getRiskId() {
        return riskId;
    }

    public UUID getFindingId() {
        return findingId;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}