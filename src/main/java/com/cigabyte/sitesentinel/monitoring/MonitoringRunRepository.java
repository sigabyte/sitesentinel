package com.cigabyte.sitesentinel.monitoring;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MonitoringRunRepository extends JpaRepository<MonitoringRun, UUID> {

    List<MonitoringRun> findByWebsiteIdOrderByCreatedAtDesc(UUID websiteId);

    List<MonitoringRun> findTop10ByOrderByCreatedAtDesc();

    long countByStatus(MonitoringRunStatus status);
}