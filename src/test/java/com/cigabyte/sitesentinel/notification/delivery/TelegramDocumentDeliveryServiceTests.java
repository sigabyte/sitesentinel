package com.cigabyte.sitesentinel.notification.delivery;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TelegramDocumentDeliveryServiceTests {

    @Test
    void deliverReturnsDisabledWithoutCallingClient() {
        TelegramDeliveryProperties properties =
                readyTelegramProperties();

        properties.setEnabled(false);

        RecordingTelegramBotApiClient client =
                new RecordingTelegramBotApiClient();

        TelegramDocumentDeliveryService service =
                service(
                        properties,
                        client
                );

        TelegramDocumentDeliveryResult result =
                service.deliver(
                        "monitoring-report.pdf",
                        "application/pdf",
                        documentBytes(),
                        "Monitoring run report"
                );

        assertEquals(
                TelegramDocumentDeliveryStatus.DISABLED,
                result.getStatus()
        );

        assertFalse(
                result.isDeliveryAttempted()
        );

        assertFalse(
                result.isSuccessful()
        );

        assertNull(
                result.getTelegramMessageId()
        );

        assertEquals(
                0,
                client.getSendDocumentCallCount()
        );
    }

    @Test
    void deliverReturnsConfigurationMissingWithoutCallingClient() {
        TelegramDeliveryProperties properties =
                readyTelegramProperties();

        properties.setBotToken("");

        RecordingTelegramBotApiClient client =
                new RecordingTelegramBotApiClient();

        TelegramDocumentDeliveryService service =
                service(
                        properties,
                        client
                );

        TelegramDocumentDeliveryResult result =
                service.deliver(
                        "monitoring-report.pdf",
                        "application/pdf",
                        documentBytes(),
                        "Monitoring run report"
                );

        assertEquals(
                TelegramDocumentDeliveryStatus
                        .CONFIGURATION_MISSING,
                result.getStatus()
        );

        assertFalse(
                result.isDeliveryAttempted()
        );

        assertFalse(
                result.isSuccessful()
        );

        assertNull(
                result.getTelegramMessageId()
        );

        assertEquals(
                0,
                client.getSendDocumentCallCount()
        );
    }

    @Test
    void deliverSendsDocumentToConfiguredDestination() {
        TelegramDeliveryProperties properties =
                readyTelegramProperties();

        RecordingTelegramBotApiClient client =
                new RecordingTelegramBotApiClient();

        client.setSendDocumentResponse(
                new TelegramBotApiResponse(
                        200,
                        """
                        {
                          "ok": true,
                          "result": {
                            "message_id": 2468
                          }
                        }
                        """
                )
        );

        TelegramDocumentDeliveryService service =
                service(
                        properties,
                        client
                );

        byte[] documentBytes =
                documentBytes();

        TelegramDocumentDeliveryResult result =
                service.deliver(
                        "monitoring-report.pdf",
                        "application/pdf",
                        documentBytes,
                        "Monitoring run report"
                );

        assertEquals(
                TelegramDocumentDeliveryStatus.SENT,
                result.getStatus()
        );

        assertTrue(
                result.isDeliveryAttempted()
        );

        assertTrue(
                result.isSuccessful()
        );

        assertEquals(
                Long.valueOf(2468L),
                result.getTelegramMessageId()
        );

        assertEquals(
                "Telegram document was sent successfully.",
                result.getResultMessage()
        );

        assertEquals(
                "Telegram Bot API accepted the "
                        + "sendDocument request. "
                        + "HTTP status=200.",
                result.getTechnicalDetail()
        );

        assertEquals(
                1,
                client.getSendDocumentCallCount()
        );

        TelegramDocumentUploadRequest recordedRequest =
                client.getRecordedUploadRequest();

        assertNotNull(
                recordedRequest
        );

        assertEquals(
                "test-chat-id",
                recordedRequest.getChatId()
        );

        assertEquals(
                "monitoring-report.pdf",
                recordedRequest.getFileName()
        );

        assertEquals(
                "application/pdf",
                recordedRequest.getContentType()
        );

        assertEquals(
                "Monitoring run report",
                recordedRequest.getCaption()
        );

        assertArrayEquals(
                documentBytes,
                recordedRequest.getDocumentBytes()
        );
    }

    @Test
    void deliverReturnsFailedWhenSuccessfulResponseHasNoMessageId() {
        RecordingTelegramBotApiClient client =
                new RecordingTelegramBotApiClient();

        client.setSendDocumentResponse(
                new TelegramBotApiResponse(
                        200,
                        """
                        {
                          "ok": true,
                          "result": {
                            "document": {
                              "file_id": "test-file-id"
                            }
                          }
                        }
                        """
                )
        );

        TelegramDocumentDeliveryService service =
                service(
                        readyTelegramProperties(),
                        client
                );

        TelegramDocumentDeliveryResult result =
                service.deliver(
                        "monitoring-report.pdf",
                        "application/pdf",
                        documentBytes(),
                        "Monitoring run report"
                );

        assertEquals(
                TelegramDocumentDeliveryStatus.FAILED,
                result.getStatus()
        );

        assertTrue(
                result.isDeliveryAttempted()
        );

        assertFalse(
                result.isSuccessful()
        );

        assertNull(
                result.getTelegramMessageId()
        );

        assertEquals(
                "Telegram Bot API accepted the "
                        + "sendDocument request but did not "
                        + "return a valid message ID. "
                        + "HTTP status=200.",
                result.getTechnicalDetail()
        );

        assertEquals(
                1,
                client.getSendDocumentCallCount()
        );
    }

    @Test
    void deliverReturnsFailedForUnsuccessfulTelegramResponse() {
        RecordingTelegramBotApiClient client =
                new RecordingTelegramBotApiClient();

        client.setSendDocumentResponse(
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

        TelegramDocumentDeliveryService service =
                service(
                        readyTelegramProperties(),
                        client
                );

        TelegramDocumentDeliveryResult result =
                service.deliver(
                        "monitoring-report.pdf",
                        "application/pdf",
                        documentBytes(),
                        "Monitoring run report"
                );

        assertEquals(
                TelegramDocumentDeliveryStatus.FAILED,
                result.getStatus()
        );

        assertTrue(
                result.isDeliveryAttempted()
        );

        assertFalse(
                result.isSuccessful()
        );

        assertNull(
                result.getTelegramMessageId()
        );

        assertEquals(
                "Telegram Bot API returned HTTP status=429.",
                result.getTechnicalDetail()
        );

        assertEquals(
                1,
                client.getSendDocumentCallCount()
        );
    }

    @Test
    void deliverReturnsSecretSafeFailureForClientException() {
        RecordingTelegramBotApiClient client =
                new RecordingTelegramBotApiClient();

        client.setSendDocumentFailure(
                new TelegramBotApiClientException(
                        "Sensitive simulated bot token "
                                + "and endpoint detail.",
                        new IllegalStateException(
                                "Controlled client failure."
                        )
                )
        );

        TelegramDocumentDeliveryService service =
                service(
                        readyTelegramProperties(),
                        client
                );

        TelegramDocumentDeliveryResult result =
                service.deliver(
                        "monitoring-report.pdf",
                        "application/pdf",
                        documentBytes(),
                        "Monitoring run report"
                );

        assertEquals(
                TelegramDocumentDeliveryStatus.FAILED,
                result.getStatus()
        );

        assertTrue(
                result.isDeliveryAttempted()
        );

        assertFalse(
                result.isSuccessful()
        );

        assertNull(
                result.getTelegramMessageId()
        );

        assertEquals(
                "Telegram document delivery failed while "
                        + "calling Telegram Bot API.",
                result.getResultMessage()
        );

        assertEquals(
                "Telegram Bot API client call failed before "
                        + "a valid response was received.",
                result.getTechnicalDetail()
        );

        assertFalse(
                result.getResultMessage().contains(
                        "Sensitive simulated"
                )
        );

        assertFalse(
                result.getTechnicalDetail().contains(
                        "Sensitive simulated"
                )
        );

        assertEquals(
                1,
                client.getSendDocumentCallCount()
        );
    }

    private TelegramDocumentDeliveryService service(
            TelegramDeliveryProperties properties,
            TelegramBotApiClient client
    ) {
        return new TelegramDocumentDeliveryService(
                properties,
                client
        );
    }

    private TelegramDeliveryProperties readyTelegramProperties() {
        TelegramDeliveryProperties properties =
                new TelegramDeliveryProperties();

        properties.setEnabled(true);
        properties.setBotToken(
                "test-bot-token"
        );

        properties.setChatId(
                "test-chat-id"
        );

        properties.setApiBaseUrl(
                "https://api.telegram.org"
        );

        return properties;
    }

    private byte[] documentBytes() {
        return "%PDF-controlled-test-document"
                .getBytes(
                        StandardCharsets.US_ASCII
                );
    }

    private static final class RecordingTelegramBotApiClient
            implements TelegramBotApiClient {

        private TelegramBotApiResponse
                sendDocumentResponse;

        private TelegramBotApiClientException
                sendDocumentFailure;

        private TelegramDocumentUploadRequest
                recordedUploadRequest;

        private int sendDocumentCallCount;

        @Override
        public TelegramBotApiResponse getMe() {
            throw new AssertionError(
                    "Document delivery must not call getMe()."
            );
        }

        @Override
        public TelegramBotApiResponse sendMessage(
                String chatId,
                String message
        ) {
            throw new AssertionError(
                    "Document delivery must not call sendMessage()."
            );
        }

        @Override
        public TelegramBotApiResponse sendDocument(
                TelegramDocumentUploadRequest request
        ) {
            sendDocumentCallCount++;
            recordedUploadRequest = request;

            if (sendDocumentFailure != null) {
                throw sendDocumentFailure;
            }

            if (sendDocumentResponse == null) {
                throw new AssertionError(
                        "No sendDocument response was configured."
                );
            }

            return sendDocumentResponse;
        }

        private void setSendDocumentResponse(
                TelegramBotApiResponse sendDocumentResponse
        ) {
            this.sendDocumentResponse =
                    sendDocumentResponse;
        }

        private void setSendDocumentFailure(
                TelegramBotApiClientException sendDocumentFailure
        ) {
            this.sendDocumentFailure =
                    sendDocumentFailure;
        }

        private TelegramDocumentUploadRequest
        getRecordedUploadRequest() {

            return recordedUploadRequest;
        }

        private int getSendDocumentCallCount() {
            return sendDocumentCallCount;
        }
    }
}