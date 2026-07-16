package com.cigabyte.sitesentinel.notification.delivery;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TelegramNotificationDeliveryProviderConnectivityTests {

    @Test
    void checkConnectivityDoesNotCallClientWhenProviderIsDisabled() {
        TelegramDeliveryProperties properties =
                readyTelegramProperties();

        properties.setEnabled(false);

        RecordingTelegramBotApiClient client =
                RecordingTelegramBotApiClient.returning(
                        successfulTelegramResponse()
                );

        TelegramNotificationDeliveryProvider provider =
                new TelegramNotificationDeliveryProvider(
                        properties,
                        client
                );

        TelegramConnectivityResult result =
                provider.checkConnectivity();

        assertEquals(
                TelegramConnectivityStatus.FAILED,
                result.getStatus()
        );

        assertFalse(result.isHealthy());
        assertNull(result.getHttpStatusCode());
        assertEquals(0, client.getMeCallCount());
    }

    @Test
    void checkConnectivityDoesNotCallClientWhenConfigurationIsIncomplete() {
        TelegramDeliveryProperties properties =
                readyTelegramProperties();

        properties.setBotToken("");

        RecordingTelegramBotApiClient client =
                RecordingTelegramBotApiClient.returning(
                        successfulTelegramResponse()
                );

        TelegramNotificationDeliveryProvider provider =
                new TelegramNotificationDeliveryProvider(
                        properties,
                        client
                );

        TelegramConnectivityResult result =
                provider.checkConnectivity();

        assertEquals(
                TelegramConnectivityStatus.FAILED,
                result.getStatus()
        );

        assertFalse(result.isHealthy());
        assertNull(result.getHttpStatusCode());
        assertEquals(0, client.getMeCallCount());
    }

    @Test
    void checkConnectivityReturnsHealthyForSuccessfulTelegramResponse() {
        TelegramDeliveryProperties properties =
                readyTelegramProperties();

        RecordingTelegramBotApiClient client =
                RecordingTelegramBotApiClient.returning(
                        successfulTelegramResponse()
                );

        TelegramNotificationDeliveryProvider provider =
                new TelegramNotificationDeliveryProvider(
                        properties,
                        client
                );

        TelegramConnectivityResult result =
                provider.checkConnectivity();

        assertEquals(
                TelegramConnectivityStatus.HEALTHY,
                result.getStatus()
        );

        assertTrue(result.isHealthy());
        assertEquals(200, result.getHttpStatusCode());
        assertEquals(1, client.getMeCallCount());
    }

    @Test
    void checkConnectivityReturnsAuthenticationFailedForUnauthorizedResponse() {
        TelegramDeliveryProperties properties =
                readyTelegramProperties();

        TelegramBotApiResponse unauthorizedResponse =
                new TelegramBotApiResponse(
                        401,
                        """
                        {
                          "ok": false,
                          "error_code": 401,
                          "description": "Unauthorized"
                        }
                        """
                );

        RecordingTelegramBotApiClient client =
                RecordingTelegramBotApiClient.returning(
                        unauthorizedResponse
                );

        TelegramNotificationDeliveryProvider provider =
                new TelegramNotificationDeliveryProvider(
                        properties,
                        client
                );

        TelegramConnectivityResult result =
                provider.checkConnectivity();

        assertEquals(
                TelegramConnectivityStatus.AUTHENTICATION_FAILED,
                result.getStatus()
        );

        assertFalse(result.isHealthy());
        assertEquals(401, result.getHttpStatusCode());
        assertEquals(1, client.getMeCallCount());
    }

    @Test
    void checkConnectivityReturnsInvalidResponseForUnexpectedSuccessfulBody() {
        TelegramDeliveryProperties properties =
                readyTelegramProperties();

        TelegramBotApiResponse invalidResponse =
                new TelegramBotApiResponse(
                        200,
                        """
                        {
                          "result": {
                            "id": 12345
                          }
                        }
                        """
                );

        RecordingTelegramBotApiClient client =
                RecordingTelegramBotApiClient.returning(
                        invalidResponse
                );

        TelegramNotificationDeliveryProvider provider =
                new TelegramNotificationDeliveryProvider(
                        properties,
                        client
                );

        TelegramConnectivityResult result =
                provider.checkConnectivity();

        assertEquals(
                TelegramConnectivityStatus.INVALID_RESPONSE,
                result.getStatus()
        );

        assertFalse(result.isHealthy());
        assertEquals(200, result.getHttpStatusCode());
        assertEquals(1, client.getMeCallCount());
    }

    private TelegramDeliveryProperties readyTelegramProperties() {
        TelegramDeliveryProperties properties =
                new TelegramDeliveryProperties();

        properties.setEnabled(true);
        properties.setBotToken("test-bot-token");
        properties.setChatId("test-chat-id");
        properties.setApiBaseUrl("https://api.telegram.org");

        return properties;
    }

    private TelegramBotApiResponse successfulTelegramResponse() {
        return new TelegramBotApiResponse(
                200,
                """
                {
                  "ok":true,
                  "result": {
                    "id": 12345,
                    "is_bot": true,
                    "username": "test_bot"
                  }
                }
                """
        );
    }

    private static final class RecordingTelegramBotApiClient
            implements TelegramBotApiClient {

        private final TelegramBotApiResponse getMeResponse;

        private int getMeCallCount;

        private RecordingTelegramBotApiClient(
                TelegramBotApiResponse getMeResponse
        ) {
            this.getMeResponse = getMeResponse;
        }

        private static RecordingTelegramBotApiClient returning(
                TelegramBotApiResponse response
        ) {
            return new RecordingTelegramBotApiClient(response);
        }

        @Override
        public TelegramBotApiResponse getMe() {
            getMeCallCount++;

            return getMeResponse;
        }

        @Override
        public TelegramBotApiResponse sendMessage(
                String chatId,
                String message
        ) {
            throw new AssertionError(
                    "Connectivity checks must not send Telegram messages."
            );
        }

        private int getMeCallCount() {
            return getMeCallCount;
        }
    }
}