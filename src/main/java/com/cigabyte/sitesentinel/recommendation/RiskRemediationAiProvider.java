package com.cigabyte.sitesentinel.recommendation;

public interface RiskRemediationAiProvider {

    String getProviderName();

    String getModelName();

    boolean isAvailable();

    RiskRemediationAiProviderResult generate(
            RiskRemediationAiRequest request
    );
}