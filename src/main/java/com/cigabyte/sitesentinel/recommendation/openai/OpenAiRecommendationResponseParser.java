package com.cigabyte.sitesentinel.recommendation.openai;

import com.cigabyte.sitesentinel.recommendation
        .RiskRemediationAiOutput;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Component
public class OpenAiRecommendationResponseParser {

    private static final JsonMapper JSON_MAPPER =
            JsonMapper.builder().build();

    private static final String COMPLETED_STATUS =
            "completed";

    private static final String MESSAGE_OUTPUT_TYPE =
            "message";

    private static final String OUTPUT_TEXT_CONTENT_TYPE =
            "output_text";

    private static final String REFUSAL_CONTENT_TYPE =
            "refusal";

    public OpenAiRecommendationApiResult
    parseSuccessfulResponse(
            int httpStatusCode,
            String responseBody
    ) {
        requireSuccessfulHttpStatus(
                httpStatusCode
        );

        if (responseBody == null
                || responseBody.isBlank()) {

            return OpenAiRecommendationApiResult
                    .invalidResponse(
                            httpStatusCode
                    );
        }

        try {
            JsonNode rootNode =
                    JSON_MAPPER.readTree(
                            responseBody
                    );

            if (!isCompletedResponse(rootNode)) {
                return OpenAiRecommendationApiResult
                        .invalidResponse(
                                httpStatusCode
                        );
            }

            JsonNode outputNode =
                    rootNode.get("output");

            if (outputNode == null
                    || !outputNode.isArray()
                    || outputNode.isEmpty()) {

                return OpenAiRecommendationApiResult
                        .invalidResponse(
                                httpStatusCode
                        );
            }

            String structuredOutput = null;

            for (JsonNode outputItem : outputNode) {
                if (!isMessageOutput(outputItem)) {
                    continue;
                }

                JsonNode contentNode =
                        outputItem.get("content");

                if (contentNode == null
                        || !contentNode.isArray()) {

                    continue;
                }

                for (JsonNode contentItem : contentNode) {
                    String contentType =
                            readText(
                                    contentItem,
                                    "type"
                            );

                    if (REFUSAL_CONTENT_TYPE.equals(
                            contentType
                    )) {
                        return OpenAiRecommendationApiResult
                                .requestRejected(
                                        httpStatusCode
                                );
                    }

                    if (!OUTPUT_TEXT_CONTENT_TYPE.equals(
                            contentType
                    )) {
                        continue;
                    }

                    String outputText =
                            readText(
                                    contentItem,
                                    "text"
                            );

                    if (outputText == null
                            || outputText.isBlank()) {

                        return OpenAiRecommendationApiResult
                                .invalidResponse(
                                        httpStatusCode
                                );
                    }

                    if (structuredOutput != null) {
                        return OpenAiRecommendationApiResult
                                .invalidResponse(
                                        httpStatusCode
                                );
                    }

                    structuredOutput =
                            outputText;
                }
            }

            if (structuredOutput == null) {
                return OpenAiRecommendationApiResult
                        .invalidResponse(
                                httpStatusCode
                        );
            }

            RiskRemediationAiOutput output =
                    JSON_MAPPER.readValue(
                            structuredOutput,
                            RiskRemediationAiOutput.class
                    );

            return OpenAiRecommendationApiResult.success(
                    output,
                    httpStatusCode
            );

        } catch (JacksonException exception) {
            return OpenAiRecommendationApiResult
                    .invalidResponse(
                            httpStatusCode
                    );
        }
    }

    private boolean isCompletedResponse(
            JsonNode rootNode
    ) {
        if (rootNode == null
                || !rootNode.isObject()) {

            return false;
        }

        String responseStatus =
                readText(
                        rootNode,
                        "status"
                );

        if (!COMPLETED_STATUS.equals(
                responseStatus
        )) {
            return false;
        }

        JsonNode errorNode =
                rootNode.get("error");

        return errorNode == null
                || errorNode.isNull();
    }

    private boolean isMessageOutput(
            JsonNode outputItem
    ) {
        if (outputItem == null
                || !outputItem.isObject()) {

            return false;
        }

        return MESSAGE_OUTPUT_TYPE.equals(
                readText(
                        outputItem,
                        "type"
                )
        );
    }

    private String readText(
            JsonNode parentNode,
            String fieldName
    ) {
        if (parentNode == null
                || !parentNode.isObject()) {

            return null;
        }

        JsonNode fieldNode =
                parentNode.get(
                        fieldName
                );

        if (fieldNode == null
                || !fieldNode.isTextual()) {

            return null;
        }

        return fieldNode.textValue();
    }

    private void requireSuccessfulHttpStatus(
            int httpStatusCode
    ) {
        if (httpStatusCode < 200
                || httpStatusCode > 299) {

            throw new IllegalArgumentException(
                    "OpenAI response parser requires "
                            + "a 2xx HTTP status code."
            );
        }
    }
}