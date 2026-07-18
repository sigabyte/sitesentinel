package com.cigabyte.sitesentinel.recommendation.openai;

public enum OpenAiRecommendationApiStatus {

    SUCCESS,

    REQUEST_REJECTED,

    AUTHENTICATION_FAILED,

    RATE_LIMITED,

    TIMEOUT,

    PROVIDER_UNAVAILABLE,

    INVALID_RESPONSE,

    INTERRUPTED,

    FAILURE
}