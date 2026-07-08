package com.cigabyte.sitesentinel.scheduling;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MonitoringScheduleRepository extends JpaRepository<MonitoringSchedule, UUID> {

    Optional<MonitoringSchedule> findByWebsiteId(UUID websiteId);

    boolean existsByWebsiteId(UUID websiteId);

    List<MonitoringSchedule> findByStatusAndNextRunAtLessThanEqualOrderByNextRunAtAsc(
            MonitoringScheduleStatus status,
            OffsetDateTime nextRunAt
    );
}