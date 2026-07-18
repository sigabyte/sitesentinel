package com.cigabyte.sitesentinel.notification.delivery;

import org.springframework.stereotype.Service;

@Service
public class TelegramDocumentDeliveryService {

    private final TelegramDeliveryProperties
            telegramDeliveryProperties;

    private final TelegramBotApiClient
            telegramBotApiClient;

    public TelegramDocumentDeliveryService(
            TelegramDeliveryProperties telegramDeliveryProperties,
            TelegramBotApiClient telegramBotApiClient
    ) {
        this.telegramDeliveryProperties =
                telegramDeliveryProperties;

        this.telegramBotApiClient =
                telegramBotApiClient;
    }

    public TelegramDocumentDeliveryResult deliver(
            String fileName,
            String contentType,
            byte[] documentBytes,
            String caption
    ) {
        if (!telegramDeliveryProperties.isEnabled()) {
            return TelegramDocumentDeliveryResult.disabled(
                    "Telegram document delivery is disabled.",
                    "Telegram Bot API was not called because "
                            + "sitesentinel.notification.delivery."
                            + "telegram.enabled=false."
            );
        }

        if (!telegramDeliveryProperties
                .hasRequiredConfiguration()) {

            return TelegramDocumentDeliveryResult
                    .configurationMissing(
                            "Telegram document delivery "
                                    + "configuration is missing.",
                            "Telegram Bot API was not called because "
                                    + "bot token, chat ID, or API base "
                                    + "URL is missing."
                    );
        }

        TelegramDocumentUploadRequest uploadRequest =
                new TelegramDocumentUploadRequest(
                        telegramDeliveryProperties.getChatId(),
                        fileName,
                        contentType,
                        documentBytes,
                        caption
                );

        return sendDocument(
                uploadRequest
        );
    }

    private TelegramDocumentDeliveryResult sendDocument(
            TelegramDocumentUploadRequest uploadRequest
    ) {
        try {
            TelegramBotApiResponse response =
                    telegramBotApiClient.sendDocument(
                            uploadRequest
                    );

            if (response == null) {
                return TelegramDocumentDeliveryResult.failure(
                        "Telegram document delivery failed.",
                        "Telegram Bot API client returned "
                                + "no response."
                );
            }

            int statusCode =
                    response.getStatusCode();

            if (!response
                    .indicatesSuccessfulTelegramResponse()) {

                return TelegramDocumentDeliveryResult.failure(
                        "Telegram document delivery failed.",
                        "Telegram Bot API returned HTTP status="
                                + statusCode
                                + "."
                );
            }

            Long telegramMessageId =
                    response.getTelegramMessageId();

            if (telegramMessageId == null) {
                return TelegramDocumentDeliveryResult.failure(
                        "Telegram document delivery failed.",
                        "Telegram Bot API accepted the "
                                + "sendDocument request but did not "
                                + "return a valid message ID. "
                                + "HTTP status="
                                + statusCode
                                + "."
                );
            }

            return TelegramDocumentDeliveryResult.success(
                    telegramMessageId,
                    "Telegram document was sent successfully.",
                    "Telegram Bot API accepted the "
                            + "sendDocument request. HTTP status="
                            + statusCode
                            + "."
            );

        } catch (TelegramBotApiClientException exception) {
            return TelegramDocumentDeliveryResult.failure(
                    "Telegram document delivery failed while "
                            + "calling Telegram Bot API.",
                    "Telegram Bot API client call failed before "
                            + "a valid response was received."
            );
        }
    }
}