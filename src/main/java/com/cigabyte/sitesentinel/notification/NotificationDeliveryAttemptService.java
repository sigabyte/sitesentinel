package com.cigabyte.sitesentinel.notification;

import com.cigabyte.sitesentinel.notification.delivery.NotificationDeliveryProvider;
import com.cigabyte.sitesentinel.notification.delivery.NotificationDeliveryProviderResult;
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
    private final List<NotificationDeliveryProvider> notificationDeliveryProviders;

    public NotificationDeliveryAttemptService(
            NotificationDeliveryAttemptRepository notificationDeliveryAttemptRepository,
            NotificationEventRepository notificationEventRepository,
            List<NotificationDeliveryProvider> notificationDeliveryProviders
    ) {
        this.notificationDeliveryAttemptRepository = notificationDeliveryAttemptRepository;
        this.notificationEventRepository = notificationEventRepository;
        this.notificationDeliveryProviders = notificationDeliveryProviders;
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

    @Transactional
    public NotificationDeliveryAttempt recordRealTelegramTestDelivery(UUID notificationEventId) {
        NotificationEvent notificationEvent = findExistingNotificationEvent(notificationEventId);
        OffsetDateTime attemptedAt = OffsetDateTime.now();

        NotificationDeliveryProvider provider = findProvider(NotificationDeliveryChannel.TELEGRAM);

        if (provider == null) {
            return recordAttempt(
                    notificationEventId,
                    NotificationDeliveryChannel.TELEGRAM,
                    NotificationDeliveryAttemptStatus.FAILED,
                    "Telegram delivery provider is not registered.",
                    "No NotificationDeliveryProvider bean was found for channel TELEGRAM.",
                    attemptedAt,
                    OffsetDateTime.now()
            );
        }

        NotificationDeliveryProviderResult providerResult = deliverSafely(provider, notificationEvent);

        return recordAttempt(
                notificationEventId,
                NotificationDeliveryChannel.TELEGRAM,
                resolveAttemptStatus(providerResult),
                resolveResultMessage(providerResult),
                resolveTechnicalDetail(providerResult),
                attemptedAt,
                OffsetDateTime.now()
        );
    }

    private NotificationDeliveryProviderResult deliverSafely(
            NotificationDeliveryProvider provider,
            NotificationEvent notificationEvent
    ) {
        try {
            return provider.deliver(notificationEvent);
        } catch (RuntimeException exception) {
            return NotificationDeliveryProviderResult.failure(
                    "Telegram delivery failed before the provider returned a result.",
                    exception.getClass().getSimpleName() + ": " + safeMessage(exception.getMessage())
            );
        }
    }

    private NotificationDeliveryProvider findProvider(NotificationDeliveryChannel channel) {
        if (notificationDeliveryProviders == null || notificationDeliveryProviders.isEmpty()) {
            return null;
        }

        for (NotificationDeliveryProvider provider : notificationDeliveryProviders) {
            if (provider.supports(channel)) {
                return provider;
            }
        }

        return null;
    }

    private NotificationDeliveryAttemptStatus resolveAttemptStatus(
            NotificationDeliveryProviderResult providerResult
    ) {
        if (providerResult == null || providerResult.getAttemptStatus() == null) {
            return NotificationDeliveryAttemptStatus.FAILED;
        }

        return providerResult.getAttemptStatus();
    }

    private String resolveResultMessage(NotificationDeliveryProviderResult providerResult) {
        if (providerResult == null || providerResult.getResultMessage() == null) {
            return "Telegram delivery provider returned no result message.";
        }

        return providerResult.getResultMessage();
    }

    private String resolveTechnicalDetail(NotificationDeliveryProviderResult providerResult) {
        if (providerResult == null || providerResult.getTechnicalDetail() == null) {
            return "Telegram delivery provider returned no technical detail.";
        }

        return providerResult.getTechnicalDetail();
    }

    private NotificationDeliveryAttempt recordAttempt(
            UUID notificationEventId,
            NotificationDeliveryChannel channel,
            NotificationDeliveryAttemptStatus status,
            String resultMessage,
            String technicalDetail
    ) {
        OffsetDateTime now = OffsetDateTime.now();

        return recordAttempt(
                notificationEventId,
                channel,
                status,
                resultMessage,
                technicalDetail,
                now,
                now
        );
    }

    private NotificationDeliveryAttempt recordAttempt(
            UUID notificationEventId,
            NotificationDeliveryChannel channel,
            NotificationDeliveryAttemptStatus status,
            String resultMessage,
            String technicalDetail,
            OffsetDateTime attemptedAt,
            OffsetDateTime completedAt
    ) {
        NotificationDeliveryAttempt attempt = new NotificationDeliveryAttempt(
                notificationEventId,
                channel,
                status,
                attemptedAt,
                completedAt,
                truncateResultMessage(resultMessage),
                technicalDetail
        );

        return notificationDeliveryAttemptRepository.save(attempt);
    }

    private NotificationEvent findExistingNotificationEvent(UUID notificationEventId) {
        if (notificationEventId == null) {
            throw new IllegalArgumentException("Notification event id is required.");
        }

        return notificationEventRepository.findById(notificationEventId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Notification event not found: " + notificationEventId
                ));
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

    private String safeMessage(String value) {
        if (value == null || value.isBlank()) {
            return "No technical message was provided.";
        }

        return value;
    }

    private String formatChannel(NotificationDeliveryChannel channel) {
        return channel.name()
                .toLowerCase()
                .replace("_", " ");
    }
}