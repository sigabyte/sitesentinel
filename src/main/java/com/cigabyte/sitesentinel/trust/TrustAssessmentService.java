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
}