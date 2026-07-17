package com.cigabyte.sitesentinel.recommendation;

import java.util.Objects;
import java.util.Optional;

public final class RiskRemediationAiProviderResult {

    private final RiskRemediationAiProviderStatus status;

    private final RiskRemediationAiOutput output;

    private RiskRemediationAiProviderResult(
            RiskRemediationAiProviderStatus status,
            RiskRemediationAiOutput output
    ) {
        this.status = Objects.requireNonNull(
                status,
                "AI provider status is required."
        );

        validateState(status, output);

        this.output = output;
    }

    public static RiskRemediationAiProviderResult success(
            RiskRemediationAiOutput output
    ) {
        return new RiskRemediationAiProviderResult(
                RiskRemediationAiProviderStatus.SUCCESS,
                Objects.requireNonNull(
                        output,
                        "Successful AI provider result requires output."
                )
        );
    }

    public static RiskRemediationAiProviderResult unavailable() {
        return new RiskRemediationAiProviderResult(
                RiskRemediationAiProviderStatus.UNAVAILABLE,
                null
        );
    }

    public static RiskRemediationAiProviderResult failure() {
        return new RiskRemediationAiProviderResult(
                RiskRemediationAiProviderStatus.FAILURE,
                null
        );
    }

    private void validateState(
            RiskRemediationAiProviderStatus status,
            RiskRemediationAiOutput output
    ) {
        if (status
                == RiskRemediationAiProviderStatus.SUCCESS
                && output == null) {

            throw new IllegalArgumentException(
                    "Successful AI provider result requires output."
            );
        }

        if (status
                != RiskRemediationAiProviderStatus.SUCCESS
                && output != null) {

            throw new IllegalArgumentException(
                    "Unsuccessful AI provider result must not contain output."
            );
        }
    }

    public RiskRemediationAiProviderStatus getStatus() {
        return status;
    }

    public Optional<RiskRemediationAiOutput> getOutput() {
        return Optional.ofNullable(output);
    }

    public boolean isSuccessful() {
        return status
                == RiskRemediationAiProviderStatus.SUCCESS;
    }
}