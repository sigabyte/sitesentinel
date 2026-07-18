package com.cigabyte.sitesentinel.notification.delivery;

public interface TelegramBotApiClient {

    TelegramBotApiResponse getMe();

    TelegramBotApiResponse sendMessage(
            String chatId,
            String message
    );

    default TelegramBotApiResponse sendDocument(
            TelegramDocumentUploadRequest request
    ) {
        throw new UnsupportedOperationException(
                "Telegram document upload "
                        + "is not implemented yet."
        );
    }
}