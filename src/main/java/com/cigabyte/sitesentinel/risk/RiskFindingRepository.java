package com.cigabyte.sitesentinel.risk;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RiskFindingRepository extends JpaRepository<RiskFinding, UUID> {

    List<RiskFinding> findByRiskId(UUID riskId);

    boolean existsByRiskIdAndFindingId(UUID riskId, UUID findingId);

    long countByRiskId(UUID riskId);
}