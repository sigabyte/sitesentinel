package com.cigabyte.sitesentinel.notification.delivery;

import com.cigabyte.sitesentinel.notification.NotificationDeliveryChannel;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationDeliveryProviderCheckValidationTests {

    private static final int RESULT_MESSAGE_MAX_LENGTH =
            500;

    private static final String TRUNCATION_SUFFIX =
            "...";

    private static final String DEFAULT_RESULT_MESSAGE =
            "No provider-check result message was provided.";

    @Test
    void constructorRejectsMissingRequiredFields() {
        NullPointerException missingChannelException =
                assertThrows(
                        NullPointerException.class,
                        () -> new NotificationDeliveryProviderCheck(
                                null,
                                NotificationDeliveryProviderCheckStatus.HEALTHY,
                                OffsetDateTime.now(),
                                "Safe diagnostic.",
                                200
                        )
                );

        assertEquals(
                "Provider-check channel is required.",
                missingChannelException.getMessage()
        );

        NullPointerException missingStatusException =
                assertThrows(
                        NullPointerException.class,
                        () -> new NotificationDeliveryProviderCheck(
                                NotificationDeliveryChannel.TELEGRAM,
                                null,
                                OffsetDateTime.now(),
                                "Safe diagnostic.",
                                200
                        )
                );

        assertEquals(
                "Provider-check status is required.",
                missingStatusException.getMessage()
        );

        NullPointerException missingTimestampException =
                assertThrows(
                        NullPointerException.class,
                        () -> new NotificationDeliveryProviderCheck(
                                NotificationDeliveryChannel.TELEGRAM,
                                NotificationDeliveryProviderCheckStatus.HEALTHY,
                                null,
                                "Safe diagnostic.",
                                200
                        )
                );

        assertEquals(
                "Provider-check timestamp is required.",
                missingTimestampException.getMessage()
        );
    }

    @Test
    void constructorReplacesMissingDiagnosticWithSafeDefault() {
        NotificationDeliveryProviderCheck nullMessageCheck =
                providerCheck(
                        null,
                        null
                );

        NotificationDeliveryProviderCheck blankMessageCheck =
                providerCheck(
                        "   ",
                        null
                );

        assertEquals(
                DEFAULT_RESULT_MESSAGE,
                nullMessageCheck.getResultMessage()
        );

        assertEquals(
                DEFAULT_RESULT_MESSAGE,
                blankMessageCheck.getResultMessage()
        );

        assertNull(
                nullMessageCheck.getHttpStatusCode()
        );
    }

    @Test
    void constructorTrimsDiagnosticMessage() {
        NotificationDeliveryProviderCheck check =
                providerCheck(
                        "  Telegram connectivity verified.  ",
                        200
                );

        assertEquals(
                "Telegram connectivity verified.",
                check.getResultMessage()
        );

        assertEquals(
                200,
                check.getHttpStatusCode()
        );
    }

    @Test
    void diagnosticExactlyAtBoundaryIsPreserved() {
        String boundaryMessage =
                "A".repeat(
                        RESULT_MESSAGE_MAX_LENGTH
                );

        NotificationDeliveryProviderCheck check =
                providerCheck(
                        boundaryMessage,
                        200
                );

        assertEquals(
                RESULT_MESSAGE_MAX_LENGTH,
                check.getResultMessage().length()
        );

        assertEquals(
                boundaryMessage,
                check.getResultMessage()
        );

        assertFalse(
                check.getResultMessage().endsWith(
                        TRUNCATION_SUFFIX
                )
        );
    }

    @Test
    void oversizedDiagnosticIsTruncatedWithinDatabaseBoundary() {
        String oversizedMessage =
                "B".repeat(
                        RESULT_MESSAGE_MAX_LENGTH + 200
                );

        NotificationDeliveryProviderCheck check =
                providerCheck(
                        oversizedMessage,
                        500
                );

        assertEquals(
                RESULT_MESSAGE_MAX_LENGTH,
                check.getResultMessage().length()
        );

        assertTrue(
                check.getResultMessage().endsWith(
                        TRUNCATION_SUFFIX
                )
        );
    }

    @Test
    void diagnosticTruncationDoesNotSplitUnicodeSurrogatePair() {
        String oversizedMessage =
                "C".repeat(496)
                        + "😀"
                        + "D".repeat(100);

        NotificationDeliveryProviderCheck check =
                providerCheck(
                        oversizedMessage,
                        500
                );

        assertTrue(
                check.getResultMessage().length()
                        <= RESULT_MESSAGE_MAX_LENGTH
        );

        assertTrue(
                check.getResultMessage().endsWith(
                        TRUNCATION_SUFFIX
                )
        );

        assertFalse(
                containsUnpairedSurrogate(
                        check.getResultMessage()
                )
        );
    }

    @Test
    void constructorAcceptsHttpStatusBoundaryValues() {
        NotificationDeliveryProviderCheck lowerBoundaryCheck =
                providerCheck(
                        "Lower HTTP status boundary.",
                        100
                );

        NotificationDeliveryProviderCheck upperBoundaryCheck =
                providerCheck(
                        "Upper HTTP status boundary.",
                        599
                );

        assertEquals(
                100,
                lowerBoundaryCheck.getHttpStatusCode()
        );

        assertEquals(
                599,
                upperBoundaryCheck.getHttpStatusCode()
        );
    }

    @Test
    void constructorRejectsHttpStatusOutsideValidRange() {
        IllegalArgumentException lowerException =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> providerCheck(
                                "Invalid lower HTTP status.",
                                99
                        )
                );

        IllegalArgumentException upperException =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> providerCheck(
                                "Invalid upper HTTP status.",
                                600
                        )
                );

        assertEquals(
                "HTTP status code must be between 100 and 599.",
                lowerException.getMessage()
        );

        assertEquals(
                "HTTP status code must be between 100 and 599.",
                upperException.getMessage()
        );
    }

    private NotificationDeliveryProviderCheck providerCheck(
            String resultMessage,
            Integer httpStatusCode
    ) {
        return new NotificationDeliveryProviderCheck(
                NotificationDeliveryChannel.TELEGRAM,
                NotificationDeliveryProviderCheckStatus.HEALTHY,
                OffsetDateTime.now(),
                resultMessage,
                httpStatusCode
        );
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
}