package com.cigabyte.sitesentinel.notification;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationDeliveryAttemptService {

    private static final int MAX_RESULT_MESSAGE_LENGTH = 500;

    private final NotificationDeliveryAttemptRepository notificationDeliveryAttemptRepository;
    private final NotificationEventRepository notificationEventRepository;

    public NotificationDeliveryAttemptService(
            NotificationDeliveryAttemptRepository notificationDeliveryAttemptRepository,
            NotificationEventRepository notificationEventRepository
    ) {
        this.notificationDeliveryAttemptRepository = notificationDeliveryAttemptRepository;
        this.notificationEventRepository = notificationEventRepository;
    }

    @Transactional(readOnly = true)
    public List<NotificationDeliveryAttempt> findAttemptsForEvent(UUID notificationEventId) {
        validateNotificationEventExists(notificationEventId);

        return notificationDeliveryAttemptRepository.findByNotificationEventIdOrderByAttemptedAtDesc(
                notificationEventId
        );
    }

    @Transactional(readOnly = true)
    public long countAttemptsForEvent(UUID notificationEventId) {
        validateNotificationEventExists(notificationEventId);

        return notificationDeliveryAttemptRepository.countByNotificationEventId(notificationEventId);
    }

    @Transactional
    public NotificationDeliveryAttempt recordSimulatedSuccess(
            UUID notificationEventId,
            NotificationDeliveryChannel channel
    ) {
        validateNotificationEventExists(notificationEventId);
        validateChannel(channel);

        String channelLabel = formatChannel(channel);

        return recordAttempt(
                notificationEventId,
                channel,
                NotificationDeliveryAttemptStatus.SIMULATED_SUCCESS,
                "Simulated " + channelLabel + " delivery attempt recorded successfully.",
                "Sprint 8 simulation only. No external " + channelLabel + " provider was called."
        );
    }

    @Transactional
    public NotificationDeliveryAttempt recordSimulatedFailure(
            UUID notificationEventId,
            NotificationDeliveryChannel channel
    ) {
        validateNotificationEventExists(notificationEventId);
        validateChannel(channel);

        String channelLabel = formatChannel(channel);

        return recordAttempt(
                notificationEventId,
                channel,
                NotificationDeliveryAttemptStatus.SIMULATED_FAILURE,
                "Simulated " + channelLabel + " delivery attempt recorded as failed.",
                "Sprint 8 simulation only. This is not a real provider failure."
        );
    }

    @Transactional
    public NotificationDeliveryAttempt recordSkipped(
            UUID notificationEventId,
            NotificationDeliveryChannel channel
    ) {
        validateNotificationEventExists(notificationEventId);
        validateChannel(channel);

        String channelLabel = formatChannel(channel);

        return recordAttempt(
                notificationEventId,
                channel,
                NotificationDeliveryAttemptStatus.SKIPPED,
                "Simulated " + channelLabel + " delivery attempt skipped.",
                "Sprint 8 simulation only. Delivery was skipped without calling an external provider."
        );
    }

    private NotificationDeliveryAttempt recordAttempt(
            UUID notificationEventId,
            NotificationDeliveryChannel channel,
            NotificationDeliveryAttemptStatus status,
            String resultMessage,
            String technicalDetail
    ) {
        OffsetDateTime now = OffsetDateTime.now();

        NotificationDeliveryAttempt attempt = new NotificationDeliveryAttempt(
                notificationEventId,
                channel,
                status,
                now,
                now,
                truncateResultMessage(resultMessage),
                technicalDetail
        );

        return notificationDeliveryAttemptRepository.save(attempt);
    }

    private void validateNotificationEventExists(UUID notificationEventId) {
        if (notificationEventId == null) {
            throw new IllegalArgumentException("Notification event id is required.");
        }

        if (!notificationEventRepository.existsById(notificationEventId)) {
            throw new IllegalArgumentException("Notification event not found: " + notificationEventId);
        }
    }

    private void validateChannel(NotificationDeliveryChannel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("Notification delivery channel is required.");
        }
    }

    private String truncateResultMessage(String value) {
        if (value == null) {
            return null;
        }

        if (value.length() <= MAX_RESULT_MESSAGE_LENGTH) {
            return value;
        }

        return value.substring(0, MAX_RESULT_MESSAGE_LENGTH);
    }

    private String formatChannel(NotificationDeliveryChannel channel) {
        return channel.name()
                .toLowerCase()
                .replace("_", " ");
    }
}