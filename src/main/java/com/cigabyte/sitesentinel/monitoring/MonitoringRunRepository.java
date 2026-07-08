package com.cigabyte.sitesentinel.monitoring;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Collection;

public interface MonitoringRunRepository extends JpaRepository<MonitoringRun, UUID> {

    List<MonitoringRun> findByWebsiteIdOrderByCreatedAtDesc(UUID websiteId);

    List<MonitoringRun> findByWebsiteIdAndStatusOrderByCompletedAtDesc(
            UUID websiteId,
            MonitoringRunStatus status
    );

    Optional<MonitoringRun> findFirstByWebsiteIdAndStatusOrderByCompletedAtDesc(
            UUID websiteId,
            MonitoringRunStatus status
    );

    Optional<MonitoringRun> findFirstByWebsiteIdAndStatusAndCompletedAtBeforeOrderByCompletedAtDesc(
            UUID websiteId,
            MonitoringRunStatus status,
            OffsetDateTime completedAt
    );

    List<MonitoringRun> findTop10ByOrderByCreatedAtDesc();

    long countByWebsiteId(UUID websiteId);

    long countByStatus(MonitoringRunStatus status);

    boolean existsByWebsiteIdAndStatusIn(
            UUID websiteId,
            Collection<MonitoringRunStatus> statuses
    );

    List<MonitoringRun> findByWebsiteIdAndStatusInOrderByCreatedAtDesc(
            UUID websiteId,
            Collection<MonitoringRunStatus> statuses
    );
}