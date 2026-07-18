package com.cigabyte.sitesentinel.notification.delivery;

import java.util.Objects;

public final class TelegramDocumentDeliveryResult {

    private static final int RESULT_MESSAGE_MAX_LENGTH =
            500;

    private static final int TECHNICAL_DETAIL_MAX_LENGTH =
            2000;

    private final TelegramDocumentDeliveryStatus status;

    private final boolean deliveryAttempted;

    private final boolean successful;

    private final String resultMessage;

    private final String technicalDetail;

    private final Long telegramMessageId;

    private TelegramDocumentDeliveryResult(
            TelegramDocumentDeliveryStatus status,
            boolean deliveryAttempted,
            boolean successful,
            String resultMessage,
            String technicalDetail,
            Long telegramMessageId
    ) {
        this.status =
                Objects.requireNonNull(
                        status,
                        "Telegram document delivery status is required."
                );

        this.deliveryAttempted =
                deliveryAttempted;

        this.successful =
                successful;

        this.resultMessage =
                normalizeRequiredText(
                        resultMessage,
                        "Telegram document delivery result message",
                        RESULT_MESSAGE_MAX_LENGTH
                );

        this.technicalDetail =
                normalizeRequiredText(
                        technicalDetail,
                        "Telegram document delivery technical detail",
                        TECHNICAL_DETAIL_MAX_LENGTH
                );

        this.telegramMessageId =
                validateTelegramMessageId(
                        telegramMessageId,
                        successful
                );

        validateState();
    }

    public static TelegramDocumentDeliveryResult success(
            Long telegramMessageId,
            String resultMessage,
            String technicalDetail
    ) {
        return new TelegramDocumentDeliveryResult(
                TelegramDocumentDeliveryStatus.SENT,
                true,
                true,
                resultMessage,
                technicalDetail,
                telegramMessageId
        );
    }

    public static TelegramDocumentDeliveryResult failure(
            String resultMessage,
            String technicalDetail
    ) {
        return new TelegramDocumentDeliveryResult(
                TelegramDocumentDeliveryStatus.FAILED,
                true,
                false,
                resultMessage,
                technicalDetail,
                null
        );
    }

    public static TelegramDocumentDeliveryResult disabled(
            String resultMessage,
            String technicalDetail
    ) {
        return new TelegramDocumentDeliveryResult(
                TelegramDocumentDeliveryStatus.DISABLED,
                false,
                false,
                resultMessage,
                technicalDetail,
                null
        );
    }

    public static TelegramDocumentDeliveryResult configurationMissing(
            String resultMessage,
            String technicalDetail
    ) {
        return new TelegramDocumentDeliveryResult(
                TelegramDocumentDeliveryStatus.CONFIGURATION_MISSING,
                false,
                false,
                resultMessage,
                technicalDetail,
                null
        );
    }

    private Long validateTelegramMessageId(
            Long value,
            boolean successful
    ) {
        if (!successful) {
            if (value != null) {
                throw new IllegalArgumentException(
                        "Unsuccessful Telegram document delivery "
                                + "must not contain a Telegram message ID."
                );
            }

            return null;
        }

        if (value == null || value <= 0) {
            throw new IllegalArgumentException(
                    "Successful Telegram document delivery "
                            + "requires a positive Telegram message ID."
            );
        }

        return value;
    }

    private void validateState() {
        if (successful
                && status
                != TelegramDocumentDeliveryStatus.SENT) {

            throw new IllegalArgumentException(
                    "Successful Telegram document delivery "
                            + "must have SENT status."
            );
        }

        if (!deliveryAttempted
                && status
                == TelegramDocumentDeliveryStatus.SENT) {

            throw new IllegalArgumentException(
                    "SENT Telegram document delivery "
                            + "must be marked as attempted."
            );
        }

        if (deliveryAttempted
                && (status
                == TelegramDocumentDeliveryStatus.DISABLED
                || status
                == TelegramDocumentDeliveryStatus.CONFIGURATION_MISSING)) {

            throw new IllegalArgumentException(
                    "Disabled or unconfigured Telegram document delivery "
                            + "must not be marked as attempted."
            );
        }
    }

    private String normalizeRequiredText(
            String value,
            String fieldName,
            int maximumLength
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " is required."
            );
        }

        String normalizedValue =
                value.trim();

        if (normalizedValue.length()
                > maximumLength) {

            throw new IllegalArgumentException(
                    fieldName
                            + " must not exceed "
                            + maximumLength
                            + " characters."
            );
        }

        return normalizedValue;
    }

    public TelegramDocumentDeliveryStatus getStatus() {
        return status;
    }

    public boolean isDeliveryAttempted() {
        return deliveryAttempted;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public String getTechnicalDetail() {
        return technicalDetail;
    }

    public Long getTelegramMessageId() {
        return telegramMessageId;
    }
}