package com.cigabyte.sitesentinel.notification.delivery;

import com.cigabyte.sitesentinel.notification.NotificationEvent;
import com.cigabyte.sitesentinel.notification.NotificationEventSeverity;
import com.cigabyte.sitesentinel.notification.NotificationEventType;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TelegramNotificationDeliveryProviderMessageBoundaryTests {

    private static final int EXPECTED_MESSAGE_MAX_LENGTH =
            3900;

    private static final String TRUNCATION_SUFFIX =
            "...";

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
    void messageExactlyAtBoundaryIsNotTruncated() {
        String probeMessage =
                deliverAndCapture("");

        int requiredEventMessageLength =
                EXPECTED_MESSAGE_MAX_LENGTH
                        - probeMessage.length();

        assertTrue(
                requiredEventMessageLength > 0,
                "The fixed Telegram message fields must leave room "
                        + "for the event message."
        );

        String recordedMessage =
                deliverAndCapture(
                        "A".repeat(
                                requiredEventMessageLength
                        )
                );

        assertEquals(
                EXPECTED_MESSAGE_MAX_LENGTH,
                recordedMessage.length()
        );

        assertFalse(
                recordedMessage.endsWith(
                        TRUNCATION_SUFFIX
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
    void oversizedMessageIncludesSuffixWithinMaximumLength() {
        String probeMessage =
                deliverAndCapture("");

        int oversizedEventMessageLength =
                EXPECTED_MESSAGE_MAX_LENGTH
                        - probeMessage.length()
                        + 500;

        String recordedMessage =
                deliverAndCapture(
                        "B".repeat(
                                oversizedEventMessageLength
                        )
                );

        assertEquals(
                EXPECTED_MESSAGE_MAX_LENGTH,
                recordedMessage.length()
        );

        assertTrue(
                recordedMessage.endsWith(
                        TRUNCATION_SUFFIX
                )
        );

        assertFalse(
                recordedMessage.contains(
                        "Notification Event ID: "
                                + NOTIFICATION_EVENT_ID
                ),
                "Metadata after the truncation point should not "
                        + "appear in the shortened message."
        );
    }

    @Test
    void truncationDoesNotSplitUnicodeSurrogatePair() {
        String marker =
                "MESSAGE_BOUNDARY_MARKER";

        String probeMessage =
                deliverAndCapture(marker);

        int messageStartIndex =
                probeMessage.indexOf(marker);

        assertTrue(
                messageStartIndex >= 0,
                "The probe marker must be present in the generated message."
        );

        int contentBoundaryIndex =
                EXPECTED_MESSAGE_MAX_LENGTH
                        - TRUNCATION_SUFFIX.length();

        int highSurrogateTargetIndex =
                contentBoundaryIndex - 1;

        int paddingLength =
                highSurrogateTargetIndex
                        - messageStartIndex;

        assertTrue(
                paddingLength >= 0,
                "The generated message prefix is unexpectedly long."
        );

        String eventMessage =
                "C".repeat(paddingLength)
                        + "😀"
                        + "D".repeat(500);

        String recordedMessage =
                deliverAndCapture(eventMessage);

        assertTrue(
                recordedMessage.length()
                        <= EXPECTED_MESSAGE_MAX_LENGTH
        );

        assertTrue(
                recordedMessage.endsWith(
                        TRUNCATION_SUFFIX
                )
        );

        assertFalse(
                containsUnpairedSurrogate(recordedMessage),
                "The truncated message must not contain "
                        + "an unpaired Unicode surrogate."
        );
    }

    private String deliverAndCapture(
            String eventMessage
    ) {
        RecordingTelegramBotApiClient client =
                new RecordingTelegramBotApiClient();

        TelegramNotificationDeliveryProvider provider =
                new TelegramNotificationDeliveryProvider(
                        readyTelegramProperties(),
                        client
                );

        NotificationDeliveryProviderResult result =
                provider.deliver(
                        notificationEvent(eventMessage)
                );

        assertTrue(result.isDeliveryAttempted());
        assertTrue(result.isSuccessful());

        assertEquals(
                1,
                client.getSendMessageCallCount()
        );

        String recordedMessage =
                client.getRecordedMessage();

        assertNotNull(recordedMessage);

        return recordedMessage;
    }

    private NotificationEvent notificationEvent(
            String eventMessage
    ) {
        NotificationEvent notificationEvent =
                new NotificationEvent(
                        WEBSITE_ID,
                        MONITORING_RUN_ID,
                        NotificationEventType.MONITORING_RUN_FAILED,
                        NotificationEventSeverity.HIGH,
                        "Telegram message boundary test",
                        eventMessage,
                        "message-boundary-test-"
                                + UUID.randomUUID()
                );

        assignNotificationEventId(
                notificationEvent
        );

        return notificationEvent;
    }

    private void assignNotificationEventId(
            NotificationEvent notificationEvent
    ) {
        try {
            Field idField =
                    NotificationEvent.class.getDeclaredField(
                            "id"
                    );

            idField.setAccessible(true);

            idField.set(
                    notificationEvent,
                    NOTIFICATION_EVENT_ID
            );

        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(
                    "Could not assign notification event id "
                            + "for the unit test.",
                    exception
            );
        }
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

    private boolean containsUnpairedSurrogate(
            String value
    ) {
        for (int index = 0; index < value.length(); index++) {
            char currentCharacter =
                    value.charAt(index);

            if (Character.isHighSurrogate(
                    currentCharacter
            )) {
                if (index + 1 >= value.length()
                        || !Character.isLowSurrogate(
                        value.charAt(index + 1)
                )) {

                    return true;
                }

                index++;

            } else if (Character.isLowSurrogate(
                    currentCharacter
            )) {
                return true;
            }
        }

        return false;
    }

    private static final class RecordingTelegramBotApiClient
            implements TelegramBotApiClient {

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
            recordedMessage = message;

            return new TelegramBotApiResponse(
                    200,
                    """
                    {
                      "ok": true,
                      "result": {
                        "message_id": 9876
                      }
                    }
                    """
            );
        }

        private String getRecordedMessage() {
            return recordedMessage;
        }

        private int getSendMessageCallCount() {
            return sendMessageCallCount;
        }
    }
}