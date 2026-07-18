package com.cigabyte.sitesentinel.recommendation.openai;

import com.cigabyte.sitesentinel.recommendation
        .RiskRemediationAiOutput;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenAiRecommendationApiResultTests {

    @Test
    void successfulResultContainsTypedOutputAndHttpStatus() {
        RiskRemediationAiOutput output =
                validOutput();

        OpenAiRecommendationApiResult result =
                OpenAiRecommendationApiResult.success(
                        output,
                        200
                );

        assertAll(
                () -> assertEquals(
                        OpenAiRecommendationApiStatus.SUCCESS,
                        result.getStatus()
                ),
                () -> assertTrue(
                        result.isSuccessful()
                ),
                () -> assertTrue(
                        result.getOutput().isPresent()
                ),
                () -> assertSame(
                        output,
                        result.getOutput().orElseThrow()
                ),
                () -> assertEquals(
                        200,
                        result.getHttpStatusCode()
                )
        );
    }

    @Test
    void requestRejectedResultContainsNoOutput() {
        OpenAiRecommendationApiResult result =
                OpenAiRecommendationApiResult
                        .requestRejected(
                                400
                        );

        assertAll(
                () -> assertEquals(
                        OpenAiRecommendationApiStatus
                                .REQUEST_REJECTED,
                        result.getStatus()
                ),
                () -> assertFalse(
                        result.isSuccessful()
                ),
                () -> assertTrue(
                        result.getOutput().isEmpty()
                ),
                () -> assertEquals(
                        400,
                        result.getHttpStatusCode()
                )
        );
    }

    @Test
    void providerFailureFactoriesUseExpectedStatuses() {
        OpenAiRecommendationApiResult authentication =
                OpenAiRecommendationApiResult
                        .authenticationFailed(
                                401
                        );

        OpenAiRecommendationApiResult rateLimited =
                OpenAiRecommendationApiResult
                        .rateLimited(
                                429
                        );

        OpenAiRecommendationApiResult timeout =
                OpenAiRecommendationApiResult
                        .timeout();

        OpenAiRecommendationApiResult unavailable =
                OpenAiRecommendationApiResult
                        .providerUnavailable(
                                503
                        );

        OpenAiRecommendationApiResult invalidResponse =
                OpenAiRecommendationApiResult
                        .invalidResponse(
                                200
                        );

        OpenAiRecommendationApiResult interrupted =
                OpenAiRecommendationApiResult
                        .interrupted();

        OpenAiRecommendationApiResult failure =
                OpenAiRecommendationApiResult
                        .failure();

        assertAll(
                () -> assertEquals(
                        OpenAiRecommendationApiStatus
                                .AUTHENTICATION_FAILED,
                        authentication.getStatus()
                ),
                () -> assertEquals(
                        OpenAiRecommendationApiStatus
                                .RATE_LIMITED,
                        rateLimited.getStatus()
                ),
                () -> assertEquals(
                        OpenAiRecommendationApiStatus.TIMEOUT,
                        timeout.getStatus()
                ),
                () -> assertEquals(
                        OpenAiRecommendationApiStatus
                                .PROVIDER_UNAVAILABLE,
                        unavailable.getStatus()
                ),
                () -> assertEquals(
                        OpenAiRecommendationApiStatus
                                .INVALID_RESPONSE,
                        invalidResponse.getStatus()
                ),
                () -> assertEquals(
                        OpenAiRecommendationApiStatus
                                .INTERRUPTED,
                        interrupted.getStatus()
                ),
                () -> assertEquals(
                        OpenAiRecommendationApiStatus.FAILURE,
                        failure.getStatus()
                ),
                () -> assertTrue(
                        authentication.getOutput().isEmpty()
                ),
                () -> assertTrue(
                        rateLimited.getOutput().isEmpty()
                ),
                () -> assertTrue(
                        timeout.getOutput().isEmpty()
                ),
                () -> assertTrue(
                        unavailable.getOutput().isEmpty()
                ),
                () -> assertTrue(
                        invalidResponse.getOutput().isEmpty()
                ),
                () -> assertTrue(
                        interrupted.getOutput().isEmpty()
                ),
                () -> assertTrue(
                        failure.getOutput().isEmpty()
                )
        );
    }

    @Test
    void successRejectsMissingOutput() {
        assertThrows(
                NullPointerException.class,
                () -> OpenAiRecommendationApiResult.success(
                        null,
                        200
                )
        );
    }

    @Test
    void successRejectsNonSuccessfulHttpStatus() {
        assertThrows(
                IllegalArgumentException.class,
                () -> OpenAiRecommendationApiResult.success(
                        validOutput(),
                        400
                )
        );
    }

    @Test
    void resultRejectsHttpStatusOutsideValidRange() {
        assertAll(
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> OpenAiRecommendationApiResult
                                .requestRejected(
                                        99
                                )
                ),
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> OpenAiRecommendationApiResult
                                .providerUnavailable(
                                        600
                                )
                )
        );
    }

    private RiskRemediationAiOutput validOutput() {
        return new RiskRemediationAiOutput(
                "1",
                "Restore monitored service availability",
                "The monitored endpoint is unavailable.",
                List.of(
                        "Confirm application process health.",
                        "Review recent deployment changes."
                ),
                List.of(
                        "Repeat the monitoring run.",
                        "Confirm successful HTTP response."
                ),
                true
        );
    }
}