package com.cigabyte.sitesentinel.recommendation.openai;

import com.cigabyte.sitesentinel.recommendation
        .RiskRemediationAiOutput;
import com.cigabyte.sitesentinel.recommendation
        .RiskRemediationAiProvider;
import com.cigabyte.sitesentinel.recommendation
        .RiskRemediationAiProviderResult;
import com.cigabyte.sitesentinel.recommendation
        .RiskRemediationAiRequest;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class OpenAiRiskRemediationAiProvider
        implements RiskRemediationAiProvider {

    private static final String PROVIDER_NAME =
            "OpenAI";

    private final OpenAiRecommendationProperties properties;

    private final OpenAiRecommendationApiClient apiClient;

    public OpenAiRiskRemediationAiProvider(
            OpenAiRecommendationProperties properties,
            OpenAiRecommendationApiClient apiClient
    ) {
        this.properties =
                Objects.requireNonNull(
                        properties,
                        "OpenAI recommendation properties "
                                + "are required."
                );

        this.apiClient =
                Objects.requireNonNull(
                        apiClient,
                        "OpenAI recommendation API client "
                                + "is required."
                );
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public String getModelName() {
        return properties.getModel();
    }

    @Override
    public boolean isAvailable() {
        return properties.isReady();
    }

    @Override
    public RiskRemediationAiProviderResult generate(
            RiskRemediationAiRequest request
    ) {
        RiskRemediationAiRequest requiredRequest =
                Objects.requireNonNull(
                        request,
                        "Risk remediation AI request "
                                + "is required."
                );

        if (!isAvailable()) {
            return RiskRemediationAiProviderResult
                    .unavailable();
        }

        OpenAiRecommendationApiResult apiResult;

        try {
            apiResult =
                    apiClient.generateRecommendation(
                            requiredRequest
                    );
        } catch (RuntimeException exception) {
            return RiskRemediationAiProviderResult
                    .failure();
        }

        if (apiResult == null) {
            return RiskRemediationAiProviderResult
                    .failure();
        }

        if (apiResult.getStatus()
                != OpenAiRecommendationApiStatus.SUCCESS) {

            return RiskRemediationAiProviderResult
                    .failure();
        }

        RiskRemediationAiOutput output =
                apiResult.getOutput()
                        .orElse(null);

        if (output == null) {
            return RiskRemediationAiProviderResult
                    .failure();
        }

        return RiskRemediationAiProviderResult
                .success(
                        output
                );
    }
}