package com.cigabyte.sitesentinel.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationEventRepository extends JpaRepository<NotificationEvent, UUID> {

    List<NotificationEvent> findByWebsiteIdOrderByCreatedAtDesc(UUID websiteId);

    List<NotificationEvent> findByMonitoringRunIdOrderByCreatedAtDesc(UUID monitoringRunId);

    List<NotificationEvent> findTop10ByOrderByCreatedAtDesc();

    List<NotificationEvent> findTop10ByStatusOrderByCreatedAtDesc(NotificationEventStatus status);

    long countByStatus(NotificationEventStatus status);

    boolean existsByDeduplicationKey(String deduplicationKey);

    Optional<NotificationEvent> findByDeduplicationKey(String deduplicationKey);
}