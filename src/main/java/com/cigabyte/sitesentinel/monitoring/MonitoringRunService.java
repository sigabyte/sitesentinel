package com.cigabyte.sitesentinel.monitoring;

import com.cigabyte.sitesentinel.website.WebsiteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MonitoringRunService {

    private final MonitoringRunRepository monitoringRunRepository;
    private final WebsiteRepository websiteRepository;

    public MonitoringRunService(
            MonitoringRunRepository monitoringRunRepository,
            WebsiteRepository websiteRepository
    ) {
        this.monitoringRunRepository = monitoringRunRepository;
        this.websiteRepository = websiteRepository;
    }

    @Transactional(readOnly = true)
    public List<MonitoringRun> findByWebsiteId(UUID websiteId) {
        return monitoringRunRepository.findByWebsiteIdOrderByCreatedAtDesc(websiteId);
    }

    @Transactional(readOnly = true)
    public List<MonitoringRun> findCompletedRunsByWebsiteId(UUID websiteId) {
        return monitoringRunRepository.findByWebsiteIdAndStatusOrderByCompletedAtDesc(
                websiteId,
                MonitoringRunStatus.COMPLETED
        );
    }

    @Transactional(readOnly = true)
    public Optional<MonitoringRun> findLatestCompletedRun(UUID websiteId) {
        return monitoringRunRepository.findFirstByWebsiteIdAndStatusOrderByCompletedAtDesc(
                websiteId,
                MonitoringRunStatus.COMPLETED
        );
    }

    @Transactional(readOnly = true)
    public Optional<MonitoringRun> findPreviousCompletedRun(MonitoringRun currentRun) {
        if (currentRun == null) {
            return Optional.empty();
        }

        if (currentRun.getStatus() != MonitoringRunStatus.COMPLETED) {
            return Optional.empty();
        }

        if (currentRun.getCompletedAt() == null) {
            return Optional.empty();
        }

        return monitoringRunRepository.findFirstByWebsiteIdAndStatusAndCompletedAtBeforeOrderByCompletedAtDesc(
                currentRun.getWebsiteId(),
                MonitoringRunStatus.COMPLETED,
                currentRun.getCompletedAt()
        );
    }

    @Transactional
    public MonitoringRun createPendingRun(UUID websiteId) {
        return createPendingRun(
                websiteId,
                MonitoringRunTriggerType.MANUAL,
                null
        );
    }

    @Transactional
    public MonitoringRun createScheduledPendingRun(UUID websiteId, UUID monitoringScheduleId) {
        return createPendingRun(
                websiteId,
                MonitoringRunTriggerType.SCHEDULED,
                monitoringScheduleId
        );
    }

    private MonitoringRun createPendingRun(
            UUID websiteId,
            MonitoringRunTriggerType triggerType,
            UUID monitoringScheduleId
    ) {
        boolean websiteExists = websiteRepository.existsById(websiteId);

        if (!websiteExists) {
            throw new IllegalArgumentException("Website not found: " + websiteId);
        }

        MonitoringRun monitoringRun = new MonitoringRun(
                websiteId,
                triggerType,
                monitoringScheduleId
        );

        return monitoringRunRepository.save(monitoringRun);
    }

    @Transactional(readOnly = true)
    public MonitoringRun findByIdAndWebsiteId(UUID runId, UUID websiteId) {
        MonitoringRun monitoringRun = monitoringRunRepository.findById(runId)
                .orElseThrow(() -> new IllegalArgumentException("Monitoring run not found: " + runId));

        if (!monitoringRun.getWebsiteId().equals(websiteId)) {
            throw new IllegalArgumentException("Monitoring run does not belong to website: " + websiteId);
        }

        return monitoringRun;
    }

    @Transactional(readOnly = true)
    public Optional<MonitoringRun> findPreviousCompletedRun(UUID websiteId, UUID currentRunId) {
        MonitoringRun currentRun = findByIdAndWebsiteId(currentRunId, websiteId);
        return findPreviousCompletedRun(currentRun);
    }

    @Transactional(readOnly = true)
    public long countByWebsiteId(UUID websiteId) {
        return monitoringRunRepository.countByWebsiteId(websiteId);
    }

    @Transactional(readOnly = true)
    public boolean hasActiveRunForWebsite(UUID websiteId) {
        return monitoringRunRepository.existsByWebsiteIdAndStatusIn(
                websiteId,
                List.of(
                        MonitoringRunStatus.PENDING,
                        MonitoringRunStatus.RUNNING
                )
        );
    }

    @Transactional(readOnly = true)
    public List<MonitoringRun> findActiveRunsForWebsite(UUID websiteId) {
        return monitoringRunRepository.findByWebsiteIdAndStatusInOrderByCreatedAtDesc(
                websiteId,
                List.of(
                        MonitoringRunStatus.PENDING,
                        MonitoringRunStatus.RUNNING
                )
        );
    }

    @Transactional
    public List<MonitoringRun> recoverStaleActiveRunsForWebsite(
            UUID websiteId,
            OffsetDateTime staleBefore,
            String failureReason
    ) {
        List<MonitoringRun> activeRuns = findActiveRunsForWebsite(websiteId);

        return activeRuns.stream()
                .filter(activeRun -> isStaleActiveRun(activeRun, staleBefore))
                .map(activeRun -> markFailed(activeRun.getId(), failureReason))
                .toList();
    }

    private boolean isStaleActiveRun(MonitoringRun monitoringRun, OffsetDateTime staleBefore) {
        if (monitoringRun == null) {
            return false;
        }

        OffsetDateTime referenceTime = monitoringRun.getStartedAt() == null
                ? monitoringRun.getCreatedAt()
                : monitoringRun.getStartedAt();

        if (referenceTime == null) {
            return false;
        }

        return referenceTime.isBefore(staleBefore);
    }

    @Transactional
    public MonitoringRun markRunning(UUID runId) {
        MonitoringRun monitoringRun = monitoringRunRepository.findById(runId)
                .orElseThrow(() -> new IllegalArgumentException("Monitoring run not found: " + runId));

        monitoringRun.markRunning();

        return monitoringRunRepository.save(monitoringRun);
    }

    @Transactional
    public MonitoringRun markCompleted(UUID runId) {
        MonitoringRun monitoringRun = monitoringRunRepository.findById(runId)
                .orElseThrow(() -> new IllegalArgumentException("Monitoring run not found: " + runId));

        monitoringRun.markCompleted();

        return monitoringRunRepository.save(monitoringRun);
    }

    @Transactional
    public MonitoringRun markFailed(UUID runId, String failureReason) {
        MonitoringRun monitoringRun = monitoringRunRepository.findById(runId)
                .orElseThrow(() -> new IllegalArgumentException("Monitoring run not found: " + runId));

        monitoringRun.markFailed(failureReason);

        return monitoringRunRepository.save(monitoringRun);
    }
}