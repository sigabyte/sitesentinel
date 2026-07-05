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
}