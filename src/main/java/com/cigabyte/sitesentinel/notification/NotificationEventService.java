package com.cigabyte.sitesentinel.notification;

import com.cigabyte.sitesentinel.monitoring.MonitoringRunRepository;
import com.cigabyte.sitesentinel.website.WebsiteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationEventService {

    private final NotificationEventRepository notificationEventRepository;
    private final WebsiteRepository websiteRepository;
    private final MonitoringRunRepository monitoringRunRepository;

    public NotificationEventService(
            NotificationEventRepository notificationEventRepository,
            WebsiteRepository websiteRepository,
            MonitoringRunRepository monitoringRunRepository
    ) {
        this.notificationEventRepository = notificationEventRepository;
        this.websiteRepository = websiteRepository;
        this.monitoringRunRepository = monitoringRunRepository;
    }

    @Transactional
    public NotificationEvent createIfAbsent(NotificationEventCreateRequest request) {
        validateRequest(request);

        return notificationEventRepository.findByDeduplicationKey(request.getDeduplicationKey())
                .orElseGet(() -> create(request));
    }

    @Transactional(readOnly = true)
    public List<NotificationEvent> findLatestEvents() {
        return notificationEventRepository.findTop10ByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<NotificationEvent> findLatestUnreadEvents() {
        return notificationEventRepository.findTop10ByStatusOrderByCreatedAtDesc(
                NotificationEventStatus.UNREAD
        );
    }

    @Transactional(readOnly = true)
    public List<NotificationEvent> findManagedEvents(
            UUID websiteId,
            UUID monitoringRunId,
            NotificationEventStatus status,
            NotificationEventSeverity severity
    ) {
        if (websiteId != null) {
            validateWebsiteExists(websiteId);
        }

        if (monitoringRunId != null) {
            validateMonitoringRunExists(monitoringRunId);
        }

        return notificationEventRepository.findManagedEvents(
                websiteId,
                monitoringRunId,
                status,
                severity
        );
    }

    @Transactional(readOnly = true)
    public List<NotificationEvent> findByWebsiteId(UUID websiteId) {
        validateWebsiteExists(websiteId);
        return notificationEventRepository.findByWebsiteIdOrderByCreatedAtDesc(websiteId);
    }

    @Transactional(readOnly = true)
    public List<NotificationEvent> findByMonitoringRunId(UUID monitoringRunId) {
        validateMonitoringRunExists(monitoringRunId);
        return notificationEventRepository.findByMonitoringRunIdOrderByCreatedAtDesc(monitoringRunId);
    }

    @Transactional(readOnly = true)
    public long countUnreadEvents() {
        return notificationEventRepository.countByStatus(NotificationEventStatus.UNREAD);
    }

    @Transactional(readOnly = true)
    public NotificationEvent findEvent(UUID notificationEventId) {
        return findExistingEvent(notificationEventId);
    }

    @Transactional
    public NotificationEvent markRead(UUID notificationEventId) {
        NotificationEvent notificationEvent = findExistingEvent(notificationEventId);
        notificationEvent.markRead();
        return notificationEventRepository.save(notificationEvent);
    }

    @Transactional
    public NotificationEvent markUnread(UUID notificationEventId) {
        NotificationEvent notificationEvent = findExistingEvent(notificationEventId);
        notificationEvent.markUnread();
        return notificationEventRepository.save(notificationEvent);
    }

    private NotificationEvent create(NotificationEventCreateRequest request) {
        NotificationEvent notificationEvent = new NotificationEvent(
                request.getWebsiteId(),
                request.getMonitoringRunId(),
                request.getEventType(),
                request.getSeverity(),
                request.getTitle().trim(),
                request.getMessage().trim(),
                request.getDeduplicationKey().trim()
        );

        return notificationEventRepository.save(notificationEvent);
    }

    private NotificationEvent findExistingEvent(UUID notificationEventId) {
        if (notificationEventId == null) {
            throw new IllegalArgumentException("Notification event id is required.");
        }

        return notificationEventRepository.findById(notificationEventId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Notification event not found: " + notificationEventId
                ));
    }

    private void validateRequest(NotificationEventCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Notification event request is required.");
        }

        validateWebsiteExists(request.getWebsiteId());
        validateMonitoringRunExists(request.getMonitoringRunId());

        if (request.getEventType() == null) {
            throw new IllegalArgumentException("Notification event type is required.");
        }

        if (request.getSeverity() == null) {
            throw new IllegalArgumentException("Notification event severity is required.");
        }

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("Notification event title is required.");
        }

        if (request.getTitle().trim().length() > 220) {
            throw new IllegalArgumentException("Notification event title must not exceed 220 characters.");
        }

        if (request.getMessage() == null || request.getMessage().isBlank()) {
            throw new IllegalArgumentException("Notification event message is required.");
        }

        if (request.getDeduplicationKey() == null || request.getDeduplicationKey().isBlank()) {
            throw new IllegalArgumentException("Notification event deduplication key is required.");
        }

        if (request.getDeduplicationKey().trim().length() > 300) {
            throw new IllegalArgumentException("Notification event deduplication key must not exceed 300 characters.");
        }
    }

    private void validateWebsiteExists(UUID websiteId) {
        if (websiteId == null) {
            throw new IllegalArgumentException("Website id is required.");
        }

        if (!websiteRepository.existsById(websiteId)) {
            throw new IllegalArgumentException("Website not found: " + websiteId);
        }
    }

    private void validateMonitoringRunExists(UUID monitoringRunId) {
        if (monitoringRunId == null) {
            throw new IllegalArgumentException("Monitoring run id is required.");
        }

        if (!monitoringRunRepository.existsById(monitoringRunId)) {
            throw new IllegalArgumentException("Monitoring run not found: " + monitoringRunId);
        }
    }
}