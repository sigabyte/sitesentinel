package com.cigabyte.sitesentinel.notification.delivery;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TelegramDocumentDeliveryResultTests {

    @Test
    void successCreatesAttemptedSuccessfulResultWithMessageId() {
        TelegramDocumentDeliveryResult result =
                TelegramDocumentDeliveryResult.success(
                        2468L,
                        "  Telegram document was sent successfully.  ",
                        "  Telegram Bot API accepted the request.  "
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
                "Telegram document was sent successfully.",
                result.getResultMessage()
        );

        assertEquals(
                "Telegram Bot API accepted the request.",
                result.getTechnicalDetail()
        );

        assertEquals(
                Long.valueOf(2468L),
                result.getTelegramMessageId()
        );
    }

    @Test
    void successRejectsMissingOrNonPositiveTelegramMessageId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> TelegramDocumentDeliveryResult.success(
                        null,
                        "Document sent.",
                        "Request accepted."
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> TelegramDocumentDeliveryResult.success(
                        0L,
                        "Document sent.",
                        "Request accepted."
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> TelegramDocumentDeliveryResult.success(
                        -1L,
                        "Document sent.",
                        "Request accepted."
                )
        );
    }

    @Test
    void failureCreatesAttemptedUnsuccessfulResultWithoutMessageId() {
        TelegramDocumentDeliveryResult result =
                TelegramDocumentDeliveryResult.failure(
                        "Telegram document delivery failed.",
                        "Telegram Bot API returned an unsuccessful response."
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
    }

    @Test
    void disabledCreatesNonAttemptedResult() {
        TelegramDocumentDeliveryResult result =
                TelegramDocumentDeliveryResult.disabled(
                        "Telegram document delivery is disabled.",
                        "Telegram provider was not called."
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
    }

    @Test
    void configurationMissingCreatesNonAttemptedResult() {
        TelegramDocumentDeliveryResult result =
                TelegramDocumentDeliveryResult.configurationMissing(
                        "Telegram document delivery configuration is missing.",
                        "Telegram provider was not called."
                );

        assertEquals(
                TelegramDocumentDeliveryStatus.CONFIGURATION_MISSING,
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
    }
}