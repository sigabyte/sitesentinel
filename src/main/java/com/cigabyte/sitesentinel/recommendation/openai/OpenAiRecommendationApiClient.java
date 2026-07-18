package com.cigabyte.sitesentinel.recommendation.openai;

import com.cigabyte.sitesentinel.recommendation
        .RiskRemediationAiRequest;

public interface OpenAiRecommendationApiClient {

    OpenAiRecommendationApiResult generateRecommendation(
            RiskRemediationAiRequest request
    );
}