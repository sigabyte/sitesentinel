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

    @Transactional(readOnly = true)
    public long countCollectedEvidenceByWebsiteId(UUID websiteId) {
        return collectedEvidenceRepository.countByWebsiteId(websiteId);
    }

    @Transactional(readOnly = true)
    public long countNormalizedEvidenceByWebsiteId(UUID websiteId) {
        return normalizedEvidenceRepository.countByWebsiteId(websiteId);
    }

    @Transactional
    public CollectedEvidence recordCollectedEvidence(
            UUID websiteId,
            UUID monitoringRunId,
            String sourceType,
            String evidenceType,
            String sourceUrl,
            String rawValue
    ) {
        if (sourceType == null || sourceType.isBlank()) {
            throw new IllegalArgumentException("Evidence source type is required.");
        }

        if (evidenceType == null || evidenceType.isBlank()) {
            throw new IllegalArgumentException("Evidence type is required.");
        }

        if (rawValue == null || rawValue.isBlank()) {
            throw new IllegalArgumentException("Evidence raw value is required.");
        }

        CollectedEvidence evidence = new CollectedEvidence(
                websiteId,
                monitoringRunId,
                sourceType.trim(),
                evidenceType.trim(),
                sourceUrl,
                rawValue
        );

        return collectedEvidenceRepository.save(evidence);
    }

    @Transactional
    public NormalizedEvidence recordNormalizedEvidence(
            UUID websiteId,
            UUID monitoringRunId,
            UUID collectedEvidenceId,
            String normalizedType,
            String normalizedValue
    ) {
        if (collectedEvidenceId == null) {
            throw new IllegalArgumentException("Collected evidence id is required.");
        }

        if (normalizedType == null || normalizedType.isBlank()) {
            throw new IllegalArgumentException("Normalized evidence type is required.");
        }

        if (normalizedValue == null || normalizedValue.isBlank()) {
            throw new IllegalArgumentException("Normalized evidence value is required.");
        }

        NormalizedEvidence normalizedEvidence = new NormalizedEvidence(
                websiteId,
                monitoringRunId,
                collectedEvidenceId,
                normalizedType.trim(),
                normalizedValue.trim()
        );

        return normalizedEvidenceRepository.save(normalizedEvidence);
    }
}