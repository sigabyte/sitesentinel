package com.cigabyte.sitesentinel.notification.delivery;

public interface TelegramBotApiClient {

    TelegramBotApiResponse getMe();

    TelegramBotApiResponse sendMessage(
            String chatId,
            String message
    );
}