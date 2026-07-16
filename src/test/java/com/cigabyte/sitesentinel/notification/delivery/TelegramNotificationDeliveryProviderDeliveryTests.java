package com.cigabyte.sitesentinel.notification.delivery;

import com.cigabyte.sitesentinel.notification.NotificationDeliveryAttemptStatus;
import com.cigabyte.sitesentinel.notification.NotificationDeliveryChannel;
import com.cigabyte.sitesentinel.notification.NotificationEvent;
import com.cigabyte.sitesentinel.notification.NotificationEventSeverity;
import com.cigabyte.sitesentinel.notification.NotificationEventType;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TelegramNotificationDeliveryProviderDeliveryTests {

    private static final UUID WEBSITE_ID =
            UUID.fromString(
                    "11111111-1111-1111-1111-111111111111"
            );

    private static final UUID MONITORING_RUN_ID =
            UUID.fromString(
                    "22222222-2222-2222-2222-222222222222"
            );

    private static final UUID NOTIFICATION_EVENT_ID =
            UUID.fromString(
                    "33333333-3333-3333-3333-333333333333"
            );

    @Test
    void getChannelReturnsTelegram() {
        RecordingTelegramBotApiClient client =
                new RecordingTelegramBotApiClient();

        TelegramNotificationDeliveryProvider provider =
                provider(
                        readyTelegramProperties(),
                        client
                );

        assertEquals(
                NotificationDeliveryChannel.TELEGRAM,
                provider.getChannel()
        );
    }

    @Test
    void deliverRejectsNullNotificationEvent() {
        RecordingTelegramBotApiClient client =
                new RecordingTelegramBotApiClient();

        TelegramNotificationDeliveryProvider provider =
                provider(
                        readyTelegramProperties(),
                        client
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> provider.deliver(null)
                );

        assertEquals(
                "Notification event is required for delivery.",
                exception.getMessage()
        );

        assertEquals(0, client.getSendMessageCallCount());
    }

    @Test
    void deliverRejectsNotificationEventWithoutId() {
        RecordingTelegramBotApiClient client =
                new RecordingTelegramBotApiClient();

        TelegramNotificationDeliveryProvider provider =
                provider(
                        readyTelegramProperties(),
                        client
                );

        NotificationEvent notificationEvent =
                notificationEventWithoutId();

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> provider.deliver(notificationEvent)
                );

        assertEquals(
                "Notification event id is required for delivery.",
                exception.getMessage()
        );

        assertEquals(0, client.getSendMessageCallCount());
    }

    @Test
    void deliverReturnsDisabledWithoutCallingClient() {
        TelegramDeliveryProperties properties =
                readyTelegramProperties();

        properties.setEnabled(false);

        RecordingTelegramBotApiClient client =
                new RecordingTelegramBotApiClient();

        TelegramNotificationDeliveryProvider provider =
                provider(
                        properties,
                        client
                );

        NotificationDeliveryProviderResult result =
                provider.deliver(
                        notificationEvent()
                );

        assertEquals(
                NotificationDeliveryAttemptStatus.DISABLED,
                result.getAttemptStatus()
        );

        assertFalse(result.isDeliveryAttempted());
        assertFalse(result.isSuccessful());

        assertEquals(
                "Telegram delivery is disabled.",
                result.getResultMessage()
        );

        assertEquals(0, client.getSendMessageCallCount());
    }

    @Test
    void deliverReturnsConfigurationMissingWithoutCallingClient() {
        TelegramDeliveryProperties properties =
                readyTelegramProperties();

        properties.setChatId("");

        RecordingTelegramBotApiClient client =
                new RecordingTelegramBotApiClient();

        TelegramNotificationDeliveryProvider provider =
                provider(
                        properties,
                        client
                );

        NotificationDeliveryProviderResult result =
                provider.deliver(
                        notificationEvent()
                );

        assertEquals(
                NotificationDeliveryAttemptStatus.CONFIGURATION_MISSING,
                result.getAttemptStatus()
        );

        assertFalse(result.isDeliveryAttempted());
        assertFalse(result.isSuccessful());

        assertEquals(
                "Telegram delivery configuration is missing.",
                result.getResultMessage()
        );

        assertEquals(0, client.getSendMessageCallCount());
    }

    @Test
    void deliverReturnsSentAndBuildsExpectedTelegramMessage() {
        RecordingTelegramBotApiClient client =
                new RecordingTelegramBotApiClient();

        client.setSendMessageResponse(
                new TelegramBotApiResponse(
                        200,
                        """
                        {
                          "ok": true,
                          "result": {
                            "message_id": 9876
                          }
                        }
                        """
                )
        );

        TelegramNotificationDeliveryProvider provider =
                provider(
                        readyTelegramProperties(),
                        client
                );

        NotificationDeliveryProviderResult result =
                provider.deliver(
                        notificationEvent()
                );

        assertEquals(
                NotificationDeliveryAttemptStatus.SENT,
                result.getAttemptStatus()
        );

        assertTrue(result.isDeliveryAttempted());
        assertTrue(result.isSuccessful());

        assertEquals(
                "Telegram message was sent successfully.",
                result.getResultMessage()
        );

        assertEquals(
                "Telegram Bot API accepted the sendMessage request. HTTP status=200.",
                result.getTechnicalDetail()
        );

        assertEquals(1, client.getSendMessageCallCount());

        assertEquals(
                "test-chat-id",
                client.getRecordedChatId()
        );

        String recordedMessage =
                client.getRecordedMessage();

        assertTrue(
                recordedMessage.contains(
                        "SiteSentinel Notification"
                )
        );

        assertTrue(
                recordedMessage.contains(
                        "Severity: HIGH"
                )
        );

        assertTrue(
                recordedMessage.contains(
                        "Event Type: MONITORING_RUN_FAILED"
                )
        );

        assertTrue(
                recordedMessage.contains(
                        "Controlled monitoring failure"
                )
        );

        assertTrue(
                recordedMessage.contains(
                        "The controlled monitoring run failed."
                )
        );

        assertTrue(
                recordedMessage.contains(
                        "Website ID: " + WEBSITE_ID
                )
        );

        assertTrue(
                recordedMessage.contains(
                        "Monitoring Run ID: "
                                + MONITORING_RUN_ID
                )
        );

        assertTrue(
                recordedMessage.contains(
                        "Notification Event ID: "
                                + NOTIFICATION_EVENT_ID
                )
        );
    }

    @Test
    void deliverReturnsFailedWhenHttpStatusIsSuccessfulButTelegramOkIsFalse() {
        RecordingTelegramBotApiClient client =
                new RecordingTelegramBotApiClient();

        client.setSendMessageResponse(
                new TelegramBotApiResponse(
                        200,
                        """
                        {
                          "ok": false,
                          "description": "Controlled rejection"
                        }
                        """
                )
        );

        TelegramNotificationDeliveryProvider provider =
                provider(
                        readyTelegramProperties(),
                        client
                );

        NotificationDeliveryProviderResult result =
                provider.deliver(
                        notificationEvent()
                );

        assertEquals(
                NotificationDeliveryAttemptStatus.FAILED,
                result.getAttemptStatus()
        );

        assertTrue(result.isDeliveryAttempted());
        assertFalse(result.isSuccessful());

        assertEquals(
                "Telegram message delivery failed.",
                result.getResultMessage()
        );

        assertEquals(
                "Telegram Bot API returned HTTP status=200.",
                result.getTechnicalDetail()
        );

        assertEquals(1, client.getSendMessageCallCount());
    }

    @Test
    void deliverReturnsFailedForUnsuccessfulHttpResponse() {
        RecordingTelegramBotApiClient client =
                new RecordingTelegramBotApiClient();

        client.setSendMessageResponse(
                new TelegramBotApiResponse(
                        429,
                        """
                        {
                          "ok": false,
                          "description": "Too Many Requests"
                        }
                        """
                )
        );

        TelegramNotificationDeliveryProvider provider =
                provider(
                        readyTelegramProperties(),
                        client
                );

        NotificationDeliveryProviderResult result =
                provider.deliver(
                        notificationEvent()
                );

        assertEquals(
                NotificationDeliveryAttemptStatus.FAILED,
                result.getAttemptStatus()
        );

        assertTrue(result.isDeliveryAttempted());
        assertFalse(result.isSuccessful());

        assertEquals(
                "Telegram Bot API returned HTTP status=429.",
                result.getTechnicalDetail()
        );

        assertEquals(1, client.getSendMessageCallCount());
    }

    @Test
    void deliverReturnsSecretSafeFailureForClientException() {
        RecordingTelegramBotApiClient client =
                new RecordingTelegramBotApiClient();

        client.setSendMessageFailure(
                new TelegramBotApiClientException(
                        "Sensitive simulated endpoint and credential detail.",
                        new IllegalStateException(
                                "Controlled client failure."
                        )
                )
        );

        TelegramNotificationDeliveryProvider provider =
                provider(
                        readyTelegramProperties(),
                        client
                );

        NotificationDeliveryProviderResult result =
                provider.deliver(
                        notificationEvent()
                );

        assertEquals(
                NotificationDeliveryAttemptStatus.FAILED,
                result.getAttemptStatus()
        );

        assertTrue(result.isDeliveryAttempted());
        assertFalse(result.isSuccessful());

        assertEquals(
                "Telegram message delivery failed while calling Telegram Bot API.",
                result.getResultMessage()
        );

        assertEquals(
                "Telegram Bot API client call failed before a valid response was received.",
                result.getTechnicalDetail()
        );

        assertFalse(
                result.getTechnicalDetail().contains(
                        "Sensitive simulated endpoint"
                )
        );

        assertEquals(1, client.getSendMessageCallCount());
    }

    private TelegramNotificationDeliveryProvider provider(
            TelegramDeliveryProperties properties,
            TelegramBotApiClient client
    ) {
        return new TelegramNotificationDeliveryProvider(
                properties,
                client
        );
    }

    private TelegramDeliveryProperties readyTelegramProperties() {
        TelegramDeliveryProperties properties =
                new TelegramDeliveryProperties();

        properties.setEnabled(true);
        properties.setBotToken("test-bot-token");
        properties.setChatId("test-chat-id");
        properties.setApiBaseUrl(
                "https://api.telegram.org"
        );

        return properties;
    }

    private NotificationEvent notificationEvent() {
        NotificationEvent notificationEvent =
                notificationEventWithoutId();

        assignNotificationEventId(
                notificationEvent,
                NOTIFICATION_EVENT_ID
        );

        return notificationEvent;
    }

    private NotificationEvent notificationEventWithoutId() {
        return new NotificationEvent(
                WEBSITE_ID,
                MONITORING_RUN_ID,
                NotificationEventType.MONITORING_RUN_FAILED,
                NotificationEventSeverity.HIGH,
                "Controlled monitoring failure",
                "The controlled monitoring run failed.",
                "delivery-test-deduplication-key"
        );
    }

    private void assignNotificationEventId(
            NotificationEvent notificationEvent,
            UUID notificationEventId
    ) {
        try {
            Field idField =
                    NotificationEvent.class.getDeclaredField(
                            "id"
                    );

            idField.setAccessible(true);

            idField.set(
                    notificationEvent,
                    notificationEventId
            );

        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(
                    "Could not assign notification event id for the unit test.",
                    exception
            );
        }
    }

    private static final class RecordingTelegramBotApiClient
            implements TelegramBotApiClient {

        private TelegramBotApiResponse sendMessageResponse;

        private TelegramBotApiClientException sendMessageFailure;

        private String recordedChatId;

        private String recordedMessage;

        private int sendMessageCallCount;

        @Override
        public TelegramBotApiResponse getMe() {
            throw new AssertionError(
                    "Message delivery must not call getMe()."
            );
        }

        @Override
        public TelegramBotApiResponse sendMessage(
                String chatId,
                String message
        ) {
            sendMessageCallCount++;
            recordedChatId = chatId;
            recordedMessage = message;

            if (sendMessageFailure != null) {
                throw sendMessageFailure;
            }

            if (sendMessageResponse == null) {
                throw new AssertionError(
                        "No sendMessage response was configured."
                );
            }

            return sendMessageResponse;
        }

        private void setSendMessageResponse(
                TelegramBotApiResponse sendMessageResponse
        ) {
            this.sendMessageResponse =
                    sendMessageResponse;
        }

        private void setSendMessageFailure(
                TelegramBotApiClientException sendMessageFailure
        ) {
            this.sendMessageFailure =
                    sendMessageFailure;
        }

        private String getRecordedChatId() {
            return recordedChatId;
        }

        private String getRecordedMessage() {
            return recordedMessage;
        }

        private int getSendMessageCallCount() {
            return sendMessageCallCount;
        }
    }
}