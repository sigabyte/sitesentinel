package com.cigabyte.sitesentinel.recommendation.openai;

import com.cigabyte.sitesentinel.recommendation
        .RiskRemediationAiRequest;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenAiRecommendationRequestBodyFactoryTests {

    private static final JsonMapper JSON_MAPPER =
            JsonMapper.builder().build();

    private static final String TEST_API_KEY =
            "test-openai-api-key";

    private static final String CONTEXT_FINGERPRINT =
            "a".repeat(64);

    @Test
    void requestFieldsAreMappedToResponsesApiPayload()
            throws JacksonException {

        OpenAiRecommendationProperties properties =
                configuredProperties();

        OpenAiRecommendationRequestBodyFactory factory =
                new OpenAiRecommendationRequestBodyFactory(
                        properties
                );

        String requestBody =
                factory.create(
                        validRequest()
                );

        JsonNode rootNode =
                JSON_MAPPER.readTree(
                        requestBody
                );

        assertAll(
                () -> assertEquals(
                        "gpt-5.6-terra",
                        rootNode.get("model")
                                .textValue()
                ),
                () -> assertEquals(
                        "Generate one advisory remediation "
                                + "recommendation.",
                        rootNode.get("instructions")
                                .textValue()
                ),
                () -> assertEquals(
                        "Use only the supplied risk context.",
                        rootNode.get("input")
                                .textValue()
                ),
                () -> assertEquals(
                        1500,
                        rootNode.get("max_output_tokens")
                                .intValue()
                ),
                () -> assertFalse(
                        rootNode.get("store")
                                .booleanValue()
                )
        );
    }

    @Test
    void strictStructuredOutputSchemaContainsRequiredFields()
            throws JacksonException {

        JsonNode formatNode =
                createFormatNode();

        JsonNode schemaNode =
                formatNode.get("schema");

        assertAll(
                () -> assertEquals(
                        "json_schema",
                        formatNode.get("type")
                                .textValue()
                ),
                () -> assertEquals(
                        "risk_remediation_recommendation",
                        formatNode.get("name")
                                .textValue()
                ),
                () -> assertTrue(
                        formatNode.get("strict")
                                .booleanValue()
                ),
                () -> assertEquals(
                        "object",
                        schemaNode.get("type")
                                .textValue()
                ),
                () -> assertFalse(
                        schemaNode.get("additionalProperties")
                                .booleanValue()
                ),
                () -> assertEquals(
                        List.of(
                                "schemaVersion",
                                "title",
                                "summary",
                                "remediationSteps",
                                "verificationSteps",
                                "advisory"
                        ),
                        readTextArray(
                                schemaNode.get("required")
                        )
                )
        );
    }

    @Test
    void schemaRestrictsVersionAndAdvisoryValue()
            throws JacksonException {

        JsonNode propertiesNode =
                createSchemaPropertiesNode();

        JsonNode schemaVersionNode =
                propertiesNode.get(
                        "schemaVersion"
                );

        JsonNode advisoryNode =
                propertiesNode.get(
                        "advisory"
                );

        assertAll(
                () -> assertEquals(
                        "string",
                        schemaVersionNode.get("type")
                                .textValue()
                ),
                () -> assertEquals(
                        List.of("1"),
                        readTextArray(
                                schemaVersionNode.get("enum")
                        )
                ),
                () -> assertEquals(
                        "boolean",
                        advisoryNode.get("type")
                                .textValue()
                ),
                () -> assertEquals(
                        1,
                        advisoryNode.get("enum")
                                .size()
                ),
                () -> assertTrue(
                        advisoryNode.get("enum")
                                .get(0)
                                .booleanValue()
                )
        );
    }

    @Test
    void schemaAppliesRecommendationValidationBounds()
            throws JacksonException {

        JsonNode propertiesNode =
                createSchemaPropertiesNode();

        JsonNode titleNode =
                propertiesNode.get("title");

        JsonNode summaryNode =
                propertiesNode.get("summary");

        JsonNode remediationStepsNode =
                propertiesNode.get(
                        "remediationSteps"
                );

        JsonNode verificationStepsNode =
                propertiesNode.get(
                        "verificationSteps"
                );

        assertAll(
                () -> assertStringBounds(
                        titleNode,
                        220
                ),
                () -> assertStringBounds(
                        summaryNode,
                        3000
                ),
                () -> assertStepListBounds(
                        remediationStepsNode
                ),
                () -> assertStepListBounds(
                        verificationStepsNode
                )
        );
    }

    @Test
    void sensitiveConfigurationAndInternalMetadataAreNotSerialized() {
        OpenAiRecommendationProperties properties =
                configuredProperties();

        OpenAiRecommendationRequestBodyFactory factory =
                new OpenAiRecommendationRequestBodyFactory(
                        properties
                );

        String requestBody =
                factory.create(
                        validRequest()
                );

        assertAll(
                () -> assertFalse(
                        requestBody.contains(
                                TEST_API_KEY
                        )
                ),
                () -> assertFalse(
                        requestBody.contains(
                                CONTEXT_FINGERPRINT
                        )
                ),
                () -> assertFalse(
                        requestBody.contains(
                                "prompt-v1"
                        )
                ),
                () -> assertFalse(
                        requestBody.contains(
                                "apiKey"
                        )
                )
        );
    }

    @Test
    void nullRequestIsRejected() {
        OpenAiRecommendationRequestBodyFactory factory =
                new OpenAiRecommendationRequestBodyFactory(
                        configuredProperties()
                );

        assertThrows(
                NullPointerException.class,
                () -> factory.create(
                        null
                )
        );
    }

    private JsonNode createFormatNode()
            throws JacksonException {

        JsonNode rootNode =
                createRootNode();

        return rootNode
                .get("text")
                .get("format");
    }

    private JsonNode createSchemaPropertiesNode()
            throws JacksonException {

        return createFormatNode()
                .get("schema")
                .get("properties");
    }

    private JsonNode createRootNode()
            throws JacksonException {

        OpenAiRecommendationRequestBodyFactory factory =
                new OpenAiRecommendationRequestBodyFactory(
                        configuredProperties()
                );

        String requestBody =
                factory.create(
                        validRequest()
                );

        return JSON_MAPPER.readTree(
                requestBody
        );
    }

    private OpenAiRecommendationProperties
    configuredProperties() {

        OpenAiRecommendationProperties properties =
                new OpenAiRecommendationProperties();

        properties.setApiKey(
                TEST_API_KEY
        );

        properties.setModel(
                "gpt-5.6-terra"
        );

        properties.setMaxOutputTokens(
                1500
        );

        return properties;
    }

    private RiskRemediationAiRequest validRequest() {
        return new RiskRemediationAiRequest(
                "prompt-v1",
                "1",
                "Generate one advisory remediation "
                        + "recommendation.",
                "Use only the supplied risk context.",
                CONTEXT_FINGERPRINT
        );
    }

    private List<String> readTextArray(
            JsonNode arrayNode
    ) {
        List<String> values =
                new ArrayList<>();

        for (JsonNode itemNode : arrayNode) {
            values.add(
                    itemNode.textValue()
            );
        }

        return values;
    }

    private void assertStringBounds(
            JsonNode propertyNode,
            int expectedMaximumLength
    ) {
        assertAll(
                () -> assertEquals(
                        "string",
                        propertyNode.get("type")
                                .textValue()
                ),
                () -> assertEquals(
                        1,
                        propertyNode.get("minLength")
                                .intValue()
                ),
                () -> assertEquals(
                        expectedMaximumLength,
                        propertyNode.get("maxLength")
                                .intValue()
                )
        );
    }

    private void assertStepListBounds(
            JsonNode propertyNode
    ) {
        JsonNode itemNode =
                propertyNode.get("items");

        assertAll(
                () -> assertEquals(
                        "array",
                        propertyNode.get("type")
                                .textValue()
                ),
                () -> assertEquals(
                        1,
                        propertyNode.get("minItems")
                                .intValue()
                ),
                () -> assertEquals(
                        12,
                        propertyNode.get("maxItems")
                                .intValue()
                ),
                () -> assertEquals(
                        "string",
                        itemNode.get("type")
                                .textValue()
                ),
                () -> assertEquals(
                        1,
                        itemNode.get("minLength")
                                .intValue()
                ),
                () -> assertEquals(
                        1200,
                        itemNode.get("maxLength")
                                .intValue()
                )
        );
    }
}