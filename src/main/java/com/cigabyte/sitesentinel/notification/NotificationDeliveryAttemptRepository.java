package com.cigabyte.sitesentinel.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationDeliveryAttemptRepository extends JpaRepository<NotificationDeliveryAttempt, UUID> {

    List<NotificationDeliveryAttempt> findByNotificationEventIdOrderByAttemptedAtDesc(UUID notificationEventId);

    long countByNotificationEventId(UUID notificationEventId);
}