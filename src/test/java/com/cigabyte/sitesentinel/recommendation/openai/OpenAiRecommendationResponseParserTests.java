package com.cigabyte.sitesentinel.recommendation.openai;

import com.cigabyte.sitesentinel.recommendation
        .RiskRemediationAiOutput;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenAiRecommendationResponseParserTests {

    private static final JsonMapper JSON_MAPPER =
            JsonMapper.builder().build();

    private final OpenAiRecommendationResponseParser parser =
            new OpenAiRecommendationResponseParser();

    @Test
    void completedResponseReturnsTypedRecommendation()
            throws JacksonException {

        RiskRemediationAiOutput expectedOutput =
                validOutput();

        String structuredOutput =
                JSON_MAPPER.writeValueAsString(
                        expectedOutput
                );

        String responseBody =
                createResponseBody(
                        "completed",
                        List.of(
                                outputTextContent(
                                        structuredOutput
                                )
                        )
                );

        OpenAiRecommendationApiResult result =
                parser.parseSuccessfulResponse(
                        200,
                        responseBody
                );

        RiskRemediationAiOutput actualOutput =
                result.getOutput().orElseThrow();

        assertAll(
                () -> assertTrue(
                        result.isSuccessful()
                ),
                () -> assertEquals(
                        OpenAiRecommendationApiStatus.SUCCESS,
                        result.getStatus()
                ),
                () -> assertEquals(
                        200,
                        result.getHttpStatusCode()
                ),
                () -> assertEquals(
                        expectedOutput,
                        actualOutput
                )
        );
    }

    @Test
    void refusalResponseIsClassifiedAsRequestRejected()
            throws JacksonException {

        String responseBody =
                createResponseBody(
                        "completed",
                        List.of(
                                Map.of(
                                        "type",
                                        "refusal",
                                        "refusal",
                                        "Request refused."
                                )
                        )
                );

        OpenAiRecommendationApiResult result =
                parser.parseSuccessfulResponse(
                        200,
                        responseBody
                );

        assertAll(
                () -> assertFalse(
                        result.isSuccessful()
                ),
                () -> assertEquals(
                        OpenAiRecommendationApiStatus
                                .REQUEST_REJECTED,
                        result.getStatus()
                ),
                () -> assertTrue(
                        result.getOutput().isEmpty()
                ),
                () -> assertEquals(
                        200,
                        result.getHttpStatusCode()
                )
        );
    }

    @Test
    void incompleteResponseIsRejected()
            throws JacksonException {

        String structuredOutput =
                JSON_MAPPER.writeValueAsString(
                        validOutput()
                );

        String responseBody =
                createResponseBody(
                        "incomplete",
                        List.of(
                                outputTextContent(
                                        structuredOutput
                                )
                        )
                );

        OpenAiRecommendationApiResult result =
                parser.parseSuccessfulResponse(
                        200,
                        responseBody
                );

        assertInvalidResponse(result);
    }

    @Test
    void malformedResponseBodyIsRejected() {
        OpenAiRecommendationApiResult result =
                parser.parseSuccessfulResponse(
                        200,
                        "{invalid-json"
                );

        assertInvalidResponse(result);
    }

    @Test
    void blankResponseBodyIsRejected() {
        OpenAiRecommendationApiResult result =
                parser.parseSuccessfulResponse(
                        200,
                        "   "
                );

        assertInvalidResponse(result);
    }

    @Test
    void completedResponseWithoutOutputIsRejected()
            throws JacksonException {

        String responseBody =
                JSON_MAPPER.writeValueAsString(
                        Map.of(
                                "status",
                                "completed"
                        )
                );

        OpenAiRecommendationApiResult result =
                parser.parseSuccessfulResponse(
                        200,
                        responseBody
                );

        assertInvalidResponse(result);
    }

    @Test
    void multipleOutputTextItemsAreRejected()
            throws JacksonException {

        String structuredOutput =
                JSON_MAPPER.writeValueAsString(
                        validOutput()
                );

        String responseBody =
                createResponseBody(
                        "completed",
                        List.of(
                                outputTextContent(
                                        structuredOutput
                                ),
                                outputTextContent(
                                        structuredOutput
                                )
                        )
                );

        OpenAiRecommendationApiResult result =
                parser.parseSuccessfulResponse(
                        200,
                        responseBody
                );

        assertInvalidResponse(result);
    }

    @Test
    void nonSuccessfulHttpStatusIsRejectedBeforeParsing() {
        assertAll(
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> parser.parseSuccessfulResponse(
                                199,
                                "{}"
                        )
                ),
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> parser.parseSuccessfulResponse(
                                400,
                                "{}"
                        )
                )
        );
    }

    private String createResponseBody(
            String status,
            List<Map<String, Object>> content
    ) throws JacksonException {

        Map<String, Object> messageOutput =
                new LinkedHashMap<>();

        messageOutput.put(
                "type",
                "message"
        );

        messageOutput.put(
                "content",
                content
        );

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "status",
                status
        );

        response.put(
                "output",
                List.of(
                        messageOutput
                )
        );

        return JSON_MAPPER.writeValueAsString(
                response
        );
    }

    private Map<String, Object> outputTextContent(
            String structuredOutput
    ) {
        return Map.of(
                "type",
                "output_text",
                "text",
                structuredOutput
        );
    }

    private void assertInvalidResponse(
            OpenAiRecommendationApiResult result
    ) {
        assertAll(
                () -> assertFalse(
                        result.isSuccessful()
                ),
                () -> assertEquals(
                        OpenAiRecommendationApiStatus
                                .INVALID_RESPONSE,
                        result.getStatus()
                ),
                () -> assertTrue(
                        result.getOutput().isEmpty()
                ),
                () -> assertEquals(
                        200,
                        result.getHttpStatusCode()
                )
        );
    }

    private RiskRemediationAiOutput validOutput() {
        return new RiskRemediationAiOutput(
                "1",
                "Restore monitored service availability",
                "The monitored endpoint is unavailable.",
                List.of(
                        "Confirm that the application process is running.",
                        "Review recent deployment and configuration changes."
                ),
                List.of(
                        "Repeat the SiteSentinel monitoring run.",
                        "Confirm that the endpoint returns a successful response."
                ),
                true
        );
    }
}