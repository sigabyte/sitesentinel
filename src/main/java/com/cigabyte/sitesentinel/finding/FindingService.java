package com.cigabyte.sitesentinel.finding;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FindingService {

    private final FindingRepository findingRepository;
    private final FindingEvidenceRepository findingEvidenceRepository;

    public FindingService(
            FindingRepository findingRepository,
            FindingEvidenceRepository findingEvidenceRepository
    ) {
        this.findingRepository = findingRepository;
        this.findingEvidenceRepository = findingEvidenceRepository;
    }

    @Transactional(readOnly = true)
    public List<Finding> findByMonitoringRunId(UUID monitoringRunId) {
        return findingRepository.findByMonitoringRunIdOrderByCreatedAtDesc(monitoringRunId);
    }

    @Transactional(readOnly = true)
    public List<FindingEvidence> findEvidenceLinks(UUID findingId) {
        return findingEvidenceRepository.findByFindingId(findingId);
    }

    @Transactional(readOnly = true)
    public long countByMonitoringRunId(UUID monitoringRunId) {
        return findingRepository.countByMonitoringRunId(monitoringRunId);
    }

    @Transactional(readOnly = true)
    public long countByWebsiteId(UUID websiteId) {
        return findingRepository.countByWebsiteId(websiteId);
    }

    @Transactional
    public Finding recordFinding(
            UUID websiteId,
            UUID monitoringRunId,
            String findingType,
            String title,
            String description,
            Integer confidenceScore,
            UUID collectedEvidenceId
    ) {
        if (findingType == null || findingType.isBlank()) {
            throw new IllegalArgumentException("Finding type is required.");
        }

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Finding title is required.");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Finding description is required.");
        }

        Finding finding = new Finding(
                websiteId,
                monitoringRunId,
                findingType.trim(),
                title.trim(),
                description.trim(),
                confidenceScore
        );

        Finding savedFinding = findingRepository.save(finding);

        if (collectedEvidenceId != null) {
            FindingEvidence findingEvidence = new FindingEvidence(savedFinding.getId(), collectedEvidenceId);
            findingEvidenceRepository.save(findingEvidence);
        }

        return savedFinding;
    }
}