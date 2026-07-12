package com.cigabyte.sitesentinel.notification.delivery;

import com.cigabyte.sitesentinel.notification.NotificationDeliveryChannel;
import com.cigabyte.sitesentinel.notification.NotificationEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class TelegramNotificationDeliveryProvider implements NotificationDeliveryProvider {

    private static final int TELEGRAM_MESSAGE_MAX_LENGTH = 3900;

    private static final int TECHNICAL_DETAIL_MAX_LENGTH = 2000;

    private final TelegramDeliveryProperties telegramDeliveryProperties;

    private final HttpClient httpClient;

    public TelegramNotificationDeliveryProvider(
            TelegramDeliveryProperties telegramDeliveryProperties
    ) {
        this.telegramDeliveryProperties = telegramDeliveryProperties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(telegramDeliveryProperties.getConnectTimeoutSeconds()))
                .build();
    }

    @Override
    public NotificationDeliveryChannel getChannel() {

        return NotificationDeliveryChannel.TELEGRAM;
    }

    public boolean verifyConnection() {
        if (!telegramDeliveryProperties.isEnabled()) {
            return false;
        }

        if (!telegramDeliveryProperties.hasRequiredConfiguration()) {
            return false;
        }

        String telegramApiUrl =
                telegramDeliveryProperties.getNormalizedApiBaseUrl()
                        + "/bot"
                        + telegramDeliveryProperties.getBotToken()
                        + "/getMe";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(telegramApiUrl))
                .timeout(Duration.ofSeconds(
                        telegramDeliveryProperties.getRequestTimeoutSeconds()
                ))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            int statusCode = response.statusCode();

            return statusCode >= 200
                    && statusCode < 300
                    && response.body() != null
                    && response.body().contains("\"ok\":true");

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return false;

        } catch (IOException | IllegalArgumentException exception) {
            return false;
        }
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

    private NotificationDeliveryProviderResult sendTelegramMessage(NotificationEvent notificationEvent) {
        String telegramApiUrl = telegramDeliveryProperties.getNormalizedApiBaseUrl()
                + "/bot"
                + telegramDeliveryProperties.getBotToken()
                + "/sendMessage";

        String requestBody = buildRequestBody(notificationEvent);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(telegramApiUrl))
                .timeout(Duration.ofSeconds(telegramDeliveryProperties.getRequestTimeoutSeconds()))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            int statusCode = response.statusCode();

            if (statusCode >= 200 && statusCode < 300) {
                return NotificationDeliveryProviderResult.success(
                        "Telegram message was sent successfully.",
                        "Telegram Bot API accepted the sendMessage request. HTTP status=" + statusCode + "."
                );
            }

            return NotificationDeliveryProviderResult.failure(
                    "Telegram message delivery failed.",
                    "Telegram Bot API returned HTTP status="
                            + statusCode
                            + ". Response body="
                            + truncateTechnicalDetail(response.body())
            );
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            return NotificationDeliveryProviderResult.failure(
                    "Telegram message delivery was interrupted.",
                    exception.getClass().getSimpleName() + ": " + safeMessage(exception.getMessage())
            );
        } catch (IOException | IllegalArgumentException exception) {
            return NotificationDeliveryProviderResult.failure(
                    "Telegram message delivery failed while calling Telegram Bot API.",
                    exception.getClass().getSimpleName() + ": " + safeMessage(exception.getMessage())
            );
        }
    }

    private String buildRequestBody(NotificationEvent notificationEvent) {
        return "chat_id=" + encode(telegramDeliveryProperties.getChatId())
                + "&text=" + encode(buildTelegramMessage(notificationEvent))
                + "&disable_web_page_preview=true";
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

        if (message.length() <= TELEGRAM_MESSAGE_MAX_LENGTH) {
            return message;
        }

        return message.substring(0, TELEGRAM_MESSAGE_MAX_LENGTH) + "...";
    }

    private String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private void validateNotificationEvent(NotificationEvent notificationEvent) {
        if (notificationEvent == null) {
            throw new IllegalArgumentException("Notification event is required for delivery.");
        }

        if (notificationEvent.getId() == null) {
            throw new IllegalArgumentException("Notification event id is required for delivery.");
        }
    }

    private String safeMessage(String value) {
        if (value == null || value.isBlank()) {
            return "No technical message was provided.";
        }

        return truncateTechnicalDetail(value);
    }

    private String truncateTechnicalDetail(String value) {
        if (value == null) {
            return "";
        }

        if (value.length() <= TECHNICAL_DETAIL_MAX_LENGTH) {
            return value;
        }

        return value.substring(0, TECHNICAL_DETAIL_MAX_LENGTH) + "...";
    }
}