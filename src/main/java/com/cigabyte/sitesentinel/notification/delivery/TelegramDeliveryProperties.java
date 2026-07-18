package com.cigabyte.sitesentinel.notification.delivery;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConfigurationProperties(prefix = "sitesentinel.notification.delivery.telegram")
public class TelegramDeliveryProperties {

    private boolean enabled = false;

    private boolean automaticPdfDispatchEnabled =
            false;

    private String botToken = "";

    private String chatId = "";

    private String apiBaseUrl = "https://api.telegram.org";

    private long connectTimeoutSeconds = 5;

    private long requestTimeoutSeconds = 10;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAutomaticPdfDispatchEnabled() {
        return automaticPdfDispatchEnabled;
    }

    public void setAutomaticPdfDispatchEnabled(
            boolean automaticPdfDispatchEnabled
    ) {
        this.automaticPdfDispatchEnabled =
                automaticPdfDispatchEnabled;
    }

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = sanitize(botToken);
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = sanitize(chatId);
    }

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = sanitize(apiBaseUrl);

        if (!StringUtils.hasText(this.apiBaseUrl)) {
            this.apiBaseUrl = "https://api.telegram.org";
        }
    }

    public long getConnectTimeoutSeconds() {
        return connectTimeoutSeconds;
    }

    public void setConnectTimeoutSeconds(long connectTimeoutSeconds) {
        this.connectTimeoutSeconds = Math.max(1, connectTimeoutSeconds);
    }

    public long getRequestTimeoutSeconds() {
        return requestTimeoutSeconds;
    }

    public void setRequestTimeoutSeconds(long requestTimeoutSeconds) {
        this.requestTimeoutSeconds = Math.max(1, requestTimeoutSeconds);
    }

    public boolean hasRequiredConfiguration() {
        return StringUtils.hasText(botToken)
                && StringUtils.hasText(chatId)
                && StringUtils.hasText(apiBaseUrl);
    }

    public String getNormalizedApiBaseUrl() {
        if (!StringUtils.hasText(apiBaseUrl)) {
            return "https://api.telegram.org";
        }

        return apiBaseUrl.endsWith("/")
                ? apiBaseUrl.substring(0, apiBaseUrl.length() - 1)
                : apiBaseUrl;
    }

    private String sanitize(String value) {
        return value == null ? "" : value.trim();
    }
}