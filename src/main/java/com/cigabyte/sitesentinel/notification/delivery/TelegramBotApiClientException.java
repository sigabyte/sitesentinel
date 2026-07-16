package com.cigabyte.sitesentinel.notification.delivery;

public class TelegramBotApiClientException extends RuntimeException {

    public TelegramBotApiClientException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}