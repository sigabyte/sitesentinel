package com.cigabyte.sitesentinel.recommendation.openai;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConfigurationProperties(
        prefix = "sitesentinel.recommendation.ai.openai"
)
public class OpenAiRecommendationProperties {

    private static final String DEFAULT_API_BASE_URL =
            "https://api.openai.com/v1";

    private static final String DEFAULT_MODEL =
            "gpt-5.6-terra";

    private static final int MINIMUM_MAX_OUTPUT_TOKENS =
            256;

    private boolean enabled = false;

    private String apiKey = "";

    private String apiBaseUrl =
            DEFAULT_API_BASE_URL;

    private String model =
            DEFAULT_MODEL;

    private long connectTimeoutSeconds = 10;

    private long requestTimeoutSeconds = 60;

    private int maxOutputTokens = 2000;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = sanitize(apiKey);
    }

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = sanitize(apiBaseUrl);

        if (!StringUtils.hasText(this.apiBaseUrl)) {
            this.apiBaseUrl =
                    DEFAULT_API_BASE_URL;
        }
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = sanitize(model);

        if (!StringUtils.hasText(this.model)) {
            this.model =
                    DEFAULT_MODEL;
        }
    }

    public long getConnectTimeoutSeconds() {
        return connectTimeoutSeconds;
    }

    public void setConnectTimeoutSeconds(
            long connectTimeoutSeconds
    ) {
        this.connectTimeoutSeconds =
                Math.max(
                        1,
                        connectTimeoutSeconds
                );
    }

    public long getRequestTimeoutSeconds() {
        return requestTimeoutSeconds;
    }

    public void setRequestTimeoutSeconds(
            long requestTimeoutSeconds
    ) {
        this.requestTimeoutSeconds =
                Math.max(
                        1,
                        requestTimeoutSeconds
                );
    }

    public int getMaxOutputTokens() {
        return maxOutputTokens;
    }

    public void setMaxOutputTokens(
            int maxOutputTokens
    ) {
        this.maxOutputTokens =
                Math.max(
                        MINIMUM_MAX_OUTPUT_TOKENS,
                        maxOutputTokens
                );
    }

    public boolean hasRequiredConfiguration() {
        return StringUtils.hasText(apiKey)
                && StringUtils.hasText(apiBaseUrl)
                && StringUtils.hasText(model);
    }

    public boolean isReady() {
        return enabled
                && hasRequiredConfiguration();
    }

    public String getNormalizedApiBaseUrl() {
        if (!StringUtils.hasText(apiBaseUrl)) {
            return DEFAULT_API_BASE_URL;
        }

        return apiBaseUrl.endsWith("/")
                ? apiBaseUrl.substring(
                0,
                apiBaseUrl.length() - 1
        )
                : apiBaseUrl;
    }

    private String sanitize(String value) {
        return value == null
                ? ""
                : value.trim();
    }
}