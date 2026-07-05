package com.cigabyte.sitesentinel.evidence;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EvidenceService {

    private final CollectedEvidenceRepository collectedEvidenceRepository;
    private final NormalizedEvidenceRepository normalizedEvidenceRepository;

    public EvidenceService(
            CollectedEvidenceRepository collectedEvidenceRepository,
            NormalizedEvidenceRepository normalizedEvidenceRepository
    ) {
        this.collectedEvidenceRepository = collectedEvidenceRepository;
        this.normalizedEvidenceRepository = normalizedEvidenceRepository;
    }

    @Transactional(readOnly = true)
    public List<CollectedEvidence> findCollectedEvidence(UUID monitoringRunId) {
        return collectedEvidenceRepository.findByMonitoringRunIdOrderByCollectedAtDesc(monitoringRunId);
    }

    @Transactional(readOnly = true)
    public List<NormalizedEvidence> findNormalizedEvidence(UUID monitoringRunId) {
        return normalizedEvidenceRepository.findByMonitoringRunIdOrderByCreatedAtDesc(monitoringRunId);
    }

    @Transactional(readOnly = true)
    public long countCollectedEvidence(UUID monitoringRunId) {
        return collectedEvidenceRepository.countByMonitoringRunId(monitoringRunId);
    }

    @Transactional(readOnly = true)
    public long countNormalizedEvidence(UUID monitoringRunId) {
        return normalizedEvidenceRepository.countByMonitoringRunId(monitoringRunId);
    }
}