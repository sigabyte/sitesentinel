package com.cigabyte.sitesentinel.trust;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TrustAssessmentService {

    private final TrustAssessmentRepository trustAssessmentRepository;
    private final TrustAssessmentRiskRepository trustAssessmentRiskRepository;

    public TrustAssessmentService(
            TrustAssessmentRepository trustAssessmentRepository,
            TrustAssessmentRiskRepository trustAssessmentRiskRepository
    ) {
        this.trustAssessmentRepository = trustAssessmentRepository;
        this.trustAssessmentRiskRepository = trustAssessmentRiskRepository;
    }

    @Transactional(readOnly = true)
    public List<TrustAssessment> findByMonitoringRunId(UUID monitoringRunId) {
        return trustAssessmentRepository.findByMonitoringRunIdOrderByCreatedAtDesc(monitoringRunId);
    }

    @Transactional(readOnly = true)
    public List<TrustAssessmentRisk> findRiskLinks(UUID trustAssessmentId) {
        return trustAssessmentRiskRepository.findByTrustAssessmentId(trustAssessmentId);
    }

    @Transactional(readOnly = true)
    public long countByMonitoringRunId(UUID monitoringRunId) {
        return trustAssessmentRepository.countByMonitoringRunId(monitoringRunId);
    }

    @Transactional(readOnly = true)
    public long countByWebsiteId(UUID websiteId) {
        return trustAssessmentRepository.countByWebsiteId(websiteId);
    }

    @Transactional
    public TrustAssessment recordTrustAssessment(
            UUID websiteId,
            UUID monitoringRunId,
            TrustStatus trustStatus,
            Integer trustScore,
            Integer confidenceScore,
            String summary,
            List<UUID> riskIds
    ) {
        if (trustStatus == null) {
            throw new IllegalArgumentException("Trust status is required.");
        }

        if (summary == null || summary.isBlank()) {
            throw new IllegalArgumentException("Trust assessment summary is required.");
        }

        TrustAssessment trustAssessment = new TrustAssessment(
                websiteId,
                monitoringRunId,
                trustStatus,
                trustScore,
                confidenceScore,
                summary.trim()
        );

        TrustAssessment savedAssessment = trustAssessmentRepository.save(trustAssessment);

        if (riskIds != null) {
            for (UUID riskId : riskIds) {
                TrustAssessmentRisk assessmentRisk =
                        new TrustAssessmentRisk(savedAssessment.getId(), riskId);

                trustAssessmentRiskRepository.save(assessmentRisk);
            }
        }

        return savedAssessment;
    }
}