package com.cigabyte.sitesentinel.notification.delivery;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

public class TelegramBotApiResponse {

    private static final JsonMapper JSON_MAPPER =
            JsonMapper.builder().build();

    private final int statusCode;

    private final String body;

    public TelegramBotApiResponse(
            int statusCode,
            String body
    ) {
        this.statusCode = statusCode;
        this.body = body == null ? "" : body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }

    public boolean hasSuccessfulStatusCode() {
        return statusCode >= 200 && statusCode < 300;
    }

    public boolean indicatesSuccessfulTelegramResponse() {
        if (!hasSuccessfulStatusCode()) {
            return false;
        }

        if (body.isBlank()) {
            return false;
        }

        try {
            JsonNode rootNode =
                    JSON_MAPPER.readTree(body);

            if (rootNode == null || !rootNode.isObject()) {
                return false;
            }

            JsonNode okNode =
                    rootNode.get("ok");

            return okNode != null
                    && okNode.isBoolean()
                    && okNode.booleanValue();

        } catch (JacksonException exception) {
            return false;
        }
    }

    public Long getTelegramMessageId() {
        if (!indicatesSuccessfulTelegramResponse()) {
            return null;
        }

        try {
            JsonNode rootNode =
                    JSON_MAPPER.readTree(body);

            if (rootNode == null || !rootNode.isObject()) {
                return null;
            }

            JsonNode resultNode =
                    rootNode.get("result");

            if (resultNode == null || !resultNode.isObject()) {
                return null;
            }

            JsonNode messageIdNode =
                    resultNode.get("message_id");

            if (messageIdNode == null
                    || !messageIdNode.isIntegralNumber()
                    || !messageIdNode.canConvertToLong()) {

                return null;
            }

            return messageIdNode.longValue();

        } catch (JacksonException exception) {
            return null;
        }
    }
}