package com.cigabyte.sitesentinel.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationEventRepository extends JpaRepository<NotificationEvent, UUID> {

    List<NotificationEvent> findByWebsiteIdOrderByCreatedAtDesc(UUID websiteId);

    List<NotificationEvent> findByMonitoringRunIdOrderByCreatedAtDesc(UUID monitoringRunId);

    List<NotificationEvent> findTop10ByOrderByCreatedAtDesc();

    List<NotificationEvent> findTop10ByStatusOrderByCreatedAtDesc(NotificationEventStatus status);

    @Query("""
        select notificationEvent
        from NotificationEvent notificationEvent
        where (:websiteId is null or notificationEvent.websiteId = :websiteId)
          and (:monitoringRunId is null or notificationEvent.monitoringRunId = :monitoringRunId)
          and (:status is null or notificationEvent.status = :status)
          and (:severity is null or notificationEvent.severity = :severity)
        order by notificationEvent.createdAt desc
        """)
    List<NotificationEvent> findManagedEvents(
            @Param("websiteId") UUID websiteId,
            @Param("monitoringRunId") UUID monitoringRunId,
            @Param("status") NotificationEventStatus status,
            @Param("severity") NotificationEventSeverity severity
    );

    long countByStatus(NotificationEventStatus status);

    boolean existsByDeduplicationKey(String deduplicationKey);

    Optional<NotificationEvent> findByDeduplicationKey(String deduplicationKey);
}