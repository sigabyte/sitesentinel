package com.cigabyte.sitesentinel.notification.delivery;

import com.cigabyte.sitesentinel.notification.NotificationDeliveryChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TelegramProviderHealthCheckServiceTests {

    @Test
    void performHealthCheckRecordsDisabledWithoutCallingProvider() {
        TelegramDeliveryProperties properties =
                readyTelegramProperties();

        properties.setEnabled(false);

        RecordingProviderCheckService checkService =
                new RecordingProviderCheckService();

        FixedConnectivityProvider provider =
                new FixedConnectivityProvider(
                        properties,
                        connectivityResult(
                                TelegramConnectivityStatus.HEALTHY,
                                200
                        )
                );

        TelegramProviderHealthCheckService service =
                healthCheckService(
                        properties,
                        checkService,
                        provider
                );

        TelegramProviderHealthCheckResult result =
                service.performHealthCheck();

        assertEquals(
                NotificationDeliveryProviderCheckStatus.DISABLED,
                result.getStatus()
        );

        assertEquals(
                "Telegram delivery provider is disabled.",
                result.getMessage()
        );

        assertNull(result.getHttpStatusCode());

        assertEquals(
                0,
                provider.getCheckConnectivityCallCount()
        );

        assertEquals(1, checkService.getRecordCallCount());

        assertEquals(
                NotificationDeliveryChannel.TELEGRAM,
                checkService.getRecordedChannel()
        );

        assertEquals(
                NotificationDeliveryProviderCheckStatus.DISABLED,
                checkService.getRecordedStatus()
        );

        assertNull(checkService.getRecordedHttpStatusCode());

        assertTrue(
                checkService.wasThreeParameterRecordUsed()
        );

        assertFalse(
                checkService.wasFourParameterRecordUsed()
        );
    }

    @Test
    void performHealthCheckRecordsConfigurationMissingWithoutCallingProvider() {
        TelegramDeliveryProperties properties =
                readyTelegramProperties();

        properties.setBotToken("");

        RecordingProviderCheckService checkService =
                new RecordingProviderCheckService();

        FixedConnectivityProvider provider =
                new FixedConnectivityProvider(
                        properties,
                        connectivityResult(
                                TelegramConnectivityStatus.HEALTHY,
                                200
                        )
                );

        TelegramProviderHealthCheckService service =
                healthCheckService(
                        properties,
                        checkService,
                        provider
                );

        TelegramProviderHealthCheckResult result =
                service.performHealthCheck();

        assertEquals(
                NotificationDeliveryProviderCheckStatus.CONFIGURATION_MISSING,
                result.getStatus()
        );

        assertEquals(
                "Telegram delivery provider configuration is incomplete.",
                result.getMessage()
        );

        assertNull(result.getHttpStatusCode());

        assertEquals(
                0,
                provider.getCheckConnectivityCallCount()
        );

        assertEquals(1, checkService.getRecordCallCount());

        assertEquals(
                NotificationDeliveryProviderCheckStatus.CONFIGURATION_MISSING,
                checkService.getRecordedStatus()
        );

        assertNull(checkService.getRecordedHttpStatusCode());

        assertTrue(
                checkService.wasThreeParameterRecordUsed()
        );

        assertFalse(
                checkService.wasFourParameterRecordUsed()
        );
    }

    @Test
    void performHealthCheckMapsAndRecordsEveryConnectivityStatus() {
        assertTypedMapping(
                TelegramConnectivityStatus.HEALTHY,
                NotificationDeliveryProviderCheckStatus.HEALTHY,
                200
        );

        assertTypedMapping(
                TelegramConnectivityStatus.AUTHENTICATION_FAILED,
                NotificationDeliveryProviderCheckStatus.AUTHENTICATION_FAILED,
                401
        );

        assertTypedMapping(
                TelegramConnectivityStatus.TIMEOUT,
                NotificationDeliveryProviderCheckStatus.TIMEOUT,
                null
        );

        assertTypedMapping(
                TelegramConnectivityStatus.UNREACHABLE,
                NotificationDeliveryProviderCheckStatus.UNREACHABLE,
                null
        );

        assertTypedMapping(
                TelegramConnectivityStatus.INVALID_RESPONSE,
                NotificationDeliveryProviderCheckStatus.INVALID_RESPONSE,
                200
        );

        assertTypedMapping(
                TelegramConnectivityStatus.INTERRUPTED,
                NotificationDeliveryProviderCheckStatus.INTERRUPTED,
                null
        );

        assertTypedMapping(
                TelegramConnectivityStatus.FAILED,
                NotificationDeliveryProviderCheckStatus.FAILED,
                503
        );
    }

    private void assertTypedMapping(
            TelegramConnectivityStatus connectivityStatus,
            NotificationDeliveryProviderCheckStatus expectedCheckStatus,
            Integer httpStatusCode
    ) {
        TelegramDeliveryProperties properties =
                readyTelegramProperties();

        RecordingProviderCheckService checkService =
                new RecordingProviderCheckService();

        TelegramConnectivityResult connectivityResult =
                connectivityResult(
                        connectivityStatus,
                        httpStatusCode
                );

        FixedConnectivityProvider provider =
                new FixedConnectivityProvider(
                        properties,
                        connectivityResult
                );

        TelegramProviderHealthCheckService service =
                healthCheckService(
                        properties,
                        checkService,
                        provider
                );

        TelegramProviderHealthCheckResult result =
                service.performHealthCheck();

        assertEquals(
                expectedCheckStatus,
                result.getStatus()
        );

        assertEquals(
                connectivityResult.getDiagnosticMessage(),
                result.getMessage()
        );

        assertEquals(
                httpStatusCode,
                result.getHttpStatusCode()
        );

        assertEquals(
                1,
                provider.getCheckConnectivityCallCount()
        );

        assertEquals(
                1,
                checkService.getRecordCallCount()
        );

        assertEquals(
                NotificationDeliveryChannel.TELEGRAM,
                checkService.getRecordedChannel()
        );

        assertEquals(
                expectedCheckStatus,
                checkService.getRecordedStatus()
        );

        assertEquals(
                connectivityResult.getDiagnosticMessage(),
                checkService.getRecordedMessage()
        );

        assertEquals(
                httpStatusCode,
                checkService.getRecordedHttpStatusCode()
        );

        assertFalse(
                checkService.wasThreeParameterRecordUsed()
        );

        assertTrue(
                checkService.wasFourParameterRecordUsed()
        );
    }

    private TelegramProviderHealthCheckService healthCheckService(
            TelegramDeliveryProperties properties,
            RecordingProviderCheckService checkService,
            FixedConnectivityProvider provider
    ) {
        return new TelegramProviderHealthCheckService(
                new TelegramDeliveryReadinessService(properties),
                checkService,
                provider
        );
    }

    private TelegramConnectivityResult connectivityResult(
            TelegramConnectivityStatus status,
            Integer httpStatusCode
    ) {
        return new TelegramConnectivityResult(
                status,
                "Safe diagnostic for " + status + ".",
                httpStatusCode
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

    private static final class FixedConnectivityProvider
            extends TelegramNotificationDeliveryProvider {

        private final TelegramConnectivityResult connectivityResult;

        private int checkConnectivityCallCount;

        private FixedConnectivityProvider(
                TelegramDeliveryProperties properties,
                TelegramConnectivityResult connectivityResult
        ) {
            super(
                    properties,
                    new NeverCalledTelegramBotApiClient()
            );

            this.connectivityResult = connectivityResult;
        }

        @Override
        public TelegramConnectivityResult checkConnectivity() {
            checkConnectivityCallCount++;

            return connectivityResult;
        }

        private int getCheckConnectivityCallCount() {
            return checkConnectivityCallCount;
        }
    }

    private static final class NeverCalledTelegramBotApiClient
            implements TelegramBotApiClient {

        @Override
        public TelegramBotApiResponse getMe() {
            throw new AssertionError(
                    "The fixed provider must not call Telegram Bot API client."
            );
        }

        @Override
        public TelegramBotApiResponse sendMessage(
                String chatId,
                String message
        ) {
            throw new AssertionError(
                    "A provider health check must not send Telegram messages."
            );
        }
    }

    private static final class RecordingProviderCheckService
            extends NotificationDeliveryProviderCheckService {

        private NotificationDeliveryChannel recordedChannel;

        private NotificationDeliveryProviderCheckStatus recordedStatus;

        private String recordedMessage;

        private Integer recordedHttpStatusCode;

        private int recordCallCount;

        private boolean threeParameterRecordUsed;

        private boolean fourParameterRecordUsed;

        private RecordingProviderCheckService() {
            super(null);
        }

        @Override
        public NotificationDeliveryProviderCheck record(
                NotificationDeliveryChannel channel,
                NotificationDeliveryProviderCheckStatus status,
                String resultMessage
        ) {
            threeParameterRecordUsed = true;

            return capture(
                    channel,
                    status,
                    resultMessage,
                    null
            );
        }

        @Override
        public NotificationDeliveryProviderCheck record(
                NotificationDeliveryChannel channel,
                NotificationDeliveryProviderCheckStatus status,
                String resultMessage,
                Integer httpStatusCode
        ) {
            fourParameterRecordUsed = true;

            return capture(
                    channel,
                    status,
                    resultMessage,
                    httpStatusCode
            );
        }

        private NotificationDeliveryProviderCheck capture(
                NotificationDeliveryChannel channel,
                NotificationDeliveryProviderCheckStatus status,
                String resultMessage,
                Integer httpStatusCode
        ) {
            recordCallCount++;
            recordedChannel = channel;
            recordedStatus = status;
            recordedMessage = resultMessage;
            recordedHttpStatusCode = httpStatusCode;

            return null;
        }

        private NotificationDeliveryChannel getRecordedChannel() {
            return recordedChannel;
        }

        private NotificationDeliveryProviderCheckStatus getRecordedStatus() {
            return recordedStatus;
        }

        private String getRecordedMessage() {
            return recordedMessage;
        }

        private Integer getRecordedHttpStatusCode() {
            return recordedHttpStatusCode;
        }

        private int getRecordCallCount() {
            return recordCallCount;
        }

        private boolean wasThreeParameterRecordUsed() {
            return threeParameterRecordUsed;
        }

        private boolean wasFourParameterRecordUsed() {
            return fourParameterRecordUsed;
        }
    }
}