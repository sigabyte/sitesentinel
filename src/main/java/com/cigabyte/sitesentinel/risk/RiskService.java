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
        return riskRepository.findByMonitoringRunIdOrderByCreatedAtDesc(monitoringRunId);
    }

    @Transactional(readOnly = true)
    public List<RiskFinding> findFindingLinks(UUID riskId) {
        return riskFindingRepository.findByRiskId(riskId);
    }

    @Transactional(readOnly = true)
    public long countByMonitoringRunId(UUID monitoringRunId) {
        return riskRepository.countByMonitoringRunId(monitoringRunId);
    }
}