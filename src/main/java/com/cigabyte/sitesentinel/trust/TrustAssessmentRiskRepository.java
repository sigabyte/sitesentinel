package com.cigabyte.sitesentinel.trust;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TrustAssessmentRiskRepository extends JpaRepository<TrustAssessmentRisk, UUID> {

    List<TrustAssessmentRisk> findByTrustAssessmentId(UUID trustAssessmentId);

    long countByTrustAssessmentId(UUID trustAssessmentId);
}