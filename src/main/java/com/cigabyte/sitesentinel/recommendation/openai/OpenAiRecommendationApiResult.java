package com.cigabyte.sitesentinel.recommendation.openai;

import com.cigabyte.sitesentinel.recommendation
        .RiskRemediationAiOutput;

import java.util.Objects;
import java.util.Optional;

public final class OpenAiRecommendationApiResult {

    private final OpenAiRecommendationApiStatus status;

    private final RiskRemediationAiOutput output;

    private final Integer httpStatusCode;

    private OpenAiRecommendationApiResult(
            OpenAiRecommendationApiStatus status,
            RiskRemediationAiOutput output,
            Integer httpStatusCode
    ) {
        this.status =
                Objects.requireNonNull(
                        status,
                        "OpenAI recommendation API status is required."
                );

        this.output = output;

        this.httpStatusCode =
                validateHttpStatusCode(
                        httpStatusCode
                );

        validateState();
    }

    public static OpenAiRecommendationApiResult success(
            RiskRemediationAiOutput output,
            int httpStatusCode
    ) {
        return new OpenAiRecommendationApiResult(
                OpenAiRecommendationApiStatus.SUCCESS,
                Objects.requireNonNull(
                        output,
                        "Successful OpenAI response requires output."
                ),
                httpStatusCode
        );
    }

    public static OpenAiRecommendationApiResult
    requestRejected(
            int httpStatusCode
    ) {
        return unsuccessful(
                OpenAiRecommendationApiStatus
                        .REQUEST_REJECTED,
                httpStatusCode
        );
    }

    public static OpenAiRecommendationApiResult
    authenticationFailed(
            int httpStatusCode
    ) {
        return unsuccessful(
                OpenAiRecommendationApiStatus
                        .AUTHENTICATION_FAILED,
                httpStatusCode
        );
    }

    public static OpenAiRecommendationApiResult rateLimited(
            int httpStatusCode
    ) {
        return unsuccessful(
                OpenAiRecommendationApiStatus.RATE_LIMITED,
                httpStatusCode
        );
    }

    public static OpenAiRecommendationApiResult timeout() {
        return unsuccessful(
                OpenAiRecommendationApiStatus.TIMEOUT,
                null
        );
    }

    public static OpenAiRecommendationApiResult
    providerUnavailable(
            int httpStatusCode
    ) {
        return unsuccessful(
                OpenAiRecommendationApiStatus
                        .PROVIDER_UNAVAILABLE,
                httpStatusCode
        );
    }

    public static OpenAiRecommendationApiResult
    invalidResponse(
            Integer httpStatusCode
    ) {
        return unsuccessful(
                OpenAiRecommendationApiStatus
                        .INVALID_RESPONSE,
                httpStatusCode
        );
    }

    public static OpenAiRecommendationApiResult interrupted() {
        return unsuccessful(
                OpenAiRecommendationApiStatus.INTERRUPTED,
                null
        );
    }

    public static OpenAiRecommendationApiResult failure() {
        return unsuccessful(
                OpenAiRecommendationApiStatus.FAILURE,
                null
        );
    }

    private static OpenAiRecommendationApiResult unsuccessful(
            OpenAiRecommendationApiStatus status,
            Integer httpStatusCode
    ) {
        if (status == OpenAiRecommendationApiStatus.SUCCESS) {
            throw new IllegalArgumentException(
                    "Unsuccessful result cannot have SUCCESS status."
            );
        }

        return new OpenAiRecommendationApiResult(
                status,
                null,
                httpStatusCode
        );
    }

    private Integer validateHttpStatusCode(
            Integer value
    ) {
        if (value == null) {
            return null;
        }

        if (value < 100 || value > 599) {
            throw new IllegalArgumentException(
                    "HTTP status code must be between 100 and 599."
            );
        }

        return value;
    }

    private void validateState() {
        if (status == OpenAiRecommendationApiStatus.SUCCESS) {
            if (output == null) {
                throw new IllegalArgumentException(
                        "Successful OpenAI response requires output."
                );
            }

            if (httpStatusCode == null
                    || httpStatusCode < 200
                    || httpStatusCode > 299) {

                throw new IllegalArgumentException(
                        "Successful OpenAI response requires "
                                + "a 2xx HTTP status code."
                );
            }

            return;
        }

        if (output != null) {
            throw new IllegalArgumentException(
                    "Unsuccessful OpenAI response "
                            + "must not contain output."
            );
        }
    }

    public OpenAiRecommendationApiStatus getStatus() {
        return status;
    }

    public Optional<RiskRemediationAiOutput> getOutput() {
        return Optional.ofNullable(output);
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public boolean isSuccessful() {
        return status
                == OpenAiRecommendationApiStatus.SUCCESS;
    }
}