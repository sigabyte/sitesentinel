package com.cigabyte.sitesentinel.notification.delivery;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.net.http.HttpTimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class TelegramNotificationDeliveryProviderExceptionClassificationTests {

    @Test
    void checkConnectivityReturnsTimeoutForHttpTimeoutException() {
        TelegramNotificationDeliveryProvider provider =
                providerThrowing(
                        new HttpTimeoutException(
                                "Simulated timeout."
                        )
                );

        TelegramConnectivityResult result =
                provider.checkConnectivity();

        assertConnectivityFailure(
                result,
                TelegramConnectivityStatus.TIMEOUT
        );
    }

    @Test
    void checkConnectivityReturnsUnreachableForConnectException() {
        TelegramNotificationDeliveryProvider provider =
                providerThrowing(
                        new ConnectException(
                                "Simulated connection failure."
                        )
                );

        TelegramConnectivityResult result =
                provider.checkConnectivity();

        assertConnectivityFailure(
                result,
                TelegramConnectivityStatus.UNREACHABLE
        );
    }

    @Test
    void checkConnectivityReturnsUnreachableForUnknownHostException() {
        TelegramNotificationDeliveryProvider provider =
                providerThrowing(
                        new UnknownHostException(
                                "Simulated unknown host."
                        )
                );

        TelegramConnectivityResult result =
                provider.checkConnectivity();

        assertConnectivityFailure(
                result,
                TelegramConnectivityStatus.UNREACHABLE
        );
    }

    @Test
    void checkConnectivityReturnsUnreachableForNoRouteToHostException() {
        TelegramNotificationDeliveryProvider provider =
                providerThrowing(
                        new NoRouteToHostException(
                                "Simulated unavailable route."
                        )
                );

        TelegramConnectivityResult result =
                provider.checkConnectivity();

        assertConnectivityFailure(
                result,
                TelegramConnectivityStatus.UNREACHABLE
        );
    }

    @Test
    void checkConnectivityReturnsInterruptedForInterruptedException() {
        TelegramNotificationDeliveryProvider provider =
                providerThrowing(
                        new InterruptedException(
                                "Simulated interruption."
                        )
                );

        TelegramConnectivityResult result =
                provider.checkConnectivity();

        assertConnectivityFailure(
                result,
                TelegramConnectivityStatus.INTERRUPTED
        );
    }

    @Test
    void checkConnectivityReturnsFailedForUnclassifiedException() {
        TelegramNotificationDeliveryProvider provider =
                providerThrowing(
                        new IOException(
                                "Simulated unclassified I/O failure."
                        )
                );

        TelegramConnectivityResult result =
                provider.checkConnectivity();

        assertConnectivityFailure(
                result,
                TelegramConnectivityStatus.FAILED
        );
    }

    private TelegramNotificationDeliveryProvider providerThrowing(
            Throwable cause
    ) {
        TelegramDeliveryProperties properties =
                readyTelegramProperties();

        ThrowingTelegramBotApiClient client =
                new ThrowingTelegramBotApiClient(cause);

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
        properties.setApiBaseUrl("https://api.telegram.org");

        return properties;
    }

    private void assertConnectivityFailure(
            TelegramConnectivityResult result,
            TelegramConnectivityStatus expectedStatus
    ) {
        assertEquals(
                expectedStatus,
                result.getStatus()
        );

        assertFalse(result.isHealthy());
        assertNull(result.getHttpStatusCode());
    }

    private static final class ThrowingTelegramBotApiClient
            implements TelegramBotApiClient {

        private final Throwable cause;

        private ThrowingTelegramBotApiClient(Throwable cause) {
            this.cause = cause;
        }

        @Override
        public TelegramBotApiResponse getMe() {
            throw new TelegramBotApiClientException(
                    "Safe simulated Telegram Bot API failure.",
                    cause
            );
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
    }
}