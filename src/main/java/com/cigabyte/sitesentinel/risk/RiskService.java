package com.cigabyte.sitesentinel.risk;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RiskService {

    private final RiskRepository riskRepository;
    private final RiskFindingRepository riskFindingRepository;

    public RiskService(
            RiskRepository riskRepository,
            RiskFindingRepository riskFindingRepository
    ) {
        this.riskRepository = riskRepository;
        this.riskFindingRepository = riskFindingRepository;
    }

    @Transactional(readOnly = true)
    public List<Risk> findByMonitoringRunId(UUID monitoringRunId) {
        return riskRepository.findByMonitoringRunIdOrderByRiskScoreDescCreatedAtAsc(monitoringRunId);
    }

    @Transactional(readOnly = true)
    public Risk findByIdAndMonitoringRunIdAndWebsiteId(
            UUID riskId,
            UUID monitoringRunId,
            UUID websiteId
    ) {
        return riskRepository.findByIdAndMonitoringRunIdAndWebsiteId(
                        riskId,
                        monitoringRunId,
                        websiteId
                )
                .orElseThrow(() -> new IllegalArgumentException("Risk not found for monitoring run: " + riskId));
    }

    @Transactional(readOnly = true)
    public List<RiskFinding> findFindingLinks(UUID riskId) {
        return riskFindingRepository.findByRiskIdOrderByCreatedAtAsc(riskId);
    }

    @Transactional(readOnly = true)
    public long countByMonitoringRunId(UUID monitoringRunId) {
        return riskRepository.countByMonitoringRunId(monitoringRunId);
    }

    @Transactional(readOnly = true)
    public long countByWebsiteId(UUID websiteId) {
        return riskRepository.countByWebsiteId(websiteId);
    }

    @Transactional
    public Risk recordRisk(
            UUID websiteId,
            UUID monitoringRunId,
            String riskType,
            RiskSeverity severity,
            Integer riskScore,
            Integer confidenceScore,
            String rationale,
            UUID findingId
    ) {
        if (riskType == null || riskType.isBlank()) {
            throw new IllegalArgumentException("Risk type is required.");
        }

        if (severity == null) {
            throw new IllegalArgumentException("Risk severity is required.");
        }

        if (rationale == null || rationale.isBlank()) {
            throw new IllegalArgumentException("Risk rationale is required.");
        }

        String cleanRiskType = riskType.trim();

        Risk savedRisk = riskRepository
                .findFirstByMonitoringRunIdAndRiskTypeOrderByCreatedAtAsc(
                        monitoringRunId,
                        cleanRiskType
                )
                .orElseGet(() -> {
                    Risk risk = new Risk(
                            websiteId,
                            monitoringRunId,
                            cleanRiskType,
                            severity,
                            riskScore,
                            confidenceScore,
                            rationale.trim()
                    );

                    return riskRepository.save(risk);
                });

        if (findingId != null
                && !riskFindingRepository.existsByRiskIdAndFindingId(
                savedRisk.getId(),
                findingId
        )) {
            RiskFinding riskFinding = new RiskFinding(savedRisk.getId(), findingId);
            riskFindingRepository.save(riskFinding);
        }

        return savedRisk;
    }
}