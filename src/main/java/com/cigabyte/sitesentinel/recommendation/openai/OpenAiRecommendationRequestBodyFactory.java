package com.cigabyte.sitesentinel.recommendation.openai;

import com.cigabyte.sitesentinel.recommendation
        .RiskRemediationAiRequest;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class OpenAiRecommendationRequestBodyFactory {

    private static final JsonMapper JSON_MAPPER =
            JsonMapper.builder().build();

    private static final String RESPONSE_FORMAT_NAME =
            "risk_remediation_recommendation";

    private static final int TITLE_MAX_LENGTH =
            220;

    private static final int SUMMARY_MAX_LENGTH =
            3000;

    private static final int STEP_MAX_LENGTH =
            1200;

    private static final int STEP_MAX_COUNT =
            12;

    private final OpenAiRecommendationProperties properties;

    public OpenAiRecommendationRequestBodyFactory(
            OpenAiRecommendationProperties properties
    ) {
        this.properties =
                Objects.requireNonNull(
                        properties,
                        "OpenAI recommendation properties "
                                + "are required."
                );
    }

    public String create(
            RiskRemediationAiRequest request
    ) {
        RiskRemediationAiRequest requiredRequest =
                Objects.requireNonNull(
                        request,
                        "Risk remediation AI request "
                                + "is required."
                );

        Map<String, Object> requestPayload =
                new LinkedHashMap<>();

        requestPayload.put(
                "model",
                properties.getModel()
        );

        requestPayload.put(
                "instructions",
                requiredRequest.systemInstruction()
        );

        requestPayload.put(
                "input",
                requiredRequest.userInstruction()
        );

        requestPayload.put(
                "max_output_tokens",
                properties.getMaxOutputTokens()
        );

        requestPayload.put(
                "store",
                false
        );

        requestPayload.put(
                "text",
                createTextConfiguration(
                        requiredRequest
                                .outputSchemaVersion()
                )
        );

        return serialize(requestPayload);
    }

    private Map<String, Object> createTextConfiguration(
            String outputSchemaVersion
    ) {
        Map<String, Object> format =
                new LinkedHashMap<>();

        format.put(
                "type",
                "json_schema"
        );

        format.put(
                "name",
                RESPONSE_FORMAT_NAME
        );

        format.put(
                "description",
                "One advisory remediation recommendation "
                        + "for a persisted SiteSentinel risk."
        );

        format.put(
                "strict",
                true
        );

        format.put(
                "schema",
                createOutputSchema(
                        outputSchemaVersion
                )
        );

        Map<String, Object> text =
                new LinkedHashMap<>();

        text.put(
                "format",
                format
        );

        return text;
    }

    private Map<String, Object> createOutputSchema(
            String outputSchemaVersion
    ) {
        Map<String, Object> schemaProperties =
                new LinkedHashMap<>();

        schemaProperties.put(
                "schemaVersion",
                createSchemaVersionProperty(
                        outputSchemaVersion
                )
        );

        schemaProperties.put(
                "title",
                createStringProperty(
                        TITLE_MAX_LENGTH
                )
        );

        schemaProperties.put(
                "summary",
                createStringProperty(
                        SUMMARY_MAX_LENGTH
                )
        );

        schemaProperties.put(
                "remediationSteps",
                createStepListProperty()
        );

        schemaProperties.put(
                "verificationSteps",
                createStepListProperty()
        );

        schemaProperties.put(
                "advisory",
                createAdvisoryProperty()
        );

        Map<String, Object> schema =
                new LinkedHashMap<>();

        schema.put(
                "type",
                "object"
        );

        schema.put(
                "properties",
                schemaProperties
        );

        schema.put(
                "required",
                List.of(
                        "schemaVersion",
                        "title",
                        "summary",
                        "remediationSteps",
                        "verificationSteps",
                        "advisory"
                )
        );

        schema.put(
                "additionalProperties",
                false
        );

        return schema;
    }

    private Map<String, Object>
    createSchemaVersionProperty(
            String outputSchemaVersion
    ) {
        Map<String, Object> property =
                new LinkedHashMap<>();

        property.put(
                "type",
                "string"
        );

        property.put(
                "enum",
                List.of(
                        outputSchemaVersion
                )
        );

        return property;
    }

    private Map<String, Object> createStringProperty(
            int maximumLength
    ) {
        Map<String, Object> property =
                new LinkedHashMap<>();

        property.put(
                "type",
                "string"
        );

        property.put(
                "minLength",
                1
        );

        property.put(
                "maxLength",
                maximumLength
        );

        return property;
    }

    private Map<String, Object> createStepListProperty() {
        Map<String, Object> itemSchema =
                new LinkedHashMap<>();

        itemSchema.put(
                "type",
                "string"
        );

        itemSchema.put(
                "minLength",
                1
        );

        itemSchema.put(
                "maxLength",
                STEP_MAX_LENGTH
        );

        Map<String, Object> property =
                new LinkedHashMap<>();

        property.put(
                "type",
                "array"
        );

        property.put(
                "items",
                itemSchema
        );

        property.put(
                "minItems",
                1
        );

        property.put(
                "maxItems",
                STEP_MAX_COUNT
        );

        return property;
    }

    private Map<String, Object> createAdvisoryProperty() {
        Map<String, Object> property =
                new LinkedHashMap<>();

        property.put(
                "type",
                "boolean"
        );

        property.put(
                "enum",
                List.of(true)
        );

        return property;
    }

    private String serialize(
            Map<String, Object> requestPayload
    ) {
        try {
            return JSON_MAPPER.writeValueAsString(
                    requestPayload
            );
        } catch (JacksonException exception) {
            throw new IllegalStateException(
                    "OpenAI recommendation request body "
                            + "could not be serialized."
            );
        }
    }
}