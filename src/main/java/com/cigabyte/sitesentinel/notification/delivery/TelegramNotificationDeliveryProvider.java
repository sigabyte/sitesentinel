package com.cigabyte.sitesentinel.notification.delivery;

import com.cigabyte.sitesentinel.notification.NotificationDeliveryChannel;
import com.cigabyte.sitesentinel.notification.NotificationEvent;
import org.springframework.stereotype.Component;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.net.http.HttpTimeoutException;

@Component
public class TelegramNotificationDeliveryProvider implements NotificationDeliveryProvider {

    private static final int TELEGRAM_MESSAGE_MAX_LENGTH = 3900;
    private static final String TELEGRAM_MESSAGE_TRUNCATION_SUFFIX =
            "...";

    private final TelegramDeliveryProperties telegramDeliveryProperties;

    private final TelegramBotApiClient telegramBotApiClient;

    public TelegramNotificationDeliveryProvider(
            TelegramDeliveryProperties telegramDeliveryProperties,
            TelegramBotApiClient telegramBotApiClient
    ) {
        this.telegramDeliveryProperties = telegramDeliveryProperties;
        this.telegramBotApiClient = telegramBotApiClient;
    }

    @Override
    public NotificationDeliveryChannel getChannel() {

        return NotificationDeliveryChannel.TELEGRAM;
    }

    public TelegramConnectivityResult checkConnectivity() {
        if (!telegramDeliveryProperties.isEnabled()) {
            return new TelegramConnectivityResult(
                    TelegramConnectivityStatus.FAILED,
                    "Connectivity check was not executed because the Telegram provider is disabled.",
                    null
            );
        }

        if (!telegramDeliveryProperties.hasRequiredConfiguration()) {
            return new TelegramConnectivityResult(
                    TelegramConnectivityStatus.FAILED,
                    "Connectivity check was not executed because the Telegram provider configuration is incomplete.",
                    null
            );
        }

        try {
            TelegramBotApiResponse response =
                    telegramBotApiClient.getMe();

            return classifyConnectivityResponse(response);

        } catch (TelegramBotApiClientException exception) {
            return classifyConnectivityException(exception);
        }
    }

    private TelegramConnectivityResult classifyConnectivityResponse(
            TelegramBotApiResponse response
    ) {
        int statusCode = response.getStatusCode();

        if (response.indicatesSuccessfulTelegramResponse()) {
            return new TelegramConnectivityResult(
                    TelegramConnectivityStatus.HEALTHY,
                    "Telegram Bot API connectivity was verified.",
                    statusCode
            );
        }

        if (indicatesAuthenticationFailure(response)) {
            return new TelegramConnectivityResult(
                    TelegramConnectivityStatus.AUTHENTICATION_FAILED,
                    "Telegram Bot API rejected the configured bot credentials.",
                    statusCode
            );
        }

        if (response.hasSuccessfulStatusCode()) {
            return new TelegramConnectivityResult(
                    TelegramConnectivityStatus.INVALID_RESPONSE,
                    "Telegram Bot API returned an unexpected successful response.",
                    statusCode
            );
        }

        return new TelegramConnectivityResult(
                TelegramConnectivityStatus.FAILED,
                "Telegram Bot API returned an unsuccessful HTTP status.",
                statusCode
        );
    }

    private boolean indicatesAuthenticationFailure(
            TelegramBotApiResponse response
    ) {
        int statusCode = response.getStatusCode();

        if (statusCode == 401 || statusCode == 403) {
            return true;
        }

        String responseBody = response.getBody();

        if (responseBody == null || responseBody.isBlank()) {
            return false;
        }

        String normalizedResponseBody =
                responseBody.toLowerCase();

        return normalizedResponseBody.contains("\"error_code\":401")
                || normalizedResponseBody.contains("unauthorized");
    }

    private TelegramConnectivityResult classifyConnectivityException(
            TelegramBotApiClientException exception
    ) {
        Throwable cause = exception.getCause();

        if (cause instanceof InterruptedException) {
            return new TelegramConnectivityResult(
                    TelegramConnectivityStatus.INTERRUPTED,
                    "Telegram Bot API connectivity check was interrupted.",
                    null
            );
        }

        if (cause instanceof HttpTimeoutException) {
            return new TelegramConnectivityResult(
                    TelegramConnectivityStatus.TIMEOUT,
                    "Telegram Bot API connectivity check timed out.",
                    null
            );
        }

        if (cause instanceof ConnectException
                || cause instanceof UnknownHostException
                || cause instanceof NoRouteToHostException) {

            return new TelegramConnectivityResult(
                    TelegramConnectivityStatus.UNREACHABLE,
                    "Telegram Bot API could not be reached.",
                    null
            );
        }

        return new TelegramConnectivityResult(
                TelegramConnectivityStatus.FAILED,
                "Telegram Bot API connectivity check failed.",
                null
        );
    }

    @Override
    public NotificationDeliveryProviderResult deliver(NotificationEvent notificationEvent) {
        validateNotificationEvent(notificationEvent);

        if (!telegramDeliveryProperties.isEnabled()) {
            return NotificationDeliveryProviderResult.disabled(
                    "Telegram delivery is disabled.",
                    "Telegram provider was not called because sitesentinel.notification.delivery.telegram.enabled=false."
            );
        }

        if (!telegramDeliveryProperties.hasRequiredConfiguration()) {
            return NotificationDeliveryProviderResult.configurationMissing(
                    "Telegram delivery configuration is missing.",
                    "Telegram provider was not called because bot token, chat id, or API base URL is missing."
            );
        }

        return sendTelegramMessage(notificationEvent);
    }

    private NotificationDeliveryProviderResult sendTelegramMessage(
            NotificationEvent notificationEvent
    ) {
        String message = buildTelegramMessage(notificationEvent);

        try {
            TelegramBotApiResponse response =
                    telegramBotApiClient.sendMessage(
                            telegramDeliveryProperties.getChatId(),
                            message
                    );

            int statusCode = response.getStatusCode();

            if (response.indicatesSuccessfulTelegramResponse()) {
                return NotificationDeliveryProviderResult.success(
                        "Telegram message was sent successfully.",
                        "Telegram Bot API accepted the sendMessage request. HTTP status="
                                + statusCode
                                + "."
                );
            }

            return NotificationDeliveryProviderResult.failure(
                    "Telegram message delivery failed.",
                    "Telegram Bot API returned HTTP status="
                            + statusCode
                            + "."
            );

        } catch (TelegramBotApiClientException exception) {
            return NotificationDeliveryProviderResult.failure(
                    "Telegram message delivery failed while calling Telegram Bot API.",
                    "Telegram Bot API client call failed before a valid response was received."
            );
        }
    }

    private String buildTelegramMessage(NotificationEvent notificationEvent) {
        String message = """
                SiteSentinel Notification

                Severity: %s
                Event Type: %s

                %s

                %s

                Website ID: %s
                Monitoring Run ID: %s
                Notification Event ID: %s
                """.formatted(
                notificationEvent.getSeverity(),
                notificationEvent.getEventType(),
                notificationEvent.getTitle(),
                notificationEvent.getMessage(),
                notificationEvent.getWebsiteId(),
                notificationEvent.getMonitoringRunId(),
                notificationEvent.getId()
        ).trim();

        return truncateTelegramMessage(message);
    }

    private String truncateTelegramMessage(String message) {
        if (message.length() <= TELEGRAM_MESSAGE_MAX_LENGTH) {
            return message;
        }

        int maximumContentLength =
                TELEGRAM_MESSAGE_MAX_LENGTH
                        - TELEGRAM_MESSAGE_TRUNCATION_SUFFIX.length();

        int endIndex =
                maximumContentLength;

        if (endIndex > 0
                && endIndex < message.length()
                && Character.isHighSurrogate(
                message.charAt(endIndex - 1)
        )
                && Character.isLowSurrogate(
                message.charAt(endIndex)
        )) {

            endIndex--;
        }

        return message.substring(
                0,
                endIndex
        ) + TELEGRAM_MESSAGE_TRUNCATION_SUFFIX;
    }

    private void validateNotificationEvent(NotificationEvent notificationEvent) {
        if (notificationEvent == null) {
            throw new IllegalArgumentException("Notification event is required for delivery.");
        }

        if (notificationEvent.getId() == null) {
            throw new IllegalArgumentException("Notification event id is required for delivery.");
        }
    }
}