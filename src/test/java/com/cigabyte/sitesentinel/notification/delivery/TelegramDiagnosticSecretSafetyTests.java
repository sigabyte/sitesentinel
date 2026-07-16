package com.cigabyte.sitesentinel.notification.delivery;

import com.cigabyte.sitesentinel.notification.NotificationEvent;
import com.cigabyte.sitesentinel.notification.NotificationEventSeverity;
import com.cigabyte.sitesentinel.notification.NotificationEventType;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TelegramDiagnosticSecretSafetyTests {

    private static final String BOT_TOKEN =
            "123456789:SUPER_SECRET_TEST_TOKEN";

    private static final String CHAT_ID =
            "-1009988776655";

    private static final String API_BASE_URL =
            "https://telegram-proxy.example.internal/private";

    private static final String RAW_PAYLOAD_MARKER =
            "RAW_PROVIDER_PAYLOAD_MUST_NOT_BE_PERSISTED";

    @Test
    void connectivityResponseDiagnosticDoesNotExposeSecretsOrRawBody() {
        StubTelegramBotApiClient client =
                new StubTelegramBotApiClient();

        client.getMeResponse =
                new TelegramBotApiResponse(
                        500,
                        secretContainingResponseBody()
                );

        TelegramNotificationDeliveryProvider provider =
                provider(client);

        TelegramConnectivityResult result =
                provider.checkConnectivity();

        assertEquals(
                TelegramConnectivityStatus.FAILED,
                result.getStatus()
        );

        assertEquals(500, result.getHttpStatusCode());

        assertSecretSafe(
                result.getDiagnosticMessage()
        );
    }

    @Test
    void connectivityExceptionDiagnosticDoesNotExposeExceptionMessage() {
        StubTelegramBotApiClient client =
                new StubTelegramBotApiClient();

        client.getMeFailure =
                new TelegramBotApiClientException(
                        secretContainingExceptionMessage(),
                        new IllegalStateException(
                                "Simulated connectivity failure."
                        )
                );

        TelegramNotificationDeliveryProvider provider =
                provider(client);

        TelegramConnectivityResult result =
                provider.checkConnectivity();

        assertEquals(
                TelegramConnectivityStatus.FAILED,
                result.getStatus()
        );

        assertNull(result.getHttpStatusCode());

        assertSecretSafe(
                result.getDiagnosticMessage()
        );
    }

    @Test
    void deliveryHttpFailureDoesNotPersistRawResponseBody() {
        StubTelegramBotApiClient client =
                new StubTelegramBotApiClient();

        client.sendMessageResponse =
                new TelegramBotApiResponse(
                        400,
                        secretContainingResponseBody()
                );

        TelegramNotificationDeliveryProvider provider =
                provider(client);

        NotificationDeliveryProviderResult result =
                provider.deliver(
                        notificationEvent()
                );

        assertTrue(result.isDeliveryAttempted());
        assertFalse(result.isSuccessful());

        assertEquals(
                "Telegram Bot API returned HTTP status=400.",
                result.getTechnicalDetail()
        );

        assertSecretSafe(result.getResultMessage());
        assertSecretSafe(result.getTechnicalDetail());
    }

    @Test
    void deliveryClientFailureDoesNotPersistExceptionMessage() {
        StubTelegramBotApiClient client =
                new StubTelegramBotApiClient();

        client.sendMessageFailure =
                new TelegramBotApiClientException(
                        secretContainingExceptionMessage(),
                        new IllegalStateException(
                                "Simulated delivery failure."
                        )
                );

        TelegramNotificationDeliveryProvider provider =
                provider(client);

        NotificationDeliveryProviderResult result =
                provider.deliver(
                        notificationEvent()
                );

        assertTrue(result.isDeliveryAttempted());
        assertFalse(result.isSuccessful());

        assertEquals(
                "Telegram Bot API client call failed before a valid response was received.",
                result.getTechnicalDetail()
        );

        assertSecretSafe(result.getResultMessage());
        assertSecretSafe(result.getTechnicalDetail());
    }

    private TelegramNotificationDeliveryProvider provider(
            TelegramBotApiClient client
    ) {
        return new TelegramNotificationDeliveryProvider(
                readyTelegramProperties(),
                client
        );
    }

    private TelegramDeliveryProperties readyTelegramProperties() {
        TelegramDeliveryProperties properties =
                new TelegramDeliveryProperties();

        properties.setEnabled(true);
        properties.setBotToken(BOT_TOKEN);
        properties.setChatId(CHAT_ID);
        properties.setApiBaseUrl(API_BASE_URL);

        return properties;
    }

    private NotificationEvent notificationEvent() {
        NotificationEvent event =
                new NotificationEvent(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        NotificationEventType.MONITORING_RUN_FAILED,
                        NotificationEventSeverity.HIGH,
                        "Test notification",
                        "Controlled notification message.",
                        "test-deduplication-key-" + UUID.randomUUID()
                );

        assignNotificationEventId(event);

        return event;
    }

    private void assignNotificationEventId(
            NotificationEvent notificationEvent
    ) {
        try {
            Field idField =
                    NotificationEvent.class.getDeclaredField("id");

            idField.setAccessible(true);
            idField.set(
                    notificationEvent,
                    UUID.randomUUID()
            );

        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(
                    "Could not assign notification event id for the unit test.",
                    exception
            );
        }
    }

    private String secretContainingResponseBody() {
        return """
                {
                  "ok": false,
                  "description": "%s",
                  "bot_token": "%s",
                  "chat_id": "%s",
                  "endpoint": "%s"
                }
                """.formatted(
                RAW_PAYLOAD_MARKER,
                BOT_TOKEN,
                CHAT_ID,
                API_BASE_URL
        );
    }

    private String secretContainingExceptionMessage() {
        return "Failure calling "
                + API_BASE_URL
                + "/bot"
                + BOT_TOKEN
                + "/sendMessage"
                + "?chat_id="
                + CHAT_ID
                + " "
                + RAW_PAYLOAD_MARKER;
    }

    private void assertSecretSafe(String value) {
        assertFalse(
                value.contains(BOT_TOKEN),
                "Diagnostic must not contain the bot token."
        );

        assertFalse(
                value.contains(CHAT_ID),
                "Diagnostic must not contain the chat ID."
        );

        assertFalse(
                value.contains(API_BASE_URL),
                "Diagnostic must not contain the Telegram API endpoint."
        );

        assertFalse(
                value.contains(RAW_PAYLOAD_MARKER),
                "Diagnostic must not contain the raw provider response."
        );
    }

    private static final class StubTelegramBotApiClient
            implements TelegramBotApiClient {

        private TelegramBotApiResponse getMeResponse;

        private TelegramBotApiResponse sendMessageResponse;

        private TelegramBotApiClientException getMeFailure;

        private TelegramBotApiClientException sendMessageFailure;

        @Override
        public TelegramBotApiResponse getMe() {
            if (getMeFailure != null) {
                throw getMeFailure;
            }

            if (getMeResponse == null) {
                throw new AssertionError(
                        "No getMe response was configured for the test."
                );
            }

            return getMeResponse;
        }

        @Override
        public TelegramBotApiResponse sendMessage(
                String chatId,
                String message
        ) {
            if (sendMessageFailure != null) {
                throw sendMessageFailure;
            }

            if (sendMessageResponse == null) {
                throw new AssertionError(
                        "No sendMessage response was configured for the test."
                );
            }

            return sendMessageResponse;
        }
    }
}